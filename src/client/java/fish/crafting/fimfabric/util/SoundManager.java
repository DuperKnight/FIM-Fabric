package fish.crafting.fimfabric.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public class SoundManager {

    public static final SoundEvent SWITCH = register("switch");

    private static SoundEvent register(@NotNull String id){
        return Registry.register(Registries.SOUND_EVENT, ModUtil.identifier(id), SoundEvent.of(ModUtil.identifier(id)));
    }

}
