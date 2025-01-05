package cool.bot.dewdropfarmland.tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;

public class SturdyFarmlandBlockTags {
    public static final TagKey<Block> WEAK_FERTILIZED_FARMLAND = tag("weak_fertilized_farmland");
    public static final TagKey<Block> STRONG_FERTILIZED_FARMLAND = tag("strong_fertilized_farmland");
    public static final TagKey<Block> BOUNTIFUL_FERTILIZED_FARMLAND = tag("bountiful_fertilized_farmland");
    public static final TagKey<Block> HYDRATING_FARMLAND = tag("hydrating_farmland");

    public static TagKey<Block> tag(String name) {
        return BlockTags.create(new ResourceLocation("dew_drop_farmland_growth", name));
    }
}