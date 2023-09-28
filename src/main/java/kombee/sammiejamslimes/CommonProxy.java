package kombee.sammiejamslimes;

import kombee.sammiejamslimes.config.ConfigFolderHandler;
import kombee.sammiejamslimes.data.DataManager;
import kombee.sammiejamslimes.entities.SlimeEntityRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class CommonProxy {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);


    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Calling preInit in CommonProxy");
        ConfigFolderHandler.preInit(event);
        DataManager.loadSlimeData();
    }

    public void init() {
        LOGGER.info("Calling init in CommonProxy");
        SlimeEntityRegistry.registerEntities();
        MinecraftForge.EVENT_BUS.register(new SlimeEntityRegistry());
    }
    public void postInit() {
        LOGGER.info("Calling postInit in CommonProxy");
    }
}

