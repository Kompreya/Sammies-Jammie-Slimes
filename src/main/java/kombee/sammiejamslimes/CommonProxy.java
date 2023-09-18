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
        SammieJamSlimeData[] slimeDataArray = JSONFileLoader.loadSlimeData("config/sammiejamslimes/slimes.json", true);

        if (slimeDataArray != null) {
            LOGGER.info("Loaded SammieJamSlime data:");

            for (SammieJamSlimeData slimeData : slimeDataArray) {
                LOGGER.info("Entity ID: {}", slimeData.getEntityID());
                LOGGER.info("Display Name: {}", slimeData.getDisplayName());
                // Add more logging as needed for data inspection
            }
        } else {
            LOGGER.warn("Failed to load SammieJamSlime data. This may be due to one of the following reasons:\n"
                    + "1. The 'slimes.json' file is missing or incorrectly located in 'config/sammiejamslimes'.\n"
                    + "2. The 'slimes.json' file is empty or contains invalid JSON data.\n"
                    + "3. There was an I/O error while attempting to load the file.\n"
                    + "Please check your mod's configuration and ensure the 'slimes.json' file is properly formatted and located.");
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

