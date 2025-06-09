package fish.crafting.fimfabric.util;

import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface User32Ex extends StdCallLibrary {
    User32Ex INSTANCE = Native.load("user32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean SetForegroundWindow(WinDef.HWND hwnd);
    boolean AllowSetForegroundWindow(WinDef.DWORD pid);

    @ApiStatus.Internal
    boolean EnumWindows(@NotNull EnumThreadWindowsCallback callback, @Nullable WinDef.INT_PTR extraData);

    @ApiStatus.Internal
    boolean GetWindowThreadProcessId(WinDef.HWND handle, IntByReference lpdwProcessId);

    @ApiStatus.Internal
    interface EnumThreadWindowsCallback extends Callback {
        boolean callback(WinDef.HWND hWnd, IntByReference lParam);
    }
}
