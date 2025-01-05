package cool.bot.dewdropfarmland.registry;

import cool.bot.dewdropfarmland.DewDropFarmland;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = DewDropFarmland.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DewDropFarmland.MODID);

    public static final List<Supplier<? extends ItemLike>> DEW_DROP_FARMLAND_TAB_ITEMS = new ArrayList<>();

    public static final RegistryObject<CreativeModeTab> DEW_DROP_FARMLAND_TAB = TABS.register("dew_drop_farmland_growth",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.dew_drop_farmland_growth"))
                    .icon(ModElements.STRONG_FERTILIZER.get()::getDefaultInstance)
                    .displayItems((displayParams, output) -> {
                        DEW_DROP_FARMLAND_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get()));
                    })
                    .build()
        );

    public static <T extends Item> RegistryObject<T> addToTab(RegistryObject<T> itemLike) {
        DEW_DROP_FARMLAND_TAB_ITEMS.add(itemLike);
        return itemLike;
    }
}
