package kombee.sammiejamslimes;

import kombee.sammiejamslimes.SammieJamSlimeData;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JSONFileLoader {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private static final String FALLBACK_PRIMARY_COLOR = "FF0000";
    private static final String FALLBACK_SECONDARY_COLOR = "00FF00";

    // SoC: Parsing and validating slimes.json before storing in SammieJamSlimeData
    // Here we perform a grand manner of validation checks on slimes.json.
    // Hard reject or expect critical value types and formatting, throw error indicating expected correct formatting or type.
    // Soft accept some values, sanitize as needed, validate and store.
    // Prevent crashes when possible. Provide fallbacks. Throw warnings indicating fallback used.
    public static SammieJamSlimeData[] loadSlimeData(String filePath, boolean loadDefaultOnFail) {
        Set<String> encounteredEntityIDs = new HashSet<>();
        String currentEntityID = null; // Declare currentEntityID at a higher scope
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            JsonArray jsonArray = new JsonArray();
            String line;
            int lineNumber = 0;

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

                    // Is it a json object?
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        //<editor-fold desc="entityID parse and validate">
                        // BEGIN entityID validation
                        // Check if the JSON object has an "entityID" property
                        JsonElement entityIDElement = jsonObject.get("entityID");

                        if (entityIDElement != null && entityIDElement.isJsonPrimitive() && entityIDElement.getAsString() != null) {
                            currentEntityID = entityIDElement.getAsString();

                            // Validate the entityID format
                            if (!isValidEntityID(currentEntityID)) {
                                LOGGER.error("Line {}: Invalid entityID format for '{}': {}. Skipping.", lineNumber, currentEntityID, line);
                                continue; // Skip invalid entityID
                            }
                        } else {
                            LOGGER.warn("Line {}: JSON object missing or has an invalid 'entityID' property. Skipping.", lineNumber);
                            continue;
                        }
                        // END entityID validation
                        //</editor-fold>

                        //<editor-fold desc="displayName parse and validate">
                        // BEGIN displayName validation
                        // Check if the JSON object has a "displayName" property
                        JsonElement displayNameElement = jsonObject.get("displayName");

                        String currentDisplayName = null;
                        if (displayNameElement != null && displayNameElement.isJsonPrimitive() && displayNameElement.getAsString() != null) {
                            currentDisplayName = displayNameElement.getAsString();
                            // Validate the displayName here
                            if (!isValidDisplayName(currentDisplayName)) {
                                LOGGER.warn("Line {}: Invalid displayName format for '{}': {}. Using as-is.", lineNumber, currentEntityID, currentDisplayName);
                            }
                        } else {
                            LOGGER.warn("Line {}: 'displayName' property missing or invalid for '{}'. Using as-is.", lineNumber, currentEntityID);
                        }
                        // END displayName validation
                        //</editor-fold>

                        //<editor-fold desc="spawnEggColors parse and validate">
                        // BEGIN spawnEggColors validation
                        String primaryColor = FALLBACK_PRIMARY_COLOR;
                        String secondaryColor = FALLBACK_SECONDARY_COLOR;

                        // Check if the JSON object has a "spawnEggColors" property
                        JsonElement spawnEggColorsElement = jsonObject.get("spawnEggColors");

                        if (spawnEggColorsElement != null && spawnEggColorsElement.isJsonObject()) {
                            JsonObject spawnEggColorsJson = spawnEggColorsElement.getAsJsonObject();
                            JsonElement primaryElement = spawnEggColorsJson.get("primary");
                            JsonElement secondaryElement = spawnEggColorsJson.get("secondary");

                            if (primaryElement != null && secondaryElement != null &&
                                    primaryElement.isJsonPrimitive() && secondaryElement.isJsonPrimitive()) {
                                String primaryColorCandidate = primaryElement.getAsString();
                                String secondaryColorCandidate = secondaryElement.getAsString();

                                // Validate primary and secondary colors
                                if (isValidColor(primaryColorCandidate) && isValidColor(secondaryColorCandidate)) {
                                    primaryColor = primaryColorCandidate;
                                    secondaryColor = secondaryColorCandidate;
                                } else {
                                    LOGGER.warn("Line {}: Invalid spawnEggColors format for '{}'. Using fallback colors.", lineNumber, currentEntityID);
                                }
                            }
                        } else {
                            LOGGER.warn("Line {}: 'spawnEggColors' property missing or invalid for '{}'. Using fallback colors.", lineNumber, currentEntityID);
                        }
                        // END spawnEggColors validation
                        //</editor-fold>

                        //<editor-fold desc="transformItems parse and validate">
                        // BEGIN transformItems validation
                        // Check if the JSON object has a "transformItems" property
                        // TODO: Still need to add checks unique to each property and provide fallbacks!!
                        JsonElement transformItemsElement = jsonObject.get("transformItems");

                        if (transformItemsElement != null && transformItemsElement.isJsonArray()) {
                            List<SammieJamSlimeData.TransformItem> transformItemsDataList = getTransformItems(transformItemsElement);

                            // Create a new SammieJamSlimeData object and set its properties
                            SammieJamSlimeData slimeData = new SammieJamSlimeData();
                            slimeData.setEntityID(currentEntityID);
                            if (currentDisplayName != null) {
                                slimeData.setDisplayName(currentDisplayName);
                            }
                            slimeData.setSpawnEggColors(new SammieJamSlimeData.SpawnEggColors(primaryColor, secondaryColor));
                            slimeData.setTransformItems(transformItemsDataList);

                            // Add the slimeData object to your array
                            JsonObject slimeDataJson = gson.toJsonTree(slimeData).getAsJsonObject();
                            jsonArray.add(slimeDataJson);

                        }
                        // END transformItems validation
                        //</editor-fold>
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

    private static List<SammieJamSlimeData.TransformItem> getTransformItems(JsonElement transformItemsElement) {
        JsonArray transformItemsArray = transformItemsElement.getAsJsonArray();

        // Create a list to hold the validated transform item data
        List<SammieJamSlimeData.TransformItem> transformItemsDataList = new ArrayList<>();

        // Iterate through the objects in the array
        for (JsonElement itemElement : transformItemsArray) {
            if (itemElement.isJsonObject()) {
                JsonObject itemObject = itemElement.getAsJsonObject();

                // Parse and validate the "itemID" property (expects string)
                JsonElement itemIDElement = itemObject.get("itemID");
                if (itemIDElement != null && itemIDElement.isJsonPrimitive() && itemIDElement.getAsString() != null) {
                    String itemID = itemIDElement.getAsString();

                    // Parse and validate the "consumeItem" property (expects boolean)
                    JsonElement consumeItemElement = itemObject.get("consumeItem");
                    if (consumeItemElement != null && consumeItemElement.isJsonPrimitive() && consumeItemElement.getAsJsonPrimitive().isBoolean()) {
                        boolean consumeItem = consumeItemElement.getAsBoolean();

                        // Parse and validate the "reduceDurability" property (expects boolean)
                        JsonElement reduceDurabilityElement = itemObject.get("reduceDurability");
                        if (reduceDurabilityElement != null && reduceDurabilityElement.isJsonPrimitive() && reduceDurabilityElement.getAsJsonPrimitive().isBoolean()) {
                            boolean reduceDurability = reduceDurabilityElement.getAsBoolean();

                            // Create an instance of TransformItem and add it to the list
                            SammieJamSlimeData.TransformItem transformItem = new SammieJamSlimeData.TransformItem(itemID, consumeItem, reduceDurability);
                            transformItemsDataList.add(transformItem);
                        }
                    }
                }
            }
        }
        return transformItemsDataList;
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

    // Validate spawnEggColor data
    private static boolean isValidColor(String color) {
        if (color != null) {
            // Remove "0x" or "#" prefixes if present
            color = color.replace("0x", "").replace("#", "");

            // If the length is now 6, assume full opacity (FF)
            if (color.length() == 6) {
                color = "FF" + color;
            }

            // Check if the modified color matches the desired format
            return color.matches("^([A-Fa-f0-9]{8})$");
        }
        return false;
    }

}