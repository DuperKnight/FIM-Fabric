package fish.crafting.fimfabric.connection.focuser;

import io.netty.buffer.ByteBufOutputStream;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;


public enum FocuserType {

    NONE(0),
    WINDOWS(1),
    LINUX(2),
    MAC(3);

    public final int focuserID;

    FocuserType(int focuserID){
        this.focuserID = focuserID;
    }

    public static FocuserType getCurrent() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        }else if(SystemUtils.IS_OS_MAC) {
            return MAC;
        }else if(SystemUtils.IS_OS_LINUX) {
            return LINUX;
        }else{
            return NONE;
        }
    }

    /*
     *  GUIDE FOR WINDOW FOCUSER
     *
     *  VALUES:
     *
     *  SHORT: ID (Dependent on ID, use the next values)
     *  0 (No Focuser):
     *    NONE
     *  1 (Windows Focuser):
     *    INT: PID
     * 2 (Linux Focuser):
     *    INT: PID
     *
     *  todo add support for linux & mac
     *
     */

    public static void writeToStream(ByteBufOutputStream stream) throws IOException {
        var focuser = getCurrent();
        stream.writeShort(focuser.focuserID);

        if (focuser == WINDOWS || focuser == LINUX || focuser == MAC) {
            stream.writeInt((int) ProcessHandle.current().pid());
        }
    }

}