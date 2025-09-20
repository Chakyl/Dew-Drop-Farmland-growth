package cool.bot.dewdropfarmland.utils;

import cool.bot.dewdropfarmland.DewDropFarmland;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.LatticeBlock;
import net.satisfy.vinery.core.block.PaleStemBlock;
import net.satisfy.vinery.core.block.entity.LatticeBlockEntity;
import net.satisfy.vinery.core.registry.GrapeTypeRegistry;
import net.satisfy.vinery.core.util.GrapeType;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;

import static cool.bot.dewdropfarmland.utils.SeasonUtils.isGlassAboveBlock;

public class VineryCompat {
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

    public static void handlePaleStemGrowth(BlockState state, ServerLevel world, BlockPos pos) {
        PaleStemBlock block = (PaleStemBlock) state.getBlock();
        int i;
        if (!block.isMature(state) && block.hasTrunk(world, pos) && state.getValue(PaleStemBlock.AGE) > 0 && world.getRawBrightness(pos, 0) >= 9 && (i = (Integer) state.getValue(PaleStemBlock.AGE)) < 4) {
            boolean canGrow = true;
            if (DewDropFarmland.SERENE_SEASONS_INSTALLED) {
                boolean fertile = isPaleStemFertile(state, world, pos);
                canGrow = (!ModConfig.fertility.seasonalCrops || fertile || isGlassAboveBlock(world, pos));
            }
            if (canGrow) {
                world.setBlock(pos, block.withAge(state, i + 1, state.getValue(PaleStemBlock.GRAPE)), 2);
            }
        }
    }

    public static void handleLatticeGrowth(BlockState state, ServerLevel world, BlockPos pos) {
        LatticeBlock block = (LatticeBlock) state.getBlock();
        if (!block.isMature(state)) {
            boolean canGrow = true;
            if (DewDropFarmland.SERENE_SEASONS_INSTALLED) {
                boolean fertile = isLatticeFertile(world, pos);
                canGrow = (!ModConfig.fertility.seasonalCrops || fertile || isGlassAboveBlock(world, pos));
            }
            if (canGrow) {
                int age = state.getValue(LatticeBlock.AGE);
                GrapeType type = state.getValue(LatticeBlock.GRAPE);
                BlockState newState = state.setValue(LatticeBlock.AGE, age + 1).setValue(LatticeBlock.GRAPE, type);
                world.setBlock(pos, newState, 2);
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof LatticeBlockEntity) {
                    LatticeBlockEntity lattice = (LatticeBlockEntity) be;
                    lattice.setAge(age + 1);
                    lattice.setGrapeType(type);
                }
            }
        }
    }
}
