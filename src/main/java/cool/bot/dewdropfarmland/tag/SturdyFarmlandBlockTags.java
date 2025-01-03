package cool.bot.dewdropfarmland.tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;

public class SturdyFarmlandBlockTags {
    public static final TagKey<Block> WEAK_FERTILIZER = tag("weak_fertilizer");
    public static final TagKey<Block> STRONG_FERTILIZER = tag("strong_fertilizer");
    public static final TagKey<Block> HYDRATING_FERTILIZER = tag("hydrating_fertilizer");
    public static TagKey<Block> tag(String name) {
        return BlockTags.create(new ResourceLocation("sturdy_farmland", name));
    }
}