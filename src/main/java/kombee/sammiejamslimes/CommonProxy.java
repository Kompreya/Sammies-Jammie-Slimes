package kombee.sammiejamslimes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class CommonProxy {

    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private SammieJamSlimeData loadedSlimeData;

    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Calling preInit in CommonProxy");
        ConfigFolderHandler.preInit(event);
    }

    public void init() {
        LOGGER.info("Calling init in CommonProxy");
        SammieJamSlimeData[] slimeDataArray = JSONFileLoader.loadSlimeData("config/sammiejamslimes/slimes.json");

        if (slimeDataArray != null) {
            LOGGER.info("Loaded SammieJamSlime data:");

            for (SammieJamSlimeData slimeData : slimeDataArray) {
                LOGGER.info("Entity ID: {}", slimeData.getEntityID());
                LOGGER.info("Display Name: {}", slimeData.getDisplayName());
                // Add more logging as needed for data inspection
            }
        } else {
            LOGGER.warn("Failed to load SammieJamSlime data");
        }
    }
    public void postInit() {
        LOGGER.info("Calling postInit in CommonProxy");
        // Common post-initialization code for both client and server
    }

    public SammieJamSlimeData getLoadedSlimeData() {
        return loadedSlimeData;
    }
}

