package cool.bot.dewdropfarmland.utils;

import cool.bot.botslib.tag.DewDropBlockTags;
import cool.bot.dewdropfarmland.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.PaleStemBlock;
import net.satisfy.vinery.core.registry.GrapeTypeRegistry;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;

public class SeasonUtils {
    public static boolean isPaleStemFertile(BlockState state, Level level, BlockPos pos) {
        Season season = SeasonHelper.getSeasonState(level).getSeason();
        Holder<Biome> biome = level.getBiome(pos);
        boolean isSavanna = state.getValue(PaleStemBlock.GRAPE) == GrapeTypeRegistry.SAVANNA_WHITE || state.getValue(PaleStemBlock.GRAPE) == GrapeTypeRegistry.SAVANNA_RED;
        boolean isTaiga = state.getValue(PaleStemBlock.GRAPE) == GrapeTypeRegistry.TAIGA_RED || state.getValue(PaleStemBlock.GRAPE) == GrapeTypeRegistry.TAIGA_WHITE;
        if (pos.getY() < ModConfig.fertility.undergroundFertilityLevel && !level.canSeeSky(pos)) {
            return true;
        } else if (biome.is(ModTags.Biomes.INFERTILE_BIOMES)) {
            return false;
        } else if (ModConfig.fertility.seasonalCrops && !biome.is(ModTags.Biomes.BLACKLISTED_BIOMES) && ModConfig.seasons.isDimensionWhitelisted(level.dimension())) {
            if (biome.is(ModTags.Biomes.TROPICAL_BIOMES)) {
                return isSavanna;
            } else if (!((Biome) biome.value()).warmEnoughToRain(pos)) {
                return isTaiga;
            } else if (season == Season.SPRING && !isTaiga) {
                return true;
            } else if (season == Season.SUMMER && isSavanna) {
                return true;
            } else if (season == Season.AUTUMN && !isSavanna && !isTaiga) {
                return true;
            } else if (season == Season.WINTER && isTaiga) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean isLatticeFertile(Level level, BlockPos pos) {
        Season season = SeasonHelper.getSeasonState(level).getSeason();
        Holder<Biome> biome = level.getBiome(pos);
        if (pos.getY() < ModConfig.fertility.undergroundFertilityLevel && !level.canSeeSky(pos)) {
            return true;
        } else if (biome.is(ModTags.Biomes.INFERTILE_BIOMES)) {
            return false;
        } else if (ModConfig.fertility.seasonalCrops && !biome.is(ModTags.Biomes.BLACKLISTED_BIOMES) && ModConfig.seasons.isDimensionWhitelisted(level.dimension())) {
            if (biome.is(ModTags.Biomes.TROPICAL_BIOMES)) {
                return true;
            } else if (season == Season.SUMMER) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean isGlassAboveBlock(ServerLevel level, BlockPos cropPos) {
        BlockState scannedBlock;
        for (int i = 0; i < 16; ++i) {
            scannedBlock = level.getBlockState(cropPos.offset(0, i + 1, 0));
            if (Config.strictGreenhouses && scannedBlock.is(DewDropBlockTags.WATERABLE)) return false;
            if (scannedBlock.is(ModTags.Blocks.GREENHOUSE_GLASS)) {
                return true;
            }
        }
        return false;
    }
}
