package com.terusty.spawntracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;

/**
 * Считает условия спавна фантомов ровно так, как это делает ванильный
 * PhantomSpawner, но по данным, доступным на клиенте.
 *
 * Ванильные проверки (1.21.1), все должны пройти:
 *   1. gamerule doInsomnia = true          <- на клиенте НЕ виден, см. README
 *   2. world.getAmbientDarkness() >= 5     <- ночь или гроза
 *   3. игрок не в спектаторе
 *   4. y >= sea level (64)
 *   5. над игроком видно небо
 *   6. localDifficulty > random(0..3)
 *   7. random.nextInt(x) >= 72000, где x = время без сна в тиках
 */
public final class PhantomInfo {

    /** 3 игровых дня = 72000 тиков. */
    public static final int REST_THRESHOLD = 72000;

    /** Тиков в игровых сутках. */
    public static final int DAY_TICKS = 24000;

    /** Ниже этой темноты фантомы не спавнятся. */
    public static final int MIN_AMBIENT_DARKNESS = 5;

    public int timeSinceRest;
    public boolean darkEnough;
    public boolean aboveSeaLevel;
    public boolean skyVisible;
    public boolean notSpectator;
    public boolean restThresholdPassed;

    public int playerY;
    public int seaLevel;
    public float localDifficulty;
    public int blockLight;
    public int skyLight;

    /** Шанс пройти проверку сложности: clamp(localDifficulty / 3, 0, 1). */
    public float difficultyChance;

    /** Шанс пройти проверку времени без сна: (x - 72000) / x. */
    public float restChance;

    /** Итоговый шанс одной попытки спавна. */
    public float totalChance;

    /** Все жёсткие условия выполнены (без учёта случайности). */
    public boolean allConditionsMet;

    public static PhantomInfo compute(MinecraftClient client) {
        PhantomInfo info = new PhantomInfo();

        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        if (player == null || world == null) {
            return info;
        }

        BlockPos pos = player.getBlockPos();

        info.timeSinceRest = player.getStatHandler()
                .getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));

        info.playerY = pos.getY();
        info.seaLevel = world.getSeaLevel();

        info.darkEnough = world.getAmbientDarkness() >= MIN_AMBIENT_DARKNESS;
        info.aboveSeaLevel = info.playerY >= info.seaLevel;
        info.skyVisible = world.isSkyVisible(pos);
        info.notSpectator = !player.isSpectator();
        info.restThresholdPassed = info.timeSinceRest >= REST_THRESHOLD;

        LocalDifficulty diff = world.getLocalDifficulty(pos);
        info.localDifficulty = diff.getLocalDifficulty();

        info.blockLight = world.getLightLevel(LightType.BLOCK, pos);
        info.skyLight = world.getLightLevel(LightType.SKY, pos);

        // Ваниль: localDifficulty.isHarderThan(random.nextFloat() * 3.0F)
        info.difficultyChance = MathHelper.clamp(info.localDifficulty / 3.0F, 0.0F, 1.0F);

        // Ваниль: random.nextInt(x) >= 72000
        int x = Math.max(1, info.timeSinceRest);
        info.restChance = x > REST_THRESHOLD
                ? (float) (x - REST_THRESHOLD) / (float) x
                : 0.0F;

        info.allConditionsMet = info.darkEnough
                && info.aboveSeaLevel
                && info.skyVisible
                && info.notSpectator
                && info.restThresholdPassed;

        info.totalChance = info.allConditionsMet
                ? info.difficultyChance * info.restChance
                : 0.0F;

        return info;
    }

    /** Целых игровых суток без сна. */
    public int daysWithoutRest() {
        return timeSinceRest / DAY_TICKS;
    }

    /** Сколько тиков осталось до порога в 72000. */
    public int ticksUntilThreshold() {
        return Math.max(0, REST_THRESHOLD - timeSinceRest);
    }

    /** Первая непройденная проверка — что именно мешает спавну. */
    public String blockingReasonKey() {
        if (!notSpectator) return "hud.spawntracker.reason.spectator";
        if (!restThresholdPassed) return "hud.spawntracker.reason.rested";
        if (!darkEnough) return "hud.spawntracker.reason.bright";
        if (!aboveSeaLevel) return "hud.spawntracker.reason.low";
        if (!skyVisible) return "hud.spawntracker.reason.roof";
        return null;
    }
}
