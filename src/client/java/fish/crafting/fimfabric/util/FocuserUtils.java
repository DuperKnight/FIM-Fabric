package fish.crafting.fimfabric.util;

import com.sun.jna.platform.win32.WinDef;
import fish.crafting.fimfabric.connection.focuser.FocuserType;

public class FocuserUtils {

    public static void allowForegroundSetting(long pid){
        FocuserType current = FocuserType.getCurrent();
        if(current == FocuserType.WINDOWS){
            User32Ex.INSTANCE.AllowSetForegroundWindow(new WinDef.DWORD((int) pid));
        }
    }

}
