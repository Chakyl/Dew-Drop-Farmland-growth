package cool.bot.dewdropfarmland.mixin;

import cool.bot.dewdropfarmland.Config;
import cool.bot.dewdropfarmland.DewDropFarmland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.vinery.core.block.LatticeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cool.bot.dewdropfarmland.utils.VineryMixins.handleLatticeGrowth;

@Mixin(value = LatticeBlock.class)
public abstract class LatticeBlockMixin {

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        world.scheduleTick(pos, state.getBlock(), 10);

        if (!Config.dailyReset || !world.getGameRules().getRule(GameRules.RULE_DAYLIGHT).get()) {
            return;
        }
        long dayTime = world.getDayTime() % 24000;
        if (dayTime >= Config.dailyTimeMin && dayTime < Config.dailyTimeMin + 10) {
            handleLatticeGrowth(state, world, pos);
            world.scheduleTick(pos, state.getBlock(), 10);
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!world.isClientSide()) {
            ((ServerLevel) world).scheduleTick(pos, state.getBlock(), 10); // prevent rescheduling
        }
    }
}