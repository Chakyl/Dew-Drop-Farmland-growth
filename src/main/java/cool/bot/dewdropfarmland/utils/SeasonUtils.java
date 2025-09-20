package cool.bot.dewdropfarmland.utils;

import cool.bot.botslib.tag.DewDropBlockTags;
import cool.bot.dewdropfarmland.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.init.ModTags;

public class SeasonUtils {

    public static boolean isGlassAboveBlock(ServerLevel level, BlockPos cropPos) {
        BlockState scannedBlock;
        for (int i = 0; i < 16; ++i) {
            scannedBlock = level.getBlockState(cropPos.offset(0, i + 1, 0));
            if (Config.strictGreenhouses && scannedBlock.is(DewDropBlockTags.WATERABLE)) return false;
            if (scannedBlock.is(ModTags.Blocks.GREENHOUSE_GLASS)) {
                return true;
            }
        }
        return false;
    }
}
