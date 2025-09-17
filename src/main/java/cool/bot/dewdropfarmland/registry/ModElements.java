package cool.bot.dewdropfarmland.registry;

import cool.bot.dewdropfarmland.DewDropFarmland;
import cool.bot.dewdropfarmland.block.CustomFarmland;
import cool.bot.dewdropfarmland.block.CustomStemBlock;
import cool.bot.dewdropfarmland.block.TilledSand;
import cool.bot.dewdropfarmland.item.FertilizerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModElements {
    public static final DeferredRegister<Block> VANILLA_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
    public static final DeferredRegister<Block> FARMLAND_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DewDropFarmland.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DewDropFarmland.MODID);

    public static final RegistryObject<Block> PUMPKIN_STEM = registerVanillaBlock("pumpkin_stem",
            () -> new CustomStemBlock((StemGrownBlock) Blocks.PUMPKIN, () -> { return Items.PUMPKIN_SEEDS; }, BlockBehaviour.Properties.copy(Blocks.PUMPKIN_STEM)));
    public static final RegistryObject<Block> MELON_STEM = registerVanillaBlock("melon_stem",
            () -> new CustomStemBlock((StemGrownBlock) Blocks.MELON, () ->  { return Items.MELON_SEEDS; }, BlockBehaviour.Properties.copy(Blocks.MELON_STEM)));

    public static final RegistryObject<Block> FARMLAND = registerVanillaBlock("farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> WEAK_FERTILIZED_FARMLAND = registerModBlock("weak_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> STRONG_FERTILIZED_FARMLAND = registerModBlock("strong_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> HYPER_FERTILIZED_FARMLAND = registerModBlock("hyper_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> HYDRATING_FARMLAND = registerModBlock("hydrating_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> BOUNTIFUL_FERTILIZED_FARMLAND = registerModBlock("bountiful_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> LOW_QUALITY_FERTILIZED_FARMLAND = registerModBlock("low_quality_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> HIGH_QUALITY_FERTILIZED_FARMLAND = registerModBlock("high_quality_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> PRISTINE_QUALITY_FERTILIZED_FARMLAND = registerModBlock("pristine_quality_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));

    public static final RegistryObject<Block> TILLED_SAND = registerModBlock("tilled_sand",
            () -> new TilledSand(BlockBehaviour.Properties.copy(Blocks.SAND)));
    public static final RegistryObject<Block> HYPER_FERTILIZED_SAND = registerModBlock("hyper_fertilized_sand",
            () -> new TilledSand(BlockBehaviour.Properties.copy(Blocks.SAND)));
//    public static final RegistryObject<Block> LOW_QUALITY_FERTILIZED_SAND = registerModBlock("low_quality_fertilized_sand",
//            () -> new TilledSand(BlockBehaviour.Properties.copy(Blocks.SAND)));
//    public static final RegistryObject<Block> HIGH_QUALITY_FERTILIZED_SAND = registerModBlock("high_quality_fertilized_sand",
//            () -> new TilledSand(BlockBehaviour.Properties.copy(Blocks.SAND)));
//    public static final RegistryObject<Block> PRISTINE_QUALITY_FERTILIZED_SAND = registerModBlock("pristine_quality_fertilized_sand",
//            () -> new TilledSand(BlockBehaviour.Properties.copy(Blocks.SAND)));

    public static final RegistryObject<BlockItem> WEAK_FERTILIZED_FARMLAND_ITEM = registerItem("weak_fertilized_farmland",
            () -> new BlockItem(WEAK_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> STRONG_FERTILIZED_FARMLAND_ITEM = registerItem("strong_fertilized_farmland",
            () -> new BlockItem(STRONG_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HYPER_FERTILIZED_FARMLAND_ITEM = registerItem("hyper_fertilized_farmland",
            () -> new BlockItem(HYPER_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HYDRATING_FARMLAND_ITEM = registerItem("hydrating_farmland",
            () -> new BlockItem(HYDRATING_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> BOUNTIFUL_FERTILIZED_FARMLAND_ITEM = registerItem("bountiful_fertilized_farmland",
            () -> new BlockItem(BOUNTIFUL_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> LOW_QUALITY_FERTILIZED_ITEM = registerItem("low_quality_fertilized_farmland",
            () -> new BlockItem(LOW_QUALITY_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HIGH_QUALITY_FERTILIZED_FARMLAND_ITEM = registerItem("high_quality_fertilized_farmland",
            () -> new BlockItem(HIGH_QUALITY_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> PRISTINE_QUALITY_FERTILIZED_FARMLAND_ITEM = registerItem("pristine_quality_fertilized_farmland",
            () -> new BlockItem(PRISTINE_QUALITY_FERTILIZED_FARMLAND.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> TILLED_SAND_ITEM = registerItem("tilled_sand",
            () -> new BlockItem(TILLED_SAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HYPER_FERTILIZED_SAND_ITEM = registerItem("hyper_fertilized_sand",
            () -> new BlockItem(HYPER_FERTILIZED_SAND.get(), new Item.Properties()));
//    public static final RegistryObject<BlockItem> LOW_QUALITY_FERTILIZED_SAND_ITEM = registerItem("low_quality_fertilized_sand",
//            () -> new BlockItem(LOW_QUALITY_FERTILIZED_SAND.get(), new Item.Properties()));
//    public static final RegistryObject<BlockItem> HIGH_QUALITY_FERTILIZED_SAND_ITEM = registerItem("high_quality_fertilized_sand",
//            () -> new BlockItem(HIGH_QUALITY_FERTILIZED_SAND.get(), new Item.Properties()));
//    public static final RegistryObject<BlockItem> PRISTINE_QUALITY_FERTILIZED_SAND_ITEM = registerItem("pristine_quality_fertilized_sand",
//            () -> new BlockItem(PRISTINE_QUALITY_FERTILIZED_SAND.get(), new Item.Properties()));

    public static final RegistryObject<Item> WEAK_FERTILIZER = registerItem("weak_fertilizer", () -> new FertilizerItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> STRONG_FERTILIZER = registerItem("strong_fertilizer", () -> new FertilizerItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64)));
    public static final RegistryObject<Item> HYPER_FERTILIZER = registerItem("hyper_fertilizer", () -> new FertilizerItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(64)));
    public static final RegistryObject<Item> HYDRATING_FERTILIZER = registerItem("hydrating_fertilizer", () -> new FertilizerItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BOUNTIFUL_FERTILIZER = registerItem("bountiful_fertilizer", () -> new FertilizerItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> LOW_QUALITY_FERTILIZER = registerItem("low_quality_fertilizer", () -> new FertilizerItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> HIGH_QUALITY_FERTILIZER = registerItem("high_quality_fertilizer", () -> new FertilizerItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64)));
    public static final RegistryObject<Item> PRISTINE_QUALITY_FERTILIZER = registerItem("pristine_quality_fertilizer", () -> new FertilizerItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(64)));

    private static <T extends Block> RegistryObject<T> registerVanillaBlock(String name, Supplier<T> block) {
        return VANILLA_BLOCKS.register(name, block);
    }
    private static <T extends Block> RegistryObject<T> registerModBlock(String name, Supplier<T> block) {
        return FARMLAND_BLOCKS.register(name, block);
    }
    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        return CreativeTab.addToTab(ITEMS.register(name, item));
    }


    public static void register(IEventBus eventBus) {
        VANILLA_BLOCKS.register(eventBus);
        FARMLAND_BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
