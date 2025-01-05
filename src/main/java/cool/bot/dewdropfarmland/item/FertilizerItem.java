package cool.bot.dewdropfarmland.item;

import cool.bot.dewdropfarmland.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FertilizerItem extends Item {
    public FertilizerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) pContext.getLevel();
            BlockPos posClicked = pContext.getClickedPos();
            BlockState state = level.getBlockState(posClicked);
            Player player = pContext.getPlayer();
            ItemStack stack = pContext.getItemInHand();

            boolean targetingCrop = state.is(BlockTags.CROPS) && level.getBlockState(posClicked.below()).is(Blocks.FARMLAND);
            if (level.getBlockState(posClicked).is(Blocks.FARMLAND) || targetingCrop) {
                if (targetingCrop) {
                    posClicked = posClicked.below();
                    state = level.getBlockState(posClicked);
                }
                if (stack.is(ModElements.WEAK_FERTILIZER.get())) {
                    level.setBlock(posClicked, ModElements.WEAK_FERTILIZED_FARMLAND.get().defaultBlockState().setValue(BlockStateProperties.MOISTURE, state.getValue(BlockStateProperties.MOISTURE)), 3);
                } else if (stack.is(ModElements.STRONG_FERTILIZER.get())) {
                    level.setBlock(posClicked, ModElements.STRONG_FERTILIZED_FARMLAND.get().defaultBlockState(), 3);
                } else if (stack.is(ModElements.HYDRATING_FERTILIZER.get())) {
                    level.setBlock(posClicked, ModElements.HYDRATING_FARMLAND.get().defaultBlockState(), 3);
                } else if (stack.is(ModElements.BOUNTIFUL_FERTILIZER.get())) {
                    level.setBlock(posClicked, ModElements.BOUNTIFUL_FERTILIZED_FARMLAND.get().defaultBlockState(), 3);
                } else {
                    return InteractionResult.FAIL;
                }
                if (player != null && !player.isCreative()) {
                    stack.shrink(1);
                }
                level.playSound(null, posClicked.getX(), posClicked.getY(), posClicked.getZ(), SoundEvents.GRASS_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }
}
