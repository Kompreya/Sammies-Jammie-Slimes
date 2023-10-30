package kombee.sammiejamslimes.config;

import kombee.sammiejamslimes.data.JSONFileLoader;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.SammieJamSlimes;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

// TODO: Provide slimes.json documentation. Serve this to end-user's config folder from mod resources, allow safe deletion and link to wiki.
public class ConfigFolderHandler {

    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public static void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), "sammiejamslimes");

        if (!configDir.exists()) {
            LOGGER.info("Creating sammiejamslimes folder at: {}", configDir.getAbsolutePath());
            configDir.mkdirs();

            // Create the textures folder inside sammiejamslimes. Saving this for later when building the resourcepack helper
            //File texturesFolder = new File(configDir, "textures");
            //texturesFolder.mkdirs();

            LOGGER.info("Copying slimes.json from resources to: {}", new File(configDir, "slimes.json").getAbsolutePath());

            File configFile = new File(configDir, "slimes.json");

            if (!configFile.exists()) {
                LOGGER.info("Copying slimes.json from resources to: {}", configFile.getAbsolutePath());
                copyResourceToFile("/assets/sammiejamslimes/slimes.json", configFile);
            }

            SammieJamSlimeData[] loadedData = JSONFileLoader.loadSlimeData(configFile.getPath(), true);

            if (loadedData == null || loadedData.length == 0) {
                LOGGER.error("Failed to generate default slimes.json file in config/sammiejamslimes, loading defaults. Please check your system.");
            } else {
                LOGGER.info("Loaded custom SammieJamSlime data from config.");
            }

        }
    }

    private static void copyResourceToFile(String resourcePath, File destinationFile) {
        try {
            InputStream inputStream = ConfigFolderHandler.class.getResourceAsStream(resourcePath);
            if (inputStream != null) {
                Files.copy(inputStream, destinationFile.toPath());
            } else {
                LOGGER.error("Failed to copy resource: {}", resourcePath);
            }
        } catch (IOException e) {
            LOGGER.error("Error while copying resource: {}", e);
        }
    }

}
