package kombee.sammiejamslimes;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
            // Copy the JSON file from your mod's resources
            copyResourceToFile("/assets/sammiejamslimes/slimes.json", new File(configDir, "slimes.json"));

            LOGGER.info("Copying melonslime.png from resources to: {}", new File(texturesFolder, "melonslime.png").getAbsolutePath());
            // Copy additional texture files (adjust paths as needed)
            copyResourceToFile("/assets/sammiejamslimes/textures/entity/melonslime.png", new File(texturesFolder, "melonslime.png"));
            LOGGER.info("Copying diamondslime.png from resources to: {}", new File(texturesFolder, "diamondslime.png").getAbsolutePath());
            copyResourceToFile("/assets/sammiejamslimes/textures/entity/diamondslime.png", new File(texturesFolder, "diamondslime.png"));
        }
    }

    private static void copyResourceToFile(String resourceName, File targetFile) {
        try (InputStream resourceStream = ConfigFolderHandler.class.getResourceAsStream(resourceName)) {
            if (resourceStream != null) {
                FileUtils.copyInputStreamToFile(resourceStream, targetFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
