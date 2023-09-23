package org.mcsr.speedrunapi.config.api;

import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

public interface SpeedrunConfigScreenProvider {

    @NotNull Screen createConfigScreen(Screen parent);
}
