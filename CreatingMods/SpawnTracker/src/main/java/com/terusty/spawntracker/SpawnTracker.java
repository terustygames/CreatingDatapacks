package com.terusty.spawntracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpawnTracker — клиентский мод для 1.21.1 (Fabric).
 * Показывает в реальном времени, могут ли заспавниться фантомы,
 * и что мешает спавну прямо сейчас.
 *
 * Мод только читает данные клиента и рисует HUD — в мир он не вмешивается,
 * поэтому его можно ставить на клиент при игре на ванильном сервере.
 */
@Environment(EnvType.CLIENT)
public class SpawnTracker implements ClientModInitializer {

    public static final String MOD_ID = "spawntracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String KEY_CATEGORY = "key.categories.spawntracker";

    private static KeyBinding toggleKey;
    private static KeyBinding phantomKey;

    private final SpawnTrackerHud hud = new SpawnTrackerHud();

    @Override
    public void onInitializeClient() {
        SpawnTrackerConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spawntracker.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                KEY_CATEGORY
        ));

        phantomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spawntracker.toggle_phantoms",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleKeys(client);
            hud.tick(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) ->
                hud.render(context, MinecraftClient.getInstance()));

        LOGGER.info("SpawnTracker загружен. HUD переключается на F6.");
    }

    private void handleKeys(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            SpawnTrackerConfig.enabled = !SpawnTrackerConfig.enabled;
            SpawnTrackerConfig.save();
            feedback(client, SpawnTrackerConfig.enabled
                    ? "msg.spawntracker.enabled" : "msg.spawntracker.disabled",
                    SpawnTrackerConfig.enabled);
        }

        while (phantomKey.wasPressed()) {
            SpawnTrackerConfig.showPhantoms = !SpawnTrackerConfig.showPhantoms;
            SpawnTrackerConfig.save();
            feedback(client, SpawnTrackerConfig.showPhantoms
                    ? "msg.spawntracker.phantoms_on" : "msg.spawntracker.phantoms_off",
                    SpawnTrackerConfig.showPhantoms);
        }
    }

    private void feedback(MinecraftClient client, String key, boolean on) {
        if (client.player == null) return;
        client.player.sendMessage(
                Text.translatable(key).formatted(on ? Formatting.GREEN : Formatting.GRAY),
                true);
    }
}
