package cool.bot.dewdropfarmland.block;


import cool.bot.botslib.util.RNG;
import cool.bot.botslib.util.Util;
import cool.bot.dewdropfarmland.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;

import static cool.bot.dewdropfarmland.utils.CropHandlerUtils.growCrop;

// Override vanilla Farmland
public class GardenPotBlock extends FarmBlock implements IForgeBlock {
    public static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 15, 10, 15),
            box(0, 10, 0, 16, 16, 16));

    public static final BooleanProperty DELUXE = BooleanProperty.create("deluxe");

    public GardenPotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DELUXE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(DELUXE);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos.relative(facing));
        return plantType == PlantType.CROP || plantType == PlantType.PLAINS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (Config.rainWatering || Config.dailyReset) {

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
                boolean hydrated = Util.isMoistWaterable(level, pos);
                BlockPos abovePos = pos.above();
                BlockState crop = level.getBlockState(abovePos);
                if (hydrated && !(crop.getBlock() instanceof AirBlock)) {
                    growCrop(crop, level, abovePos, farmland, random, false, true);
                }
                if (RNG.mc_ihundo(random, Config.dailyDryChance)) {
                    if (!farmland.getValue(DELUXE)) {
                        Util.setDry(level, pos);
                    }
                }


            }

        }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
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
