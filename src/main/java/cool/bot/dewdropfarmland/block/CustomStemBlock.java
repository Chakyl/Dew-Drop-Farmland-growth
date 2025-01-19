package cool.bot.dewdropfarmland.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.function.Supplier;

// Override vanilla StemBlocks (Pumpkins/Melons)
public class CustomStemBlock extends StemBlock implements IForgeBlock {
    public CustomStemBlock(StemGrownBlock fruit, Supplier<Item> seedSupplier, BlockBehaviour.Properties properties) {
        super(fruit, seedSupplier, properties);
    }

    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getAge(BlockState pState) {
        return pState.getValue(this.getAgeProperty());
    }

    public BlockState getStateForAge(int pAge) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(pAge));
    }

    public void placeFruit(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos fruitPos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(fruitPos.below());
            if (level.isEmptyBlock(fruitPos) && (blockstate.canSustainPlant(level, fruitPos.below(), Direction.UP, this.getFruit()) || blockstate.is(Blocks.FARMLAND) || blockstate.is(BlockTags.DIRT))) {
                level.setBlockAndUpdate(fruitPos, this.getFruit().defaultBlockState());
                BlockState newState = this.getStateForAge(0);
                level.setBlock(pos, newState, 2);
                break;
            }
        }
    }
}
