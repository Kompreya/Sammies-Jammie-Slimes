package kombee.sammiejamslimes;

import kombee.sammiejamslimes.entities.SlimeEntityRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;

import java.io.File;

@Mod(modid = SammieJamSlimes.MODID, version = SammieJamSlimes.VERSION)
public class SammieJamSlimes {

    public static final String MODID = "sammiejamslimes";
    public static final String VERSION = "0.1";
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public static final String ProxyClientClass = "kombee.sammiejamslimes.ClientProxy";
    public static final String ProxyServerClass = "kombee.sammiejamslimes.CommonProxy";
    @SidedProxy(clientSide = ProxyClientClass, serverSide = ProxyServerClass)
    public static CommonProxy proxy;

    private File modConfigurationDir;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Pre-Initialization phase started.");
        modConfigurationDir = event.getModConfigurationDirectory();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Initialization phase started.");
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Post-Initialization phase started.");
        proxy.postInit();
    }

    @Mod.Instance
    public static SammieJamSlimes instance;
}
