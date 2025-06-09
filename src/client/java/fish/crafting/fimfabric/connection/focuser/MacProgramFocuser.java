package fish.crafting.fimfabric.connection.focuser;

import fish.crafting.fimfabric.connection.focuser.mac.Foundation;

public class MacProgramFocuser {

    public static void focus(int pid) {
        var nsRunningApplicationClass = Foundation.getObjcClass("NSRunningApplication");
        var nsApplication = Foundation.invoke(nsRunningApplicationClass, "runningApplicationWithProcessIdentifier:", pid);
        Foundation.invoke(nsApplication, "activateWithOptions:", 1);
    }
}
