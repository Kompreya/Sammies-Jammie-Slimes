package kombee.sammiejamslimes;

import com.google.gson.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class JSONFileLoader {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private static final String FALLBACK_PRIMARY_COLOR = "FF0000";
    private static final String FALLBACK_SECONDARY_COLOR = "00FF00";
    private static final Boolean FALLBACK_CONSUME_ITEM = true;
    private static final Boolean FALLBACK_REDUCE_DURABILITY = false;

    // SoC: Parsing and validating slimes.json before storing in SammieJamSlimeData
    // Here we perform a grand manner of validation checks on slimes.json.
    // Hard reject or expect critical value types and formatting, throw error indicating expected correct formatting or type.
    // Soft accept some values, sanitize as needed, validate and store.
    // Prevent crashes when possible. Provide fallbacks. Throw warnings indicating fallback used.
    public static SammieJamSlimeData[] loadSlimeData(String filePath, boolean loadDefaultOnFail) {
        // Declare a set to track encountered entityIDs
        Set<String> encounteredEntityIDs = new HashSet<>();

        // Declare a list to collect SammieJamSlimeData objects
        List<SammieJamSlimeData> slimeDataList = new ArrayList<>();

        // Initialize other variables you may need here
        SammieJamSlimeData slimeData = null;

        JsonArray jsonArray = new JsonArray(); // Initialize jsonArray


        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

                        // Handle entityID property
                        String currentEntityID = handleEntityID(jsonObject, lineNumber);

                        if (currentEntityID == null) {
                            // Invalid or missing entityID, skip this object
                            continue;
                        }

                        // Handle displayName property
                        String currentDisplayName = handleDisplayName(jsonObject, lineNumber, currentEntityID);

                        // Handle spawnEggColors property
                        handleSpawnEggColors(jsonObject, lineNumber, currentEntityID, slimeData);

                        // Handle transformItems property
                        handleTransformItems(jsonObject, lineNumber, currentEntityID, jsonArray);

                        // Create a new SammieJamSlimeData instance if needed
                        if (slimeData != null) {
                            slimeDataList.add(slimeData);
                        }
                    } else {
                        LOGGER.warn("Line {}: Ignored invalid JSON data: {}", lineNumber, line);
                    }
                } catch (JsonParseException e) {
                    LOGGER.warn("Line {}: Ignored invalid JSON data: {}", lineNumber, line);
                }
            }

            // Create an array from the list of SammieJamSlimeData
            SammieJamSlimeData[] slimeDataArray = slimeDataList.toArray(new SammieJamSlimeData[0]);
            return slimeDataArray;
        } catch (IOException e) {
            LOGGER.error("Error reading file: {}", filePath);
            e.printStackTrace(); // Handle the exception appropriately
        }

        // Return null if there was an error
        return null;
    }



    // HANDLERS //

    //<editor-fold desc="handleEntityID">
    private static String handleEntityID(JsonObject jsonObject, int lineNumber) {
        JsonElement entityIDElement = jsonObject.get("entityID");

        if (entityIDElement != null && entityIDElement.isJsonPrimitive() && entityIDElement.getAsString() != null) {
            String currentEntityID = entityIDElement.getAsString();

            // Validate the entityID format
            if (!isValidEntityID(currentEntityID)) {
                LOGGER.error("Line {}: Invalid entityID format for '{}': {}. Skipping.", lineNumber, currentEntityID, jsonObject.toString());
                return null; // Return null for invalid entityID
            }

            return currentEntityID;
        } else {
            LOGGER.warn("Line {}: JSON object missing or has an invalid 'entityID' property. Skipping.", lineNumber);
            return null; // Return null for missing or invalid entityID
        }
    }
    //</editor-fold>

    //<editor-fold desc="handleDisplayName">
    private static String handleDisplayName(JsonObject jsonObject, int lineNumber, String currentEntityID) {
        // Check if the JSON object has a "displayName" property
        JsonElement displayNameElement = jsonObject.get("displayName");

        String currentDisplayName = null;
        if (displayNameElement != null && displayNameElement.isJsonPrimitive()) {
            currentDisplayName = displayNameElement.getAsString();
            // Validate the displayName here
            if (!isValidDisplayName(currentDisplayName)) {
                LOGGER.warn("Line {}: Invalid displayName format for '{}': {}. Using as-is.", lineNumber, currentEntityID, currentDisplayName);
            }
        } else {
            LOGGER.warn("Line {}: 'displayName' property missing or invalid for '{}'. Using as-is.", lineNumber, currentEntityID);
        }

        return currentDisplayName;
    }
    //</editor-fold>

    //<editor-fold desc="handleSpawnEggColors">
    private static void handleSpawnEggColors(JsonObject jsonObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeData) {
        String primaryColor = FALLBACK_PRIMARY_COLOR;
        String secondaryColor = FALLBACK_SECONDARY_COLOR;

        // Check if the JSON object has a "spawnEggColors" property
        JsonElement spawnEggColorsElement = jsonObject.get("spawnEggColors");

        if (spawnEggColorsElement != null && spawnEggColorsElement.isJsonObject()) {
            JsonObject spawnEggColorsJson = spawnEggColorsElement.getAsJsonObject();
            JsonElement primaryElement = spawnEggColorsJson.get("primary");
            JsonElement secondaryElement = spawnEggColorsJson.get("secondary");

            // Validate primary color
            if (primaryElement != null && primaryElement.isJsonPrimitive()) {
                String primaryColorCandidate = primaryElement.getAsString();
                if (isValidColor(primaryColorCandidate)) {
                    primaryColor = primaryColorCandidate;
                } else {
                    LOGGER.warn("Line {}: Invalid primary color format for '{}' in '{}'. Using fallback primary color.", lineNumber, currentEntityID, primaryColorCandidate);
                }
            }

            // Validate secondary color
            if (secondaryElement != null && secondaryElement.isJsonPrimitive()) {
                String secondaryColorCandidate = secondaryElement.getAsString();
                if (isValidColor(secondaryColorCandidate)) {
                    secondaryColor = secondaryColorCandidate;
                } else {
                    LOGGER.warn("Line {}: Invalid secondary color format for '{}' in '{}'. Using fallback secondary color.", lineNumber, currentEntityID, secondaryColorCandidate);
                }
            }
        } else {
            LOGGER.warn("Line {}: 'spawnEggColors' property missing or invalid for '{}'. Using fallback colors.", lineNumber, currentEntityID);
        }

        // Set the spawnEggColors property in your SammieJamSlimeData instance
        slimeData.setSpawnEggColors(new SammieJamSlimeData.SpawnEggColors(primaryColor, secondaryColor));
    }
    //</editor-fold>

    //<editor-fold desc="handleTransformItems">
    private static void handleTransformItems(JsonObject jsonObject, int lineNumber, String currentEntityID, JsonArray jsonArray) {
        // Check if the JSON object has a "transformItems" property
        JsonElement transformItemsElement = jsonObject.get("transformItems");

        if (transformItemsElement != null && transformItemsElement.isJsonArray()) {
            List<SammieJamSlimeData.TransformItem> transformItemsDataList = getTransformItems(transformItemsElement);

            // Create an instance of SammieJamSlimeData and set its properties
            SammieJamSlimeData slimeData = new SammieJamSlimeData();
            slimeData.setEntityID(currentEntityID);
            slimeData.setTransformItems(transformItemsDataList);

            // Add the slimeData object to your array
            JsonObject slimeDataJson = gson.toJsonTree(slimeData).getAsJsonObject();
            jsonArray.add(slimeDataJson);
        }
    }
    //</editor-fold>

    //<editor-fold desc="getTransformItems">
    private static List<SammieJamSlimeData.TransformItem> getTransformItems(JsonElement transformItemsElement) {
        JsonArray transformItemsArray = transformItemsElement.getAsJsonArray();

        // Create a list to hold the validated transform item data
        List<SammieJamSlimeData.TransformItem> transformItemsDataList = new ArrayList<>();

        // Iterate through the objects in the array
        for (JsonElement itemElement : transformItemsArray) {
            if (itemElement.isJsonObject()) {
                JsonObject itemObject = itemElement.getAsJsonObject();

                // Extract the "itemID" property and validate it
                JsonElement itemIDElement = itemObject.get("itemID");
                if (itemIDElement != null && itemIDElement.isJsonPrimitive() && itemIDElement.getAsString() != null) {
                    String itemID = itemIDElement.getAsString();

                    // Continue processing only if itemID is valid
                    if (isValidItemID(itemID)) {
                        // Extract and validate the "consumeItem" property (optional)
                        boolean consumeItem = getBooleanOrDefault(itemObject, "consumeItem", FALLBACK_CONSUME_ITEM);

                        // Extract and validate the "reduceDurability" property (optional)
                        boolean reduceDurability = getBooleanOrDefault(itemObject, "reduceDurability", FALLBACK_REDUCE_DURABILITY);

                        // Create an instance of TransformItem and add it to the list
                        SammieJamSlimeData.TransformItem transformItem = new SammieJamSlimeData.TransformItem(itemID, consumeItem, reduceDurability);
                        transformItemsDataList.add(transformItem);
                    } else {
                        LOGGER.warn("Invalid 'itemID' format for '{}'. Skipping the entire object.", itemID);
                    }
                } else {
                    LOGGER.warn("Missing or invalid 'itemID' property in an object. Skipping the entire object.");
                }
            } else {
                LOGGER.warn("Invalid object format in the 'transformItems' array. Skipping the entire object.");
            }
        }
        return transformItemsDataList;
    }
    //</editor-fold>

    //<editor-fold desc="getBooleanOrDefault">
    private static boolean getBooleanOrDefault(JsonObject jsonObject, String propertyName, boolean fallback) {
        JsonElement propertyElement = jsonObject.get(propertyName);
        if (propertyElement != null && propertyElement.isJsonPrimitive() && propertyElement.getAsJsonPrimitive().isBoolean()) {
            return propertyElement.getAsBoolean();
        } else {
            LOGGER.warn("Invalid '{}' property format or missing. Using the fallback value.", propertyName);
            return fallback;
        }
    }
    //</editor-fold>

    ///////////////

    // VALIDATORS //

    //<editor-fold desc="isValidEntityID">
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
    //</editor-fold>

    //<editor-fold desc="isValidDisplayName">
    // TODO: Check for caveats with localized name formatting. Generally, any formatting is acceptable, but let's warn users for caveats.
    // For now, we're just sanitizing values containing only whitespace, which then reverts back to the unlocalized name. Unlocalized names will still get added to en_us.lang in the resources folder to each entity.
    private static boolean isValidDisplayName(String displayName) {
        // Check if the displayName is not null
        if (displayName == null) {
            return false;
        }

        // Sanitize spaces by removing them if the value consists only of whitespace
        if (displayName.trim().isEmpty()) {
            return false; // Treat it as missing
        }

        // If the displayName has passed all checks, consider it valid
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="isValidColor">
    private static boolean isValidColor(String color) {
        if (color != null) {
            // Remove whitespace characters
            color = color.replaceAll("\\s", "");

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
    //</editor-fold>

    //<editor-fold desc="isValidItemID">
    private static boolean isValidItemID(String itemID) {
        if (itemID != null && !itemID.isEmpty()) {
            if (isItemRegistered(itemID)) {
                return true; // The item ID is valid and registered
            } else {
                LOGGER.warn("Invalid itemID: '{}'. Item not found. Skipping.", itemID);
                return false; // The item ID is invalid or not registered
            }
        } else {
            LOGGER.warn("ItemID is missing or empty. Skipping.");
            return false; // The item ID is missing or empty
        }
    }

    //</editor-fold>

    //<editor-fold desc="isItemRegistered">
    private static boolean isItemRegistered(String itemID) {
        ResourceLocation itemResourceLocation = new ResourceLocation(itemID);
        Item item = ForgeRegistries.ITEMS.getValue(itemResourceLocation);
        return item != null;
    }
    //</editor-fold>

    ////////////////

    // Here we grab all that nicely validated and parsed data and wrap it into a nice package for delivery to SammieJamSlimeData
    private static SammieJamSlimeData createSammieJamSlimeData(String currentEntityID, String currentDisplayName, String primaryColor, String secondaryColor, List<SammieJamSlimeData.TransformItem> transformItemsDataList) {
        SammieJamSlimeData slimeData = new SammieJamSlimeData();
        slimeData.setEntityID(currentEntityID);
        if (currentDisplayName != null) {
            slimeData.setDisplayName(currentDisplayName);
        }
        slimeData.setSpawnEggColors(new SammieJamSlimeData.SpawnEggColors(primaryColor, secondaryColor));
        slimeData.setTransformItems(transformItemsDataList);
        return slimeData;
    }


}