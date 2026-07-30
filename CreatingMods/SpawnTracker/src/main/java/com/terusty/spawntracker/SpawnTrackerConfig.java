package com.terusty.spawntracker;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Простой конфиг в .properties — без зависимости от Cloth Config,
 * чтобы мод оставался лёгким.
 *
 * Файл: .minecraft/config/spawntracker.properties
 */
public final class SpawnTrackerConfig {

    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("spawntracker.properties");

    /** Показывать ли HUD (переключается клавишей). */
    public static boolean enabled = true;

    /** Показывать блок про фантомов. */
    public static boolean showPhantoms = true;

    /** Показывать счётчик мобов вокруг. */
    public static boolean showMobCounts = true;

    /** Показывать уровень освещения под ногами. */
    public static boolean showLight = true;

    /** Отступ HUD от края экрана. */
    public static int offsetX = 4;
    public static int offsetY = 4;

    /** Как часто пересчитывать тяжёлые данные, в тиках. */
    public static int refreshRate = 10;

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        Properties p = new Properties();
        try (var in = Files.newInputStream(PATH)) {
            p.load(in);
        } catch (IOException e) {
            SpawnTracker.LOGGER.warn("Не удалось прочитать конфиг, беру значения по умолчанию", e);
            return;
        }
        enabled = bool(p, "enabled", enabled);
        showPhantoms = bool(p, "showPhantoms", showPhantoms);
        showMobCounts = bool(p, "showMobCounts", showMobCounts);
        showLight = bool(p, "showLight", showLight);
        offsetX = integer(p, "offsetX", offsetX);
        offsetY = integer(p, "offsetY", offsetY);
        refreshRate = Math.max(1, integer(p, "refreshRate", refreshRate));
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("enabled", String.valueOf(enabled));
        p.setProperty("showPhantoms", String.valueOf(showPhantoms));
        p.setProperty("showMobCounts", String.valueOf(showMobCounts));
        p.setProperty("showLight", String.valueOf(showLight));
        p.setProperty("offsetX", String.valueOf(offsetX));
        p.setProperty("offsetY", String.valueOf(offsetY));
        p.setProperty("refreshRate", String.valueOf(refreshRate));
        try {
            Files.createDirectories(PATH.getParent());
            try (var out = Files.newOutputStream(PATH)) {
                p.store(out, "SpawnTracker config");
            }
        } catch (IOException e) {
            SpawnTracker.LOGGER.warn("Не удалось сохранить конфиг", e);
        }
    }

    private static boolean bool(Properties p, String key, boolean def) {
        String v = p.getProperty(key);
        return v == null ? def : Boolean.parseBoolean(v.trim());
    }

    private static int integer(Properties p, String key, int def) {
        String v = p.getProperty(key);
        if (v == null) return def;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
