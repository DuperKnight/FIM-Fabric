package fish.crafting.fimfabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class SoundUtil {

    public static void play(SoundEvent event){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, 1.0f));
    }

    public static void play(SoundEvent event, float pitch){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, pitch));
    }

    public static void clickSound() {
        play(SoundEvents.UI_BUTTON_CLICK.value());
    }
}
