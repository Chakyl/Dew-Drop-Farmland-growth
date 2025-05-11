package cool.bot.dewdropfarmland.block;


import cool.bot.botslib.tag.DewDropBlockTags;
import cool.bot.botslib.util.RNG;
import cool.bot.dewdropfarmland.Config;
import cool.bot.botslib.util.Util;
import cool.bot.dewdropfarmland.DewDropFarmland;
import cool.bot.dewdropfarmland.tag.SturdyFarmlandBlockTags;
import net.mehvahdjukaar.supplementaries.common.block.blocks.FlaxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeBlock;
import com.baisylia.culturaldelights.block.ModBlocks;
import com.baisylia.culturaldelights.block.custom.CornBlock;
import com.baisylia.culturaldelights.block.custom.CornUpperBlock;
import net.ribs.vintagedelight.block.custom.GearoBerryBushBlock;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModTags;

import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;

import org.jetbrains.annotations.Nullable;

import static vectorwing.farmersdelight.common.block.TomatoVineBlock.ROPELOGGED;

// Override vanilla Farmland
public class CustomFarmland extends FarmBlock implements IForgeBlock {
    public CustomFarmland(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!Config.noRandomTick) {
            super.randomTick(state, level, pos, random);
        }

    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        ItemStack itemStack = context.getItemInHand();

        if (!itemStack.canPerformAction(toolAction)) {return null;}

        if (ToolActions.SHOVEL_FLATTEN == toolAction && Config.shovelReverting) {
            return Blocks.DIRT.defaultBlockState();
        }

        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        net.minecraftforge.common.PlantType plantType = plantable.getPlantType(world, pos.relative(facing));
        return plantType == PlantType.CROP || plantType == PlantType.PLAINS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (Config.rainWatering || Config.dailyReset) {

            level.scheduleTick(pos, this, 10);

            if (level.isRainingAt(pos.above()) && Config.rainWatering) {
                Util.setMoist(level, pos);
            }

            if (!Config.dailyReset || !level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).get()) {
                return;
            }
            long dayTime = level.getDayTime() % 24000;
            // check before rain

            if (dayTime >= Config.dailyTimeMin && dayTime < Config.dailyTimeMin + 10) {
                BlockState farmland = level.getBlockState(pos);
                boolean hydrated = Config.checkSprinklers && isNearSprinkler(level, pos);
                if (!hydrated && !Util.isMoistWaterable(level, pos)) {
                    if (RNG.mc_ihundo(random, Config.dailyDecayChance)) {
                        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                    }
                } else {
                    BlockPos abovePos = pos.above();
                    BlockState crop = level.getBlockState(abovePos);
                    if (!(crop.getBlock() instanceof AirBlock)) {
                        growCrop(crop, level, abovePos, farmland, random);
                    }
                    if (RNG.mc_ihundo(random, Config.dailyDryChance)) {
                        if (hydrated && Util.isDryWaterable(level, pos)) {
                            Util.setMoist(level, pos);
                        }
                        if (!hydrated && farmland.is(SturdyFarmlandBlockTags.HYDRATING_FARMLAND) && crop.getBlock() instanceof CropBlock cropBlock && cropBlock.getAge(crop) < Math.floor((float) cropBlock.getMaxAge() / 2) ) {
                            hydrated = true;
                        }
                        if (!hydrated) {
                            Util.setDry(level, pos);
                        }
                    }

                }
            }

        }

        if (!Config.sturdyFarmland) {
            super.tick(state, level, pos, random);
        }
    }
    private static int getFertilizerIncrease(int age, int maxAge, BlockState farmland) {
        if (age == 0) {
            if (farmland.is(SturdyFarmlandBlockTags.WEAK_FERTILIZED_FARMLAND)) {
                return 2;
            }
            if (farmland.is(SturdyFarmlandBlockTags.STRONG_FERTILIZED_FARMLAND)) {
                return 3;
            }
            if (farmland.is(SturdyFarmlandBlockTags.HYPER_FERTILIZED_FARMLAND)) {
                if (maxAge > 3) {
                    return 4;
                } else {
                    return 3;
                }
            }
        }
        return 1;
    }
    private static boolean isGlassAboveBlock(ServerLevel level, BlockPos cropPos) {
        BlockState scannedBlock;
        for (int i = 0; i < 16; ++i) {
            scannedBlock = level.getBlockState(cropPos.offset(0, i + 1, 0));
            if (Config.strictGreenhouses && scannedBlock.is(DewDropBlockTags.WATERABLE)) break;
            if (scannedBlock.is(ModTags.Blocks.GREENHOUSE_GLASS)) {
                return true;
            }
        }
        return false;
    }

    private static void growCrop(BlockState crop, ServerLevel level, BlockPos abovePos, BlockState farmland, RandomSource random) {
        ResourceLocation fertileKey = ForgeRegistries.BLOCKS.getKey(crop.getBlock());
        boolean fertile = ModFertility.isCropFertile(fertileKey != null ? fertileKey.toString() : null, level, abovePos);
        boolean canGrow = !ModConfig.fertility.seasonalCrops || fertile || isGlassAboveBlock(level, abovePos);
        Block aboveBlock = crop.getBlock();
        BlockPos aboveAbovePos = abovePos.above();
        int growthAmount;
        BlockState newState;
        if (canGrow) {
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
                    growthAmount = getFertilizerIncrease(cropBlock.getAge(crop), cropBlock.getMaxAge(), farmland);
                    flaxBlock.growCropBy(level, abovePos, crop, growthAmount);
                }
            } else if (aboveBlock instanceof SweetBerryBushBlock sweetBerryBushBlock) {
                int bushAge = level.getBlockState(abovePos).getValue(SweetBerryBushBlock.AGE);
                if (bushAge < SweetBerryBushBlock.MAX_AGE) {
                    growthAmount = getFertilizerIncrease(bushAge, SweetBerryBushBlock.MAX_AGE, farmland);
                    newState = sweetBerryBushBlock.defaultBlockState().setValue(SweetBerryBushBlock.AGE, bushAge + growthAmount);
                    level.setBlock(abovePos, newState, 2);
                }
            } else if (DewDropFarmland.VINTAGEDELIGHT_INSTALLED && aboveBlock instanceof GearoBerryBushBlock gearoBerryBushBlock) {
                int bushAge = level.getBlockState(abovePos).getValue(GearoBerryBushBlock.AGE);
                if (bushAge < GearoBerryBushBlock.MAX_AGE) {
                    growthAmount = getFertilizerIncrease(bushAge, GearoBerryBushBlock.MAX_AGE, farmland);
                    newState = gearoBerryBushBlock.defaultBlockState().setValue(GearoBerryBushBlock.AGE, bushAge + growthAmount);
                    level.setBlock(abovePos, newState, 2);
                }
            } else if (DewDropFarmland.CULTURAL_DELIGHTS_INSTALLED && aboveBlock instanceof CornBlock cornBlock) {
                int cornAge = level.getBlockState(abovePos).getValue(cornBlock.AGE);
                int cornMaxAge = cornBlock.getMaxAge();
                if (cornAge < cornMaxAge) {
                    growthAmount = getFertilizerIncrease(cornAge, cornMaxAge, farmland);
                    newState = cornBlock.defaultBlockState().setValue(cornBlock.AGE, cornAge + growthAmount);
                    level.setBlock(abovePos, newState, 2);
                } else if (cornAge == cornMaxAge) {
                    CornUpperBlock cornUpper = (CornUpperBlock) ModBlocks.CORN_UPPER.get();
                    if (cornUpper.defaultBlockState().canSurvive(level, aboveAbovePos) && level.isEmptyBlock(aboveAbovePos)) {
                        level.setBlockAndUpdate(aboveAbovePos, cornUpper.defaultBlockState());
                        ForgeHooks.onCropsGrowPost(level, aboveAbovePos, level.getBlockState(aboveAbovePos));
                    } else if (level.getBlockState(aboveAbovePos).getBlock() instanceof CornUpperBlock cornUpperBlock) {
                        int aboveCornAge = level.getBlockState(aboveAbovePos).getValue(cornUpperBlock.CORN_AGE);
                        if (aboveCornAge < cornUpperBlock.getMaxAge()) {
                            newState = cornUpperBlock.defaultBlockState().setValue(cornUpperBlock.CORN_AGE, aboveCornAge + 1);
                            level.setBlock(aboveAbovePos, newState, 2);
                        }
                    }
                }
            } else if (aboveBlock instanceof CustomStemBlock stemBlock) {
                int stemAge = stemBlock.getAge(crop);
                if (ModConfig.fertility.seasonalCrops) {
                    if (stemAge == 7) {
                        stemBlock.placeFruit(level, abovePos);
                    } else {
                        growthAmount = getFertilizerIncrease(stemAge, 7, farmland);
                        newState = stemBlock.getStateForAge(stemAge + growthAmount);
                        level.setBlock(abovePos, newState, 2);
                    }
                }
            } else if (aboveBlock instanceof PitcherCropBlock pitcherCropBlock) {
                int pitcherAge = level.getBlockState(abovePos).getValue(PitcherCropBlock.AGE);
                growthAmount = getFertilizerIncrease(pitcherAge, PitcherCropBlock.MAX_AGE, farmland);
                for (int i = 0; i < growthAmount; i++) {
                    pitcherCropBlock.performBonemeal(level, random, abovePos, level.getBlockState(abovePos));
                }
            }
            else if (DewDropFarmland.FARMERS_DELIGHT_INSTALLED && aboveBlock instanceof BuddingTomatoBlock tomatoBlock) {
                tomatoBlock.growPastMaxAge(crop, level, abovePos, random);
            }
        }
    }

    private static boolean inRange(Integer range, BlockPos pos, BlockPos center) {
        return Math.abs(pos.getX() - center.getX()) < range + 1  && Math.abs(pos.getZ() - center.getZ()) < range + 1;
    }

    private static boolean isNearSprinkler(LevelReader level, BlockPos pPos) {
        for(BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-4, 0, -4), pPos.offset(4, 1, 4))) {
            if (inRange(1, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_1)) return true;
            if (inRange(2, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_2)) return true;
            if (inRange(3, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_3)) return true;
            if (inRange(4, blockpos, pPos) && level.getBlockState(blockpos).is(SturdyFarmlandBlockTags.SPRINKLER_TIER_4)) return true;
        }
        return false;
    }
    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!level.isClientSide()) {
            if (Config.rainWatering || Config.dailyReset) {
                ((ServerLevel) level).scheduleTick(pos, this, 10); // prevent rescheduling
            }
        }
        super.onBlockStateChange(level, pos, oldState, newState);
    }
}
