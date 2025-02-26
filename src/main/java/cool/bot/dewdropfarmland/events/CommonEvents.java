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
            event.enqueueWork(() -> DewDropFarmland.FARMERS_DELIGHT_INSTALLED = ModList.get().isLoaded("farmersdelight"));
            event.enqueueWork(() -> DewDropFarmland.VINTAGEDELIGHT_INSTALLED = ModList.get().isLoaded("vintagedelight"));
        }
    }
}