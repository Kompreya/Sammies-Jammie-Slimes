package kombee.sammiejamslimes.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResourcePackUtil {

    public static void copyResourcesToGame(String modId, File sourceFolder) {
        File gameResourcePackFolder = new File("resourcepacks", "modresources_" + modId);

        if (!gameResourcePackFolder.exists()) {
            gameResourcePackFolder.mkdirs();
        }

        if (sourceFolder.exists() && sourceFolder.isDirectory()) {
            File targetFolder = new File(gameResourcePackFolder, "assets/sammiejamslimes/textures/entity");

            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }

            File[] files = sourceFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        File targetFile = new File(targetFolder, file.getName());

                        try {
                            Files.copy(file.toPath(), targetFile.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

