package com.terusty.spawntracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.PhantomEntity;

import java.util.EnumMap;
import java.util.Map;

/**
 * Считает загруженных мобов вокруг игрока по категориям спавна.
 *
 * Важно: это данные КЛИЕНТА. Клиент видит только сущности в пределах
 * дистанции трекинга (обычно радиус прорисовки), поэтому цифры —
 * не серверный mob cap, а «что рядом со мной». Для отладки ферм и
 * понимания обстановки этого достаточно.
 */
public final class MobScanner {

    /** Радиус сканирования в блоках. */
    private static final double SCAN_RADIUS = 128.0;

    public final Map<SpawnGroup, Integer> counts = new EnumMap<>(SpawnGroup.class);

    public int phantoms;
    public int totalMobs;
    public double nearestPhantomDistance = -1.0;

    public static MobScanner scan(MinecraftClient client) {
        MobScanner result = new MobScanner();

        for (SpawnGroup group : SpawnGroup.values()) {
            result.counts.put(group, 0);
        }

        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null) {
            return result;
        }

        double radiusSq = SCAN_RADIUS * SCAN_RADIUS;

        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (living == player) continue;

            double distSq = living.squaredDistanceTo(player);
            if (distSq > radiusSq) continue;

            SpawnGroup group = living.getType().getSpawnGroup();
            result.counts.merge(group, 1, Integer::sum);
            result.totalMobs++;

            if (living instanceof PhantomEntity) {
                result.phantoms++;
                double dist = Math.sqrt(distSq);
                if (result.nearestPhantomDistance < 0 || dist < result.nearestPhantomDistance) {
                    result.nearestPhantomDistance = dist;
                }
            }
        }

        return result;
    }

    public int hostiles() {
        return counts.getOrDefault(SpawnGroup.MONSTER, 0);
    }

    public int creatures() {
        return counts.getOrDefault(SpawnGroup.CREATURE, 0);
    }

    public int ambient() {
        return counts.getOrDefault(SpawnGroup.AMBIENT, 0);
    }

    public int waterCreatures() {
        return counts.getOrDefault(SpawnGroup.WATER_CREATURE, 0)
                + counts.getOrDefault(SpawnGroup.WATER_AMBIENT, 0)
                + counts.getOrDefault(SpawnGroup.UNDERGROUND_WATER_CREATURE, 0);
    }
}
