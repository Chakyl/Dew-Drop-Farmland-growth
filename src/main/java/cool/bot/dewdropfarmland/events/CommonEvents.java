package cool.bot.dewdropfarmland.events;

import cool.bot.dewdropfarmland.DewDropFarmland;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    @Mod.EventBusSubscriber(modid = DewDropFarmland.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent

        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> DewDropFarmland.SERENE_SEASONS_INSTALLED = ModList.get().isLoaded("sereneseasons"));
            event.enqueueWork(() -> DewDropFarmland.FARMERS_DELIGHT_INSTALLED = ModList.get().isLoaded("farmersdelight"));
            event.enqueueWork(() -> DewDropFarmland.VINTAGEDELIGHT_INSTALLED = ModList.get().isLoaded("vintagedelight"));
            event.enqueueWork(() -> DewDropFarmland.SUPPLEMENTARIES_INSTALLED = ModList.get().isLoaded("supplementaries"));
            event.enqueueWork(() -> DewDropFarmland.CULTURAL_DELIGHTS_INSTALLED = ModList.get().isLoaded("culturaldelights"));
            event.enqueueWork(() -> DewDropFarmland.WINDSWEPT_INSTALLED = ModList.get().isLoaded("windswept"));
            event.enqueueWork(() -> DewDropFarmland.VINERY_INSTALLED = ModList.get().isLoaded("vinery"));
            event.enqueueWork(() -> DewDropFarmland.FLOWERARY_INSTALLED = ModList.get().isLoaded("flowerary"));
            event.enqueueWork(() -> DewDropFarmland.COBBLEMON_INSTALLED = ModList.get().isLoaded("cobblemon"));
        }
    }
}