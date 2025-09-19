package cool.bot.dewdropfarmland.block;


import cool.bot.botslib.util.RNG;
import cool.bot.botslib.util.Util;
import cool.bot.dewdropfarmland.Config;
import cool.bot.dewdropfarmland.tag.SturdyFarmlandBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;

import static cool.bot.dewdropfarmland.utils.CropHandlerUtils.growCrop;
import static cool.bot.dewdropfarmland.utils.CropHandlerUtils.isNearSprinkler;

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

        if (!itemStack.canPerformAction(toolAction)) {
            return null;
        }

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

        level.scheduleTick(pos, this, 10);

        if (level.isRainingAt(pos.above()) && Config.rainWatering) Util.setMoist(level, pos);

        if (level.getFluidState(pos.above()).getType() == Fluids.WATER) Util.setMoist(level, pos);

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
                    growCrop(crop, level, abovePos, farmland, random, false);
                }
                if (RNG.mc_ihundo(random, Config.dailyDryChance)) {
                    if (hydrated && Util.isDryWaterable(level, pos)) {
                        Util.setMoist(level, pos);
                    }
                    if (!hydrated && farmland.is(SturdyFarmlandBlockTags.HYDRATING_FARMLAND) && crop.getBlock() instanceof CropBlock cropBlock && cropBlock.getAge(crop) < Math.floor((float) cropBlock.getMaxAge() / 2)) {
                        hydrated = true;
                    }
                    if (!hydrated && farmland.is(SturdyFarmlandBlockTags.HYDRATING_FARMLAND)) {
                        hydrated = true;
                    }
                    if (!hydrated) {
                        Util.setDry(level, pos);
                    }
                }
            }
        }

        if (!Config.sturdyFarmland) {
            super.tick(state, level, pos, random);
        }
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
