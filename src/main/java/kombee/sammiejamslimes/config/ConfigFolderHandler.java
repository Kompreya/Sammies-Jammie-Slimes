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

// TODO: Provide slimes.json documentation. Serve this to end-user's config folder from mod resources, allow safe deletion and link to wiki.
public class ConfigFolderHandler {

    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public static void preInit(FMLPreInitializationEvent event) {
        // Check if the sammiejamslimes folder exists in the config directory
        File configDir = new File(event.getModConfigurationDirectory(), "sammiejamslimes");

        if (!configDir.exists()) {
            LOGGER.info("Creating sammiejamslimes folder at: {}", configDir.getAbsolutePath());
            // Create the sammiejamslimes folder
            configDir.mkdirs();

            // Create the textures folder inside sammiejamslimes
            File texturesFolder = new File(configDir, "textures");
            texturesFolder.mkdirs();

            LOGGER.info("Copying slimes.json from resources to: {}", new File(configDir, "slimes.json").getAbsolutePath());

            // Pass true to loadDefaultOnFail
            SammieJamSlimeData[] loadedData = JSONFileLoader.loadSlimeData("config/sammiejamslimes/slimes.json", true);

            // Check if the loaded data is null or empty (indicating a failure)
            if (loadedData == null || loadedData.length == 0) {
                LOGGER.error("Failed to generate default slimes.json file in config/sammiejamslimes, loading defaults. Please check your system.");
                // Continue with loading the default data
            } else {
                // Data loaded successfully
                LOGGER.info("Loaded custom SammieJamSlime data from config.");
            }

            LOGGER.info("Copying melonslime.png from resources to: {}", new File(texturesFolder, "melonslime.png").getAbsolutePath());
            copyResourceToFile("/assets/sammiejamslimes/textures/entity/melonslime.png", new File(texturesFolder, "melonslime.png"));
            LOGGER.info("Copying diamondslime.png from resources to: {}", new File(texturesFolder, "diamondslime.png").getAbsolutePath());
            copyResourceToFile("/assets/sammiejamslimes/textures/entity/diamondslime.png", new File(texturesFolder, "diamondslime.png"));
        }
    }


    private static boolean copyResourceToFile(String resourceName, File targetFile) {
        try (InputStream resourceStream = ConfigFolderHandler.class.getResourceAsStream(resourceName)) {
            if (resourceStream != null) {
                FileUtils.copyInputStreamToFile(resourceStream, targetFile);
                return true; // Return true if the copy was successful
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Return false if the copy failed
    }
}
