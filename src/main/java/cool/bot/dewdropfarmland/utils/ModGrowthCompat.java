package cool.bot.dewdropfarmland.utils;

import com.baisylia.culturaldelights.block.ModBlocks;
import com.baisylia.culturaldelights.block.custom.CornBlock;
import com.baisylia.culturaldelights.block.custom.CornUpperBlock;
import com.cobblemon.mod.common.block.MintBlock;
import com.cobblemon.mod.common.block.RevivalHerbBlock;
import com.github.mnesikos.flowerary.block.TallFlowerCropBlock;
import com.rosemods.windswept.common.block.WildBerryBushBlock;
import cool.bot.dewdropfarmland.DewDropFarmland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.ForgeHooks;
import net.ribs.vintagedelight.block.custom.GearoBerryBushBlock;
import net.satisfy.vinery.core.block.GrapeBush;
import vectorwing.farmersdelight.common.block.RiceBlock;
import vectorwing.farmersdelight.common.block.RicePaniclesBlock;

import static com.github.mnesikos.flowerary.block.TallFlowerCropBlock.SEGMENT;
import static cool.bot.dewdropfarmland.utils.CropHandlerUtils.getFertilizerIncrease;

public class ModGrowthCompat {
    public static void growFarmersDelightRice(ServerLevel level, Block pRiceBlock, BlockPos pos, BlockState farmland) {
        RiceBlock riceBlock = (RiceBlock) pRiceBlock;
        BlockState state = level.getBlockState(pos);
        int age = state.getValue(RiceBlock.AGE);
        if (age <= riceBlock.getMaxAge()) {
            if (ForgeHooks.onCropsGrowPre(level, pos, state, true)) {
                if (age == riceBlock.getMaxAge()) {
                    RicePaniclesBlock riceUpper = (RicePaniclesBlock) vectorwing.farmersdelight.common.registry.ModBlocks.RICE_CROP_PANICLES.get();
                    if (riceUpper.defaultBlockState().canSurvive(level, pos.above())) {
                        if (level.isEmptyBlock(pos.above())) {
                            level.setBlockAndUpdate(pos.above(), riceUpper.defaultBlockState());
                            ForgeHooks.onCropsGrowPost(level, pos, state);
                        } else if (level.getBlockState(pos.above()).getBlock() instanceof RicePaniclesBlock ricePaniclesBlock) {
                            int panicleAge = level.getBlockState(pos.above()).getValue(RiceBlock.AGE);
                            if (panicleAge < ricePaniclesBlock.getMaxAge()) {
                                level.setBlock(pos.above(), ricePaniclesBlock.defaultBlockState().setValue(RiceBlock.AGE, panicleAge + 1), 2);
                            }
                        }
                    }
                } else {
                    level.setBlock(pos, riceBlock.withAge(age + getFertilizerIncrease(age, riceBlock.getMaxAge(), farmland)), 2);
                    ForgeHooks.onCropsGrowPost(level, pos, state);
                }
            }
        }

    }

    public static void growCulturalDelightCorn(ServerLevel level, Block cornBlock, BlockPos pos, IntegerProperty ageProperty, IntegerProperty upperAgeProperty, BlockState farmland) {
        BlockPos aboveAbovePos = pos.above();
        int cornAge = level.getBlockState(pos).getValue(ageProperty);
        int cornMaxAge = ((CornBlock) cornBlock).getMaxAge();
        if (cornAge < cornMaxAge) {
            level.setBlock(pos, cornBlock.defaultBlockState().setValue(ageProperty, cornAge + getFertilizerIncrease(cornAge, cornMaxAge, farmland)), 2);
        } else if (cornAge == cornMaxAge) {
            CornUpperBlock cornUpper = (CornUpperBlock) ModBlocks.CORN_UPPER.get();
            if (cornUpper.defaultBlockState().canSurvive(level, aboveAbovePos) && level.isEmptyBlock(aboveAbovePos)) {
                level.setBlockAndUpdate(aboveAbovePos, cornUpper.defaultBlockState());
                ForgeHooks.onCropsGrowPost(level, aboveAbovePos, level.getBlockState(aboveAbovePos));
            } else if (level.getBlockState(aboveAbovePos).getBlock() instanceof CornUpperBlock cornUpperBlock) {
                int aboveCornAge = level.getBlockState(aboveAbovePos).getValue(upperAgeProperty);
                if (aboveCornAge < cornUpperBlock.getMaxAge()) {
                    level.setBlock(aboveAbovePos, cornUpperBlock.defaultBlockState().setValue(upperAgeProperty, aboveCornAge + 1), 2);
                }
            }
        }
    }

    public static void growGearoBerryBush(ServerLevel level, Block bushBlock, BlockPos pos, BlockState farmland) {
        int bushAge = level.getBlockState(pos).getValue(GearoBerryBushBlock.AGE);
        if (bushAge < GearoBerryBushBlock.MAX_AGE)
            level.setBlock(pos, bushBlock.defaultBlockState().setValue(GearoBerryBushBlock.AGE, bushAge + getFertilizerIncrease(bushAge, GearoBerryBushBlock.MAX_AGE, farmland)), 2);
    }

    public static void growGrapeBush(ServerLevel level, Block bushBlock, BlockPos pos, BlockState farmland) {
        int bushAge = level.getBlockState(pos).getValue(GrapeBush.AGE);
        if (bushAge < 3)
            level.setBlock(pos, bushBlock.defaultBlockState().setValue(GrapeBush.AGE, bushAge + getFertilizerIncrease(bushAge, 3, farmland)), 2);
    }

    public static void growWildBerryBush(ServerLevel level, Block bushBlock, BlockPos pos, BlockState farmland) {
        int bushAge = level.getBlockState(pos).getValue(WildBerryBushBlock.AGE);
        if (bushAge < 3)
            level.setBlock(pos, bushBlock.defaultBlockState().setValue(WildBerryBushBlock.AGE, bushAge + getFertilizerIncrease(bushAge, 3, farmland)), 2);
    }

    public static void growTallFlowerCropBlock(ServerLevel level, BlockState crop, Block cropBlock, BlockPos pos, BlockState farmland) {
        if (crop.getValue(SEGMENT) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
        }
        int age = ((TallFlowerCropBlock)cropBlock).getAge(crop);
        int growthAge = age +  getFertilizerIncrease(age, 3, farmland);
//        growthAge = Math.min(growthAge, ((TallFlowerCropBlock)cropBlock).getMaxAge());
//        if (growthAge >= ((TallFlowerCropBlock)cropBlock).upperSegmentAge) {
//            if (!((TallFlowerCropBlock)cropBlock).canGrowUp(level, pos)) {
//                return;
//            }
//
//            level.setBlock(pos.above(), ((TallFlowerCropBlock) level.getBlockState(pos.above()).getBlock()).getStateForAge(growthAge).setValue(SEGMENT, DoubleBlockHalf.UPPER), 2);
//        }

        level.setBlock(pos, ((TallFlowerCropBlock)cropBlock).getStateForAge(growthAge), 2);
    }

    public static void growRevivalHerbBlock(ServerLevel level, Block revivalHerbBlock, BlockPos pos, BlockState farmland) {
        BlockState herbState = level.getBlockState(pos);
        int mintAge = ((CropBlock)revivalHerbBlock).getAge(herbState);
        if (mintAge < 8) {
            level.setBlock(pos, herbState.setValue(RevivalHerbBlock.Companion.getAGE(), mintAge + getFertilizerIncrease(mintAge, RevivalHerbBlock.MAX_AGE, farmland)), 2);
        }
    }
}
