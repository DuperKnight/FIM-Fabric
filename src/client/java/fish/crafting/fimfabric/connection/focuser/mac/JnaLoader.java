package fish.crafting.fimfabric.connection.focuser.mac;

import com.sun.jna.Native;
import fish.crafting.fimfabric.connection.focuser.FocuserType;


public final class JnaLoader {
    private static Boolean ourJnaLoaded = null;

    public static synchronized void load() {
        if (ourJnaLoaded == null) {
            ourJnaLoaded = Boolean.FALSE;

            if (FocuserType.getCurrent() == FocuserType.WINDOWS && Boolean.getBoolean("ide.native.launcher")) {
                // temporary fix for JNA + `SetDefaultDllDirectories` DLL loading issue (IJPL-157390)
                String winDir = System.getenv("SystemRoot");
                if (winDir != null) {
                    String path = System.getProperty("jna.platform.library.path");
                    path = (path == null ? "" : path + ';') + winDir + "\\System32";
                    System.setProperty("jna.platform.library.path", path);
                }
            }

            try {
                long t = System.currentTimeMillis();
                int ptrSize = Native.POINTER_SIZE;
                t = System.currentTimeMillis() - t;
                ourJnaLoaded = Boolean.TRUE;
            }
            catch (Throwable t) {
            }
        }
    }

    public static synchronized boolean isLoaded() {
        if (ourJnaLoaded == null) {
            load();
        }
        return ourJnaLoaded;
    }
}
