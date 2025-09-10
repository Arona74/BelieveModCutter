package io.arona74.believemodcutter.client;

import io.arona74.believemodcutter.BelieveModCutterMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class BelieveModCutterClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(
            BelieveModCutterMod.BELIEVEMOD_CUTTER_SCREEN_HANDLER,
            BelieveModCutterScreen::new
        );
    }
}
