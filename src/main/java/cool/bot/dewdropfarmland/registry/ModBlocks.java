package cool.bot.dewdropfarmland.registry;

import cool.bot.dewdropfarmland.DewDropFarmland;
import cool.bot.dewdropfarmland.block.CustomFarmland;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> VANILLA_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
    public static final DeferredRegister<Block> FARMLAND_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DewDropFarmland.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DewDropFarmland.MODID);

    public static final RegistryObject<Block> FARMLAND = registerVanillaBlock("farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));

    public static final RegistryObject<Block> WEAK_FERTILIZED_FARMLAND = registerModBlock("weak_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> STRONG_FERTILIZED_FARMLAND = registerModBlock("strong_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> HYDRATING_FARMLAND = registerModBlock("hydrating_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> BOUNTIFUL_FERTILIZED_FARMLAND = registerModBlock("bountiful_fertilized_farmland",
            () -> new CustomFarmland(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));

    public static final RegistryObject<BlockItem> WEAK_FERTILIZED_FARMLAND_ITEM = registerItem("weak_fertilized_farmland",
            () -> new BlockItem(WEAK_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> STRONG_FERTILIZED_FARMLAND_ITEM = registerItem("strong_fertilized_farmland",
            () -> new BlockItem(STRONG_FERTILIZED_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HYDRATING_FARMLAND_ITEM = registerItem("hydrating_farmland",
            () -> new BlockItem(HYDRATING_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> BOUNTIFUL_FERTILIZED_FARMLAND_ITEM = registerItem("bountiful_fertilized_farmland",
            () -> new BlockItem(BOUNTIFUL_FERTILIZED_FARMLAND.get(), new Item.Properties()));

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
