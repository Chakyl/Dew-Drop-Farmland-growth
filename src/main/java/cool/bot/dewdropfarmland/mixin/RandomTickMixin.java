package cool.bot.dewdropfarmland.mixin;

import cool.bot.dewdropfarmland.tag.SturdyFarmlandBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase")
public class RandomTickMixin {
    @Inject(method = "randomTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At("HEAD"), cancellable = true)
    private void randomTickInjection(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo ci) {
        if (pLevel.getBlockState(pPos).is(SturdyFarmlandBlockTags.CANCEL_RANDOM_TICK)) ci.cancel();
    }
}
