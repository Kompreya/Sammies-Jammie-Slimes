package kombee.sammiejamslimes.data;

import com.google.gson.*;

import kombee.sammiejamslimes.SammieJamSlimes;
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
    private static final ResourceLocation DEFAULT_SLIME_TEXTURE = new ResourceLocation("minecraft", "textures/entity/slime/slime.png");

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

                        // Handle appearance property
                        handleAppearance(jsonObject, lineNumber, currentEntityID, jsonArray);

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
        // Define and initialize the metadata variable if it's applicable
        int metadata = 0; // Initialize with a default value or whatever is appropriate

        // Check if the JSON object has a "transformItems" property
        JsonElement transformItemsElement = jsonObject.get("transformItems");

        if (transformItemsElement != null && transformItemsElement.isJsonArray()) {
            // Use the metadata variable when calling getTransformItems
            List<SammieJamSlimeData.TransformItem> transformItemsDataList = getTransformItems(transformItemsElement, metadata);

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
    private static List<SammieJamSlimeData.TransformItem> getTransformItems(JsonElement transformItemsElement, int metadata) {
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
                        SammieJamSlimeData.TransformItem transformItem = new SammieJamSlimeData.TransformItem(itemID, metadata, consumeItem, reduceDurability);
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

    //<editor-fold desc="handleAppearance">
    private static void handleAppearance(JsonObject jsonObject, int lineNumber, String currentEntityID, JsonArray jsonArray) {
        // Check if the JSON object has an "appearance" property
        JsonElement appearanceElement = jsonObject.get("appearance");

        if (appearanceElement == null || !appearanceElement.isJsonObject()) {
            // Use the fallback appearance
            setFallbackAppearance(jsonObject, DEFAULT_SLIME_TEXTURE);
        } else {
            // The "appearance" property exists and is an object
            JsonObject appearanceObject = appearanceElement.getAsJsonObject();

            // Check and validate the "type" property
            JsonElement typeElement = appearanceObject.get("type");
            if (typeElement == null || !typeElement.isJsonPrimitive() || !typeElement.getAsJsonPrimitive().isString()) {
                // Use the fallback appearance
                setFallbackAppearance(jsonObject, DEFAULT_SLIME_TEXTURE);
            } else {
                String type = typeElement.getAsString();
                if (type.equals("texture")) {
                    handleTextureAppearance(appearanceObject, lineNumber, currentEntityID, jsonObject);
                } else if (type.equals("color")) {
                    handleColorAppearance(appearanceObject, lineNumber, currentEntityID, jsonObject);
                } else {
                    // Unknown "type" value, use the fallback appearance
                    setFallbackAppearance(jsonObject, DEFAULT_SLIME_TEXTURE);
                }
            }
        }
    }


    //</editor-fold>

    //<editor-fold desc="handleTextureAppearance">
    private static void handleTextureAppearance(JsonObject appearanceObject, int lineNumber, String currentEntityID, JsonObject jsonObject) {
        // Handle "texture" type
        JsonElement sourceElement = appearanceObject.get("source");
        if (sourceElement != null && sourceElement.isJsonPrimitive() && sourceElement.getAsString() != null) {
            String source = sourceElement.getAsString();
            // Validate and process the "source" value for "texture" type
            if (!isValidTextureSource(source, lineNumber, currentEntityID, jsonObject)) {
                // Invalid source, fallback has already been set
                return;
            }

            // Process the valid source here
            // ...
        } else {
            LOGGER.warn("Line {}: 'source' property missing or invalid for 'texture' appearance. Skipping.", lineNumber);
        }
    }

    //</editor-fold>

    //<editor-fold desc="handleColorAppearance">
    private static boolean handleColorAppearance(JsonObject appearanceObject, int lineNumber, String currentEntityID, JsonObject jsonObject) {
        // Handle "color" type
        JsonElement sourceElement = appearanceObject.get("source");
        if (sourceElement != null && sourceElement.isJsonArray()) {
            JsonArray colorArray = sourceElement.getAsJsonArray();
            // Validate and process the "source" value for "color" type
            return isValidColorSource(colorArray, lineNumber, currentEntityID, jsonObject);
        } else {
            LOGGER.warn("Line {}: 'source' property missing or invalid for 'color' appearance. Using the fallback texture.", lineNumber);
            // Use the fallback appearance for "color" type
            setFallbackAppearance(jsonObject, DEFAULT_SLIME_TEXTURE);
            return false;
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
        if (itemID == null || itemID.isEmpty()) {
            LOGGER.warn("Item ID is missing or empty. Skipping.");
            return false;
        }

        // Split the itemID using a colon to check for metadata
        String[] itemData = itemID.split(":");
        if (itemData.length < 2 || itemData.length > 3) {
            LOGGER.warn("Invalid item ID format for '{}'. Skipping.", itemID);
            return false;
        }

        // Check if the item namespace and path are valid
        ResourceLocation itemResourceLocation = new ResourceLocation(itemData[0], itemData[1]);
        if (!ForgeRegistries.ITEMS.containsKey(itemResourceLocation)) {
            LOGGER.warn("Invalid item ID format for '{}'. Namespace or path is not valid. Skipping.", itemID);
            return false;
        }

        // Check if metadata is specified and if it's an integer
        if (itemData.length == 3) {
            String metaString = itemData[2];
            if (!metaString.equals("*")) {
                try {
                    int meta = Integer.parseInt(metaString);
                    if (meta < 0) {
                        LOGGER.warn("Invalid metadata '{}' for item ID '{}'. Metadata must be a non-negative integer. Skipping.", meta, itemID);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warn("Invalid metadata format for item ID '{}'. Metadata must be a valid integer. Skipping.", itemID);
                    return false;
                }
            }
        }

        return true; // Valid item ID format
    }

    //</editor-fold>

    //<editor-fold desc="isValidTextureSource">
    private static boolean isValidTextureSource(String source, int lineNumber, String currentEntityID, JsonObject jsonObject) {
        if (!source.endsWith(".png") && !source.endsWith(".json")) {
            // The provided source does not have a '.png' or '.json' extension
            LOGGER.warn("Line {}: Invalid 'source' property format for 'texture' appearance for '{}'. Using the fallback texture.", lineNumber, currentEntityID);
            // Use the fallback appearance for "texture" type
            setFallbackAppearance(jsonObject, DEFAULT_SLIME_TEXTURE);
            return false;
        }
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="isValidColorSource">
    private static boolean isValidColorSource(JsonArray colorArray, int lineNumber, String currentEntityID, JsonObject jsonObject) {
        int numValues = colorArray.size();

        if (numValues < 1 || numValues > 4) {
            // Invalid number of values in the array
            LOGGER.warn("Line {}: Invalid 'source' property format for 'color' appearance. Using the fallback texture.", lineNumber);
            // Use the fallback appearance for "color" type
            setFallbackAppearance(jsonObject, DEFAULT_SLIME_TEXTURE);
            return false;
        }

        int r, g, b, a;

        if (numValues == 1) {
            // If one value is provided, assume it for R, G, B, and fully opaque alpha
            int value = Math.min(Math.max(colorArray.get(0).getAsInt(), 0), 255);
            r = g = b = value;
            a = 255;
            LOGGER.warn("Line {}: Assuming provided value {} for 'color' appearance for '{}'.", lineNumber, value, currentEntityID);
        } else if (numValues == 2) {
            // If two values are provided, assume the first for R, G, B, and the second for alpha
            int value1 = Math.min(Math.max(colorArray.get(0).getAsInt(), 0), 255);
            int value2 = Math.min(Math.max(colorArray.get(1).getAsInt(), 0), 255);
            r = g = b = value1;
            a = value2;
            LOGGER.warn("Line {}: Assuming provided values {} for 'color' appearance for '{}'.", lineNumber, value1 + ", " + value2, currentEntityID);

            // If both values are between 0-255, assume the third value is 0
            if (r >= 0 && r <= 255 && a >= 0 && a <= 255) {
                b = 0;
                LOGGER.warn("Line {}: Assuming B value 0 for 'color' appearance for '{}'.", lineNumber, currentEntityID);
            }
        } else if (numValues == 3) {
            // If three values are provided, assume them for R, G, B, and fully opaque alpha
            r = Math.min(Math.max(colorArray.get(0).getAsInt(), 0), 255);
            g = Math.min(Math.max(colorArray.get(1).getAsInt(), 0), 255);
            b = Math.min(Math.max(colorArray.get(2).getAsInt(), 0), 255);
            a = 255;
        } else {
            // If four values are provided, assume them for R, G, B, and alpha
            r = Math.min(Math.max(colorArray.get(0).getAsInt(), 0), 255);
            g = Math.min(Math.max(colorArray.get(1).getAsInt(), 0), 255);
            b = Math.min(Math.max(colorArray.get(2).getAsInt(), 0), 255);
            a = Math.min(Math.max(colorArray.get(3).getAsInt(), 0), 255);
        }

        // Normalize the values
        float normalizedR = r / 255.0f;
        float normalizedG = g / 255.0f;
        float normalizedB = b / 255.0f;
        float normalizedA = a / 255.0f;

        // Now you can use these normalized values as needed

        return true; // Source is valid
    }



    //</editor-fold>

    ////////////////

    private static void setFallbackAppearance(JsonObject jsonObject, ResourceLocation fallbackTexture) {
        JsonObject fallbackAppearance = new JsonObject();
        fallbackAppearance.addProperty("type", "texture");
        fallbackAppearance.addProperty("source", fallbackTexture.toString());
        jsonObject.add("appearance", fallbackAppearance);
    }


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