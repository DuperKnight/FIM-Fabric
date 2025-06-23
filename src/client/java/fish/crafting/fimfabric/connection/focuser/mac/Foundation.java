package fish.crafting.fimfabric.connection.focuser.mac;

import com.sun.jna.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public final @NonNls class Foundation {
    private static final FoundationLibrary myFoundationLibrary;
    private static final Function myObjcMsgSend;

    static {
        assert JnaLoader.isLoaded() : "JNA library is not available";
        myFoundationLibrary = Native.load("Foundation", FoundationLibrary.class, Collections.singletonMap("jna.encoding", "UTF8"));
        NativeLibrary nativeLibrary = ((Library.Handler) Proxy.getInvocationHandler(myFoundationLibrary)).getNativeLibrary();
        myObjcMsgSend = nativeLibrary.getFunction("objc_msgSend");
    }

    public static void init() { /* fake method to init foundation */ }

    private Foundation() { }

    /**
     * Get the ID of the NSClass with className
     */
    public static ID getObjcClass(String className) {
        return myFoundationLibrary.objc_getClass(className);
    }

    public static Pointer createSelector(String s) {
        return myFoundationLibrary.sel_registerName(s);
    }

    private static Object @NotNull [] prepInvoke(ID id, Pointer selector, Object[] args) {
        Object[] invokArgs = new Object[args.length + 2];
        invokArgs[0] = id;
        invokArgs[1] = selector;
        System.arraycopy(args, 0, invokArgs, 2, args.length);
        return invokArgs;
    }

    public static @NotNull ID invoke(final ID id, final Pointer selector, Object... args) {
        // objc_msgSend is called with the calling convention of the target method
        // on x86_64 this does not make a difference, but arm64 uses a different calling convention for varargs
        // it is therefore important to not call objc_msgSend as a vararg function
        return new ID(myObjcMsgSend.invokeLong(prepInvoke(id, selector, args)));
    }

    public static ID invoke(final String cls, final String selector, Object... args) {
        return invoke(getObjcClass(cls), createSelector(selector), args);
    }

    public static @NotNull ID invoke(final ID id, final String selector, Object... args) {
        return invoke(id, createSelector(selector), args);
    }

    public static ID allocateObjcClassPair(ID superCls, String name) {
        return myFoundationLibrary.objc_allocateClassPair(superCls, name, 0);
    }

    public static void registerObjcClassPair(ID cls) {
        myFoundationLibrary.objc_registerClassPair(cls);
    }

    /**
     * @param cls          The class to which to add a method.
     * @param selectorName A selector that specifies the name of the method being added.
     * @param impl         A function which is the implementation of the new method. The function must take at least two arguments-self and _cmd.
     * @param types        An array of characters that describe the types of the arguments to the method.
     *                     See <a href="https://developer.apple.com/library/IOs/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html#//apple_ref/doc/uid/TP40008048-CH100"></a>
     * @return true if the method was added successfully, otherwise false (for example, the class already contains a method implementation with that name).
     */
    public static boolean addMethod(ID cls, Pointer selectorName, Callback impl, String types) {
        return myFoundationLibrary.class_addMethod(cls, selectorName, impl, types);
    }



    private static final class NSString {
        private static final ID nsStringCls = getObjcClass("NSString");
        private static final Pointer stringSel = createSelector("string");
        private static final Pointer allocSel = createSelector("alloc");
        private static final Pointer autoreleaseSel = createSelector("autorelease");
        private static final Pointer initWithBytesLengthEncodingSel = createSelector("initWithBytes:length:encoding:");
        private static final long nsEncodingUTF16LE = convertCFEncodingToNS(FoundationLibrary.kCFStringEncodingUTF16LE);

        public static @NotNull ID create(@NotNull String s) {
            // Use a byte[] rather than letting jna do the String -> char* marshalling itself.
            // Turns out about 10% quicker for long strings.
            if (s.isEmpty()) {
                return invoke(nsStringCls, stringSel);
            }

            byte[] utf16Bytes = s.getBytes(StandardCharsets.UTF_16LE);
            return create(utf16Bytes);
        }

        public static @NotNull ID create(@NotNull CharSequence cs) {
            if (cs instanceof String s) {
                return create(s);
            }
            if (cs.isEmpty()) {
                return invoke(nsStringCls, stringSel);
            }

            byte[] utf16Bytes = StandardCharsets.UTF_16LE.encode(CharBuffer.wrap(cs)).array();
            return create(utf16Bytes);
        }

        private static @NotNull ID create(byte[] utf16Bytes) {
            ID emptyNsString = invoke(nsStringCls, allocSel);
            ID initializedNsString = invoke(emptyNsString, initWithBytesLengthEncodingSel, utf16Bytes, utf16Bytes.length, nsEncodingUTF16LE);
            return invoke(initializedNsString, autoreleaseSel);
        }
    }

    public static @NotNull ID nsString(@Nullable String s) {
        return s == null ? ID.NIL : NSString.create(s);
    }

    public static @Nullable String toStringViaUTF8(ID cfString) {
        if (ID.NIL.equals(cfString)) return null;

        int lengthInChars = myFoundationLibrary.CFStringGetLength(cfString);
        int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF8 fully escaped 16 bit chars, plus nul

        byte[] buffer = new byte[potentialLengthInBytes];
        byte ok = myFoundationLibrary.CFStringGetCString(cfString, buffer, buffer.length, FoundationLibrary.kCFStringEncodingUTF8);
        if (ok == 0) throw new RuntimeException("Could not convert string");
        return Native.toString(buffer);
    }

    private static long convertCFEncodingToNS(long cfEncoding) {
        return myFoundationLibrary.CFStringConvertEncodingToNSStringEncoding(cfEncoding) & 0xffffffffffL;  // trim to C-type limits
    }


    public static Object[] convertTypes(Object @NotNull [] v) {
        final Object[] result = new Object[v.length + 1];
        for (int i = 0; i < v.length; i++) {
            result[i] = convertType(v[i]);
        }
        result[v.length] = ID.NIL;
        return result;
    }

    private static Object convertType(@NotNull Object o) {
        if (o instanceof Pointer || o instanceof ID) {
            return o;
        }
        else if (o instanceof String) {
            return nsString((String)o);
        }
        else {
            throw new IllegalArgumentException("Unsupported type! " + o.getClass());
        }
    }
}

