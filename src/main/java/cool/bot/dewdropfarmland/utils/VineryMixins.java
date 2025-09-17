package cool.bot.dewdropfarmland.utils;

import cool.bot.dewdropfarmland.DewDropFarmland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.LatticeBlock;
import net.satisfy.vinery.core.block.PaleStemBlock;
import net.satisfy.vinery.core.block.entity.LatticeBlockEntity;
import net.satisfy.vinery.core.util.GrapeType;
import sereneseasons.init.ModConfig;

import static cool.bot.dewdropfarmland.utils.SeasonUtils.isGlassAboveBlock;

public class VineryMixins {
    public static void handlePaleStemGrowth(BlockState state, ServerLevel world, BlockPos pos) {
        PaleStemBlock block = (PaleStemBlock) state.getBlock();
        int i;
        if (!block.isMature(state) && block.hasTrunk(world, pos) && state.getValue(PaleStemBlock.AGE) > 0 && world.getRawBrightness(pos, 0) >= 9 && (i = (Integer) state.getValue(PaleStemBlock.AGE)) < 4) {
            boolean canGrow = true;
            if (DewDropFarmland.SERENE_SEASONS_INSTALLED) {
                boolean fertile = SeasonUtils.isPaleStemFertile(state, world, pos);
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
                boolean fertile = SeasonUtils.isLatticeFertile(world, pos);
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
