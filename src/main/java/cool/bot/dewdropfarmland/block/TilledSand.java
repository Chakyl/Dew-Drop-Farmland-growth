package cool.bot.dewdropfarmland.block;


import cool.bot.dewdropfarmland.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;

import static cool.bot.dewdropfarmland.utils.CropHandlerUtils.growCrop;

public class TilledSand extends FallingBlock implements IForgeBlock {
    public static final VoxelShape SHAPE = box(0, 0, 0, 16, 16, 16);

    public TilledSand(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        ItemStack itemStack = context.getItemInHand();

        if (!itemStack.canPerformAction(toolAction)) {
            return null;
        }

        if (ToolActions.SHOVEL_FLATTEN == toolAction && Config.shovelReverting) {
            return Blocks.SAND.defaultBlockState();
        }

        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos.relative(facing));
        BlockState plant = plantable.getPlant(world, pos.relative(facing));

        return plantType == PlantType.DESERT;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 10);

        if (!Config.dailyReset || !level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).get()) {
            return;
        }
        long dayTime = level.getDayTime() % 24000;

        if (dayTime >= Config.dailyTimeMin && dayTime < Config.dailyTimeMin + 10) {
            BlockState farmland = level.getBlockState(pos);
            BlockPos abovePos = pos.above();
            BlockState crop = level.getBlockState(abovePos);
            if (!(crop.getBlock() instanceof AirBlock)) {
                growCrop(crop, level, abovePos, farmland, random, true);
            }
        }
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!level.isClientSide()) {
            ((ServerLevel) level).scheduleTick(pos, this, 10); // prevent rescheduling
        }
        super.onBlockStateChange(level, pos, oldState, newState);
    }
}
