package cool.bot.dewdropfarmland.tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;

public class SturdyFarmlandBlockTags {
    public static final TagKey<Block> WEAK_FERTILIZED_FARMLAND = tag("weak_fertilized_farmland");
    public static final TagKey<Block> STRONG_FERTILIZED_FARMLAND = tag("strong_fertilized_farmland");
    public static final TagKey<Block> HYPER_FERTILIZED_FARMLAND = tag("hyper_fertilized_farmland");
    public static final TagKey<Block> BOUNTIFUL_FERTILIZED_FARMLAND = tag("bountiful_fertilized_farmland");
    public static final TagKey<Block> HYDRATING_FARMLAND = tag("hydrating_farmland");
    public static final TagKey<Block> LOW_QUALITY_FERTILIZED_FARMLAND = tag("low_quality_fertilized_farmland");
    public static final TagKey<Block> HIGH_QUALITY_FERTILIZED_FARMLAND = tag("high_quality_farmland");
    public static final TagKey<Block> PRISTINE_QUALITY_FERTILIZED_FARMLAND = tag("pristine_quality_farmland");
    // Sprinkler tags
    public static final TagKey<Block> SPRINKLER_TIER_1 = tag("sprinkler_tier_1");
    public static final TagKey<Block> SPRINKLER_TIER_2 = tag("sprinkler_tier_2");
    public static final TagKey<Block> SPRINKLER_TIER_3 = tag("sprinkler_tier_3");
    public static final TagKey<Block> SPRINKLER_TIER_4 = tag("sprinkler_tier_4");

    public static TagKey<Block> tag(String name) {
        return BlockTags.create(new ResourceLocation("dew_drop_farmland_growth", name));
    }
}