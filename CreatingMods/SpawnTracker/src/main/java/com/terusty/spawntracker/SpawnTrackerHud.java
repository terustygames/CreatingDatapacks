package com.terusty.spawntracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/** Рисует панель со статусом спавна в левом верхнем углу. */
public final class SpawnTrackerHud {

    private static final int LINE_HEIGHT = 10;
    private static final int PADDING = 3;
    private static final int BG_COLOR = 0x90000000;

    private PhantomInfo phantomInfo = new PhantomInfo();
    private MobScanner mobScanner = new MobScanner();
    private int tickCounter;

    /** Тяжёлый пересчёт — раз в refreshRate тиков, а не каждый кадр. */
    public void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        if (tickCounter++ % SpawnTrackerConfig.refreshRate == 0) {
            phantomInfo = PhantomInfo.compute(client);
            mobScanner = MobScanner.scan(client);
        }
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!SpawnTrackerConfig.enabled) return;
        if (client.player == null || client.world == null) return;
        if (client.options.hudHidden) return;
        if (client.currentScreen != null && !(client.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen)) return;

        List<Text> lines = buildLines();
        if (lines.isEmpty()) return;

        var tr = client.textRenderer;

        int maxWidth = 0;
        for (Text line : lines) {
            maxWidth = Math.max(maxWidth, tr.getWidth(line));
        }

        int x = SpawnTrackerConfig.offsetX;
        int y = SpawnTrackerConfig.offsetY;
        int boxW = maxWidth + PADDING * 2;
        int boxH = lines.size() * LINE_HEIGHT + PADDING * 2;

        context.fill(x, y, x + boxW, y + boxH, BG_COLOR);

        int textY = y + PADDING;
        for (Text line : lines) {
            context.drawTextWithShadow(tr, line, x + PADDING, textY, 0xFFFFFF);
            textY += LINE_HEIGHT;
        }
    }

    private List<Text> buildLines() {
        List<Text> lines = new ArrayList<>();

        lines.add(Text.translatable("hud.spawntracker.title")
                .formatted(Formatting.AQUA, Formatting.BOLD));

        if (SpawnTrackerConfig.showPhantoms) {
            addPhantomLines(lines);
        }

        if (SpawnTrackerConfig.showLight) {
            lines.add(label("hud.spawntracker.light")
                    .append(value(phantomInfo.blockLight + " / " + phantomInfo.skyLight,
                            phantomInfo.blockLight == 0 ? Formatting.RED : Formatting.GREEN)));
        }

        if (SpawnTrackerConfig.showMobCounts) {
            lines.add(label("hud.spawntracker.hostiles")
                    .append(value(String.valueOf(mobScanner.hostiles()), Formatting.WHITE)));
            lines.add(label("hud.spawntracker.passive")
                    .append(value(String.valueOf(mobScanner.creatures()), Formatting.WHITE)));
        }

        return lines;
    }

    private void addPhantomLines(List<Text> lines) {
        int days = phantomInfo.daysWithoutRest();

        // Строка со временем без сна
        Formatting restColor = phantomInfo.restThresholdPassed ? Formatting.RED : Formatting.GREEN;
        String restText = days + Text.translatable("hud.spawntracker.days_suffix").getString();
        lines.add(label("hud.spawntracker.no_sleep").append(value(restText, restColor)));

        if (!phantomInfo.restThresholdPassed) {
            // Ещё не набежало 72000 тиков — покажем, сколько осталось
            int ticksLeft = phantomInfo.ticksUntilThreshold();
            String left = formatTicks(ticksLeft);
            lines.add(label("hud.spawntracker.until_danger")
                    .append(value(left, Formatting.GREEN)));
            return;
        }

        // Порог пройден — что мешает прямо сейчас
        String reason = phantomInfo.blockingReasonKey();
        if (reason != null) {
            lines.add(label("hud.spawntracker.status")
                    .append(Text.translatable(reason).formatted(Formatting.GREEN)));
        } else {
            int percent = Math.round(phantomInfo.totalChance * 100.0F);
            Formatting c = percent >= 50 ? Formatting.RED
                    : percent >= 20 ? Formatting.GOLD : Formatting.YELLOW;
            lines.add(label("hud.spawntracker.status")
                    .append(Text.translatable("hud.spawntracker.reason.can_spawn").formatted(c)));
            lines.add(label("hud.spawntracker.chance")
                    .append(value(percent + "%", c)));
        }

        if (mobScanner.phantoms > 0) {
            String near = mobScanner.nearestPhantomDistance >= 0
                    ? String.format(" (%.0fm)", mobScanner.nearestPhantomDistance)
                    : "";
            lines.add(label("hud.spawntracker.phantoms_near")
                    .append(value(mobScanner.phantoms + near, Formatting.RED)));
        }
    }

    private static MutableText label(String key) {
        return Text.translatable(key).formatted(Formatting.GRAY);
    }

    private static MutableText value(String text, Formatting color) {
        return Text.literal(text).formatted(color);
    }

    /** Тики -> "1ч 23м" по игровому времени. */
    private static String formatTicks(int ticks) {
        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        if (minutes >= 60) {
            return (minutes / 60) + "h " + (minutes % 60) + "m";
        }
        return minutes + "m " + seconds + "s";
    }
}
