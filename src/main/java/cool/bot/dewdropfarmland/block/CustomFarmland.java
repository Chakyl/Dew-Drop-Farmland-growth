package cool.bot.dewdropfarmland.block;

import cool.bot.botslib.util.RNG;
import cool.bot.dewdropfarmland.Config;
import cool.bot.botslib.util.Util;
import cool.bot.dewdropfarmland.tag.SturdyFarmlandBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeBlock;

import sereneseasons.config.FertilityConfig;
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

            if (dayTime >= Config.dailyTimeMin && dayTime <= Config.dailyTimeMin + 10) {
                BlockState farmland = level.getBlockState(pos);
                if (!Util.isMoistWaterable(level, pos)) {
                    if (RNG.mc_ihundo(random, Config.dailyDecayChance)) {
                        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                    }

                } else {
                    BlockPos abovePos = pos.above();
                    BlockState crop = level.getBlockState(abovePos);
                    ResourceLocation fertileKey = ForgeRegistries.BLOCKS.getKey(crop.getBlock());
                    boolean fertile = ModFertility.isCropFertile(fertileKey != null ? fertileKey.toString() : null, level, abovePos);

                    if (crop.getBlock() instanceof CropBlock cropBlock) {
                        if (!cropBlock.isMaxAge(crop) && FertilityConfig.seasonalCrops.get() && (fertile || isGlassAboveBlock(level, abovePos))) {
                            int increase = 1;
                            if (cropBlock.getAge(crop) == 0) {
                                if (farmland.is(SturdyFarmlandBlockTags.WEAK_FERTILIZED_FARMLAND)) {
                                    increase = 2;
                                }
                                if (farmland.is(SturdyFarmlandBlockTags.STRONG_FERTILIZED_FARMLAND)) {
                                    increase = 3;
                                }
                            }
                            BlockState newState = cropBlock.getStateForAge(cropBlock.getAge(crop) + increase);
                            level.setBlock(abovePos, newState, 2);
                            if (cropBlock instanceof TomatoVineBlock tomatoVineBlock) {
                                BlockPos aboveCropPos = abovePos.offset(0, 1, 0);
                                BlockState tomatoVine = level.getBlockState(aboveCropPos);
                                if (tomatoVine.getBlock() instanceof TomatoVineBlock aboveTomatoVineBlock) {
                                    if (!aboveTomatoVineBlock.isMaxAge(tomatoVine)) {
                                        BlockState newVineState = aboveTomatoVineBlock.getStateForAge(aboveTomatoVineBlock.getAge(crop) + 1).setValue(ROPELOGGED, true);
                                        level.setBlock(aboveCropPos, newVineState, 2);
                                    }
                                } else {
                                    tomatoVineBlock.attemptRopeClimb(level, abovePos, random);
                                }
                            }
                        }
                        if (cropBlock.isMaxAge(crop)) {
                            Util.setDry(level, pos);
                        }
                    } else if (crop.getBlock() instanceof BuddingTomatoBlock tomatoBlock) {
                        tomatoBlock.growPastMaxAge(crop, level, abovePos, random);
                    }
                    if (RNG.mc_ihundo(random, Config.dailyDryChance)) {
                        if (!farmland.is(SturdyFarmlandBlockTags.HYDRATING_FARMLAND)) {
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
    private static boolean isGlassAboveBlock(ServerLevel level, BlockPos cropPos) {
        for (int i = 0; i < 16; ++i) {
            if (level.getBlockState(cropPos.offset(0, i + 1, 0)).is(ModTags.Blocks.GREENHOUSE_GLASS)) {
                return true;
            }
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
