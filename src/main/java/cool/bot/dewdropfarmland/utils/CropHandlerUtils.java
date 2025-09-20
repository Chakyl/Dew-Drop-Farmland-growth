package cool.bot.dewdropfarmland.utils;

import com.baisylia.culturaldelights.block.custom.CornBlock;
import com.baisylia.culturaldelights.block.custom.CornUpperBlock;
import com.rosemods.windswept.common.block.WildBerryBushBlock;
import cool.bot.dewdropfarmland.DewDropFarmland;
import cool.bot.dewdropfarmland.block.CustomStemBlock;
import cool.bot.dewdropfarmland.tag.SturdyFarmlandBlockTags;
import net.mehvahdjukaar.supplementaries.common.block.blocks.FlaxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.ribs.vintagedelight.block.custom.GearoBerryBushBlock;
import net.satisfy.vinery.core.block.GrapeBush;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.RiceBlock;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;

import static cool.bot.dewdropfarmland.utils.ModGrowthCompat.*;
import static cool.bot.dewdropfarmland.utils.SeasonUtils.isGlassAboveBlock;
import static net.minecraft.world.level.block.CropBlock.AGE;
import static vectorwing.farmersdelight.common.block.TomatoVineBlock.ROPELOGGED;

public class CropHandlerUtils {

    public static int getFertilizerIncrease(int age, int maxAge, BlockState farmland) {
        int increase = 1;
        if (age == 0) {
            if (farmland.is(SturdyFarmlandBlockTags.WEAK_FERTILIZED_FARMLAND)) {
                increase = 2;
            }
            if (farmland.is(SturdyFarmlandBlockTags.STRONG_FERTILIZED_FARMLAND)) {
                increase = 3;
            }
            if (farmland.is(SturdyFarmlandBlockTags.HYPER_FERTILIZED_FARMLAND)) {
                increase = 4;
            }
        }
        return Math.min(increase, maxAge);
    }


    public static void growBush(ServerLevel level, Block bushBlock, BlockPos pos, int maxAge, BlockState farmland) {
        int bushAge = level.getBlockState(pos).getValue(AGE);
        if (bushAge < maxAge)
            level.setBlock(pos, bushBlock.defaultBlockState().setValue(AGE, bushAge + getFertilizerIncrease(bushAge, maxAge, farmland)), 2);
    }

    public static void handleCropGrowth(BlockState crop, ServerLevel level, BlockPos abovePos, BlockState farmland, RandomSource random) {
        Block aboveBlock = crop.getBlock();
        int growthAmount;
        BlockState newState;
        if (aboveBlock instanceof CropBlock cropBlock) {
            if (!cropBlock.isMaxAge(crop) && !(DewDropFarmland.SUPPLEMENTARIES_INSTALLED && cropBlock instanceof FlaxBlock)) {
                growthAmount = getFertilizerIncrease(cropBlock.getAge(crop), cropBlock.getMaxAge(), farmland);
                newState = cropBlock.getStateForAge(cropBlock.getAge(crop) + growthAmount);
                level.setBlock(abovePos, newState, 2);
                if (DewDropFarmland.FARMERS_DELIGHT_INSTALLED && cropBlock instanceof TomatoVineBlock tomatoVineBlock) {
                    BlockPos aboveCropPos = abovePos.offset(0, 1, 0);
                    BlockState tomatoVine = level.getBlockState(aboveCropPos);
                    if (tomatoVine.getBlock() instanceof TomatoVineBlock aboveTomatoVineBlock) {
                        if (!aboveTomatoVineBlock.isMaxAge(tomatoVine)) {
                            newState = aboveTomatoVineBlock.getStateForAge(aboveTomatoVineBlock.getAge(crop) + 1).setValue(ROPELOGGED, true);
                            level.setBlock(aboveCropPos, newState, 2);
                        }
                    } else {
                        tomatoVineBlock.attemptRopeClimb(level, abovePos, random);
                    }
                }
            } else if (DewDropFarmland.SUPPLEMENTARIES_INSTALLED && cropBlock instanceof FlaxBlock flaxBlock) {
                flaxBlock.growCropBy(level, abovePos, crop, getFertilizerIncrease(cropBlock.getAge(crop), cropBlock.getMaxAge(), farmland));
            }
        } else if (aboveBlock instanceof SweetBerryBushBlock sweetBerryBushBlock) {
            growBush(level, sweetBerryBushBlock, abovePos, SweetBerryBushBlock.MAX_AGE, farmland);
        } else if (DewDropFarmland.WINDSWEPT_INSTALLED && aboveBlock instanceof WildBerryBushBlock) {
            growWildBerryBush(level, aboveBlock, abovePos, farmland);
        } else if (DewDropFarmland.VINERY_INSTALLED && aboveBlock instanceof GrapeBush) {
            growGrapeBush(level, aboveBlock, abovePos, farmland);
        } else if (DewDropFarmland.VINTAGEDELIGHT_INSTALLED && aboveBlock instanceof GearoBerryBushBlock) {
            growGearoBerryBush(level, aboveBlock, abovePos, farmland);
        } else if (DewDropFarmland.CULTURAL_DELIGHTS_INSTALLED && aboveBlock instanceof CornBlock) {
            growCulturalDelightCorn(level, aboveBlock, abovePos, CornBlock.AGE, CornUpperBlock.AGE, farmland);
        } else if (DewDropFarmland.FARMERS_DELIGHT_INSTALLED && aboveBlock instanceof RiceBlock) {
            growFarmersDelightRice(level, aboveBlock, abovePos, farmland);

        } else if (aboveBlock instanceof CustomStemBlock stemBlock) {
            int stemAge = stemBlock.getAge(crop);
            if (stemAge == 7) {
                stemBlock.placeFruit(level, abovePos);
            } else {
                growthAmount = getFertilizerIncrease(stemAge, 7, farmland);
                newState = stemBlock.getStateForAge(stemAge + growthAmount);
                level.setBlock(abovePos, newState, 2);
            }
        } else if (aboveBlock instanceof PitcherCropBlock pitcherCropBlock) {
            int pitcherAge = level.getBlockState(abovePos).getValue(PitcherCropBlock.AGE);
            growthAmount = getFertilizerIncrease(pitcherAge, PitcherCropBlock.MAX_AGE, farmland);
            for (int i = 0; i < growthAmount; i++) {
                pitcherCropBlock.performBonemeal(level, random, abovePos, level.getBlockState(abovePos));
            }
        } else if (DewDropFarmland.FARMERS_DELIGHT_INSTALLED && aboveBlock instanceof BuddingTomatoBlock tomatoBlock) {
            tomatoBlock.growPastMaxAge(crop, level, abovePos, random);
        }
    }


    public static void growCactusLike(ServerLevel level, Block block, BlockPos pos, BlockState farmland) {
        int growth = getFertilizerIncrease(0, 2, farmland);
        for (int i = 1; i < 3; ++i) {
            if (level.getBlockState(pos.above(i)).is(Blocks.AIR)) {
                if (block instanceof SugarCaneBlock && ForgeHooks.onCropsGrowPre(level, pos.above(i), block.defaultBlockState(), true)) {
                    level.setBlockAndUpdate(pos.above(i), block.defaultBlockState());
                    ForgeHooks.onCropsGrowPost(level, pos.above(i), block.defaultBlockState());
                    level.setBlock(pos.above(i), block.defaultBlockState(), 4);
                } else {
                    level.setBlock(pos.above(i), block.defaultBlockState(), 4);
                    level.neighborChanged(block.defaultBlockState(), pos.above(i - 1), block, pos.above(i), false);
                }
                if (growth > 1) {
                    growth--;
                } else break;
            }
        }
    }

    public static void handleSandCropGrowth(BlockState crop, ServerLevel level, BlockPos abovePos, BlockState farmland, RandomSource random) {
        Block aboveBlock = crop.getBlock();
        if (aboveBlock instanceof CactusBlock cactusBlock) {
            growCactusLike(level, cactusBlock, abovePos, farmland);
        } else if (aboveBlock instanceof SugarCaneBlock sugarCaneBlock) {
            if (sugarCaneBlock.canSurvive(crop, level, abovePos))
                growCactusLike(level, sugarCaneBlock, abovePos, farmland);
        }
    }


    public static boolean inRange(Integer range, BlockPos pos, BlockPos center) {
        return Math.abs(pos.getX() - center.getX()) < range + 1 && Math.abs(pos.getZ() - center.getZ()) < range + 1;
    }


    public static boolean isNearSprinkler(LevelReader level, BlockPos pPos) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-4, 0, -4), pPos.offset(4, 1, 4))) {
            if (inRange(1, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_1))
                return true;
            if (inRange(2, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_2))
                return true;
            if (inRange(3, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_3))
                return true;
            if (inRange(4, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_4))
                return true;
        }
        return false;
    }

    public static void growCropsInRadius(ServerLevel level, BlockPos centerPos, RandomSource random, int radius) {
        for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(centerPos.getX() - radius, centerPos.getY(), centerPos.getZ() - radius), new BlockPos(
                centerPos.getX() + radius,
                centerPos.getY(),
                centerPos.getZ() + radius)
        )) {
            growCrop(level.getBlockState(pos), level, pos, level.getBlockState(centerPos.below()), random, false);
        }
    }

    public static void growCrop(BlockState crop, ServerLevel level, BlockPos abovePos, BlockState farmland, RandomSource random, Boolean sandy) {
        growCrop(crop, level, abovePos, farmland, random, sandy, false);
    }

    public static void growCrop(BlockState crop, ServerLevel level, BlockPos abovePos, BlockState farmland, RandomSource random, Boolean sandy, Boolean gardenPot) {
        boolean canGrow = true;
        if (DewDropFarmland.SERENE_SEASONS_INSTALLED) {
            ResourceLocation fertileKey = ForgeRegistries.BLOCKS.getKey(crop.getBlock());
            boolean fertile = ModFertility.isCropFertile(fertileKey != null ? fertileKey.toString() : null, level, abovePos);
            canGrow = (!ModConfig.fertility.seasonalCrops || fertile || isGlassAboveBlock(level, abovePos) || (gardenPot && !level.canSeeSky(abovePos.above())));
        }
        if (canGrow) {
            if (sandy) {
                handleSandCropGrowth(crop, level, abovePos, farmland, random);
            } else {
                handleCropGrowth(crop, level, abovePos, farmland, random);
            }
        }
    }
}
