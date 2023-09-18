package kombee.sammiejamslimes;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class JSONFileLoader {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    // SoC: Parsing and validating slimes.json before storing in SammieJamSlimeData
    // Here we perform a grand manner of validation checks on slimes.json.
    // Hard reject or expect critical value types and formatting, throw error indicating expected correct formatting or type.
    // Soft accept some values, sanitize as needed, validate and store.
    // Prevent crashes when possible. Provide fallbacks. Throw warnings indicating fallback used.
    public static SammieJamSlimeData[] loadSlimeData(String filePath, boolean loadDefaultOnFail) {
        Set<String> encounteredEntityIDs = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            FileReader fileReader = new FileReader(filePath);
            JsonArray jsonArray = new JsonArray();
            String line;
            int lineNumber = 0;
            String currentEntityID = null; // Track the current entityID. We do this, so we can associate properties to entityID, as entityID is required for each slime object.
            String currentDisplayName = null; // Track the current displayName

            // Track line number for use in logging.
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines or lines with only whitespace
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    // Attempt to parse each line as JSON
                    JsonElement jsonElement = jsonParser.parse(line);

                    if (jsonElement.isJsonObject()) {
                        // Check if the JSON object has an "entityID" property
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement entityIDElement = jsonObject.get("entityID");

                        if (entityIDElement != null && entityIDElement.isJsonPrimitive() && entityIDElement.getAsString() != null) {
                            currentEntityID = entityIDElement.getAsString();

                            // Validate the entityID format
                            if (!isValidEntityID(currentEntityID)) {
                                LOGGER.error("Line {}: Invalid entityID format for '{}': {}. Skipping.", lineNumber, currentEntityID, line);
                                continue; // Skip invalid entityID
                            }
                        } else {
                            currentEntityID = null;
                            LOGGER.warn("Line {}: JSON object missing or has an invalid 'entityID' property. Skipping.", lineNumber);
                            continue;
                        }

                        // Check if the JSON object has a "displayName" property
                        JsonElement displayNameElement = jsonObject.get("displayName");

                        if (displayNameElement != null && displayNameElement.isJsonPrimitive() && displayNameElement.getAsString() != null) {
                            currentDisplayName = displayNameElement.getAsString();
                            // Validate the displayName here
                            if (!isValidDisplayName(currentDisplayName)) {
                                LOGGER.warn("Line {}: Invalid displayName format for '{}': {}. Using as-is.", lineNumber, currentEntityID, currentDisplayName);
                            }
                        } else {
                            LOGGER.warn("Line {}: 'displayName' property missing or invalid for '{}'. Using as-is.", lineNumber, currentEntityID);
                        }

                        jsonArray.add(jsonObject);
                    } else {
                        LOGGER.warn("Line {}: Ignored invalid JSON data: {}", lineNumber, line);
                    }
                } catch (JsonParseException e) {
                    LOGGER.warn("Line {}: Ignored invalid JSON data: {}", lineNumber, line);
                }
            }

            SammieJamSlimeData[] slimeDataArray = new SammieJamSlimeData[jsonArray.size()];

            int i = 0;
            for (JsonElement element : jsonArray) {
                SammieJamSlimeData slimeData = gson.fromJson(element, SammieJamSlimeData.class);

                // Add the slimeData object to your array
                slimeDataArray[i] = slimeData;
                encounteredEntityIDs.add(slimeData.getEntityID());
                i++;
            }

            return slimeDataArray;
        } catch (IOException e) {
            e.printStackTrace();
            if (loadDefaultOnFail) {
                // Load the default data from the mod's resources folder. I provided a default slimes.json
            }
        }
        return null; // Return null if the file is empty or there was an IO error.
    }

    // Place json validation methods and their respective validation conditions here.
    //

    // Validate entityID format
    private static boolean isValidEntityID(String entityID) {
        if (entityID == null || entityID.isEmpty()) {
            return false;
        }

        // Replace spaces with underscores and convert to lowercase
        String sanitizedEntityID = entityID.replaceAll(" ", "_").toLowerCase();

        // Check if the sanitized entityID matches the desired format: lowercase, alphanumeric, underscore
        if (sanitizedEntityID.matches("[a-z0-9_]+")) {
            // Check if the original entityID contained uppercase characters or spaces
            if (!entityID.equals(sanitizedEntityID)) {
                // Warn the user about the sanitization
                LOGGER.warn("EntityID '{}' contains uppercase characters or spaces. Converted to '{}'.", entityID, sanitizedEntityID);
            }
            return true;
        } else {
            // If the sanitized entityID doesn't match the format, it's invalid
            return false;
        }
    }

    // Validate displayName format
    // TODO: Check for caveats with localized name formatting. Generally, any formatting is acceptable, but let's warn users for caveats.
    // For now, we're just sanitizing values containing only whitespace, which then reverts back to the unlocalized name. Unlocalized names will still get added to en_us.lang in the resources folder to each entity.
    private static boolean isValidDisplayName(String displayName) {
        // Check if the displayName is not null
        if (displayName == null) {
            return false;
        }

        // Sanitize spaces by removing them
        String sanitizedDisplayName = displayName.replaceAll("\\s", "");

        // Check if the sanitized displayName is empty
        if (sanitizedDisplayName.isEmpty()) {
            return false; // Treat it as missing
        }

        // If the displayName has passed all checks, consider it valid
        return true;
    }

}