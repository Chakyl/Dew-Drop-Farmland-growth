package cool.bot.dewdropfarmland;

import com.mojang.logging.LogUtils;
import cool.bot.dewdropfarmland.block.FarmlandEventHandler;
import cool.bot.dewdropfarmland.registry.CreativeTab;
import cool.bot.dewdropfarmland.registry.ModElements;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DewDropFarmland.MODID)
public class DewDropFarmland {
    public static final String MODID = "dew_drop_farmland_growth";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean FARMERS_DELIGHT_INSTALLED = false;
    public static boolean VINTAGEDELIGHT_INSTALLED = false;
    public static boolean SUPPLEMENTARIES_INSTALLED = false;
    public static boolean CULTURAL_DELIGHTS_INSTALLED = false;

    public DewDropFarmland() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModElements.register(modEventBus);
        CreativeTab.TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(FarmlandEventHandler.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

}
