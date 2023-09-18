package kombee.sammiejamslimes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class JSONFileLoader {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public static SammieJamSlimeData[] loadSlimeData(String filePath, boolean loadDefaultOnFail) {
        try {
            FileReader fileReader = new FileReader(filePath);

            // Check if the file is empty (zero bytes)
            if (fileReader.ready()) {
                JsonArray jsonArray = jsonParser.parse(fileReader).getAsJsonArray();

                SammieJamSlimeData[] slimeDataArray = new SammieJamSlimeData[jsonArray.size()];
                Set<String> encounteredEntityIDs = new HashSet<>(); // Maintain encountered entityIDs

                int i = 0;
                for (JsonElement element : jsonArray) {
                    SammieJamSlimeData slimeData = gson.fromJson(element, SammieJamSlimeData.class);

                    // Check if the entityID property has an error and log it
                    if (slimeData.hasEntityIDError()) {
                        LOGGER.error("Invalid entityID found in slimes.json: {}", element);
                    }

                    // Check for duplicate entityID
                    if (!encounteredEntityIDs.add(slimeData.getEntityID())) {
                        LOGGER.error("Duplicate entityID found in slimes.json: {}. Will not be registered. Ensure entityID values are unique.", slimeData.getEntityID());
                    }

                    // Add the slimeData object to your array
                    slimeDataArray[i] = slimeData;
                    i++;
                }

                return slimeDataArray;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (loadDefaultOnFail) {
                // Load the default data from the mod's resources
                try (InputStream resourceStream = JSONFileLoader.class.getResourceAsStream("/assets/sammiejamslimes/slimes.json")) {
                    if (resourceStream != null) {
                        InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
                        JsonArray jsonArray = jsonParser.parse(reader).getAsJsonArray();

                        SammieJamSlimeData[] slimeDataArray = new SammieJamSlimeData[jsonArray.size()];

                        int i = 0;
                        for (JsonElement element : jsonArray) {
                            slimeDataArray[i] = gson.fromJson(element, SammieJamSlimeData.class);
                            i++;
                        }

                        return slimeDataArray;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null; // Return null if the file is empty or there was an IO error.
    }

}
