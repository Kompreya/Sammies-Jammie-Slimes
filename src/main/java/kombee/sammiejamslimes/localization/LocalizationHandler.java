package kombee.sammiejamslimes.localization;

import kombee.sammiejamslimes.data.DataManager;
import kombee.sammiejamslimes.data.SammieJamSlimeData;

import java.io.*;
import java.util.List;

// Creates and populates a lang file in the config dir using the registered slime unloc names
// TODO: Perhaps let the user specify the language? Also, provide instructions to the user to place the lang in resourcepack
public class LocalizationHandler {

    public static void generateLocalizationFile() {
        List<SammieJamSlimeData> slimeDataList = DataManager.getSlimeDataList();

        File langDir = new File("config/sammiejamslimes/lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        File langFile = new File(langDir, "en_us.lang");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(langFile))) {
            for (SammieJamSlimeData slimeData : slimeDataList) {
                String entityID = slimeData.getEntityID();
                String displayName = slimeData.getDisplayName();

                // Write unlocalized name
                writer.write("entity." + entityID + ".name=" + displayName);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

