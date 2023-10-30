package kombee.sammiejamslimes.data;

import kombee.sammiejamslimes.SammieJamSlimes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private static List<SammieJamSlimeData> slimeDataList = new ArrayList<>();

    public static void loadSlimeData() {
        SammieJamSlimeData[] slimeDataArray = JSONFileLoader.loadSlimeData("config/sammiejamslimes/slimes.json", true);

        if (slimeDataArray != null) {
            LOGGER.info("Loaded SammieJamSlime data:");

            slimeDataList.clear();
            slimeDataList.addAll(Arrays.asList(slimeDataArray));

            for (SammieJamSlimeData slimeData : slimeDataList) {
                //LOGGER.info("Entity ID: {}", slimeData.getEntityID());
                //LOGGER.info("Display Name: {}", slimeData.getDisplayName());
                // Add more logging as needed for data inspection. Can probably remove this closer to release.
            }
        } else {
            LOGGER.warn("Failed to load SammieJamSlime data. This may be due to one of the following reasons:\n"
                    + "1. The 'slimes.json' file is missing or incorrectly located in 'config/sammiejamslimes'.\n"
                    + "2. The 'slimes.json' file is empty or contains invalid JSON data.\n"
                    + "3. There was an I/O error while attempting to load the file.\n"
                    + "Please check your mod's configuration and ensure the 'slimes.json' file is properly formatted and located.");
        }
    }

    public static List<SammieJamSlimeData> getSlimeDataList() {
        return slimeDataList;
    }
}
