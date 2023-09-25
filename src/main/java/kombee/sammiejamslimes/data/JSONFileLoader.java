package kombee.sammiejamslimes.data;

import com.google.gson.*;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.utils.ItemMetaHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public class JSONFileLoader {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private static List<SammieJamSlimeData> slimeDataList = new ArrayList<>();
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

        LOGGER.debug("Starting JSON parsing loop.");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonContentBuilder = new StringBuilder();
            String line;

            // Track line number for use in logging.
            while ((line = reader.readLine()) != null) {
                // Skip empty lines or lines with only whitespace
                if (line.trim().isEmpty()) {
                    continue;
                }

                jsonContentBuilder.append(line); // Append the line to build the JSON content
            }

            // Attempt to parse the entire content as a JSON array
            String jsonContent = jsonContentBuilder.toString();
            JsonArray jsonArray = jsonParser.parse(jsonContent).getAsJsonArray();
            SammieJamSlimeData slimeDataA;

            int i = 0; // Initialize index
            int iterationsWithoutProgress = 0; // Track iterations without progress

            // Collect Valid EntityIDs in the order of their occurrence
            List<String> entityIDList = new ArrayList<>();

            //<editor-fold desc="First Pass">
            // First Pass - Collect Valid EntityIDs
            int lineNumber = 0;
            while (i < jsonArray.size() && iterationsWithoutProgress < jsonArray.size()) {
                lineNumber++;
                JsonElement jsonElement = jsonArray.get(i);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    // Handle entityID property
                    String currentEntityID = handleEntityID(jsonObject, lineNumber);

                    if (currentEntityID != null) {
                        // Valid entityID, create a new slimeData instance
                        slimeDataA = new SammieJamSlimeData();
                        slimeDataA.setEntityID(currentEntityID);

                        // Add the valid entityID to the encounteredEntityIDs set
                        encounteredEntityIDs.add(currentEntityID);

                        // Add the entityID to the list in the order of occurrence
                        entityIDList.add(currentEntityID);

                        // Add the slimeDataA object to the list
                        slimeDataList.add(slimeDataA);
                    } else {
                        // Invalid or missing entityID, skip this object
                        i++; // Increment the index and continue to the next element
                        iterationsWithoutProgress++;
                        continue;
                    }

                    i++;
                    iterationsWithoutProgress = 0; // Reset the counter since progress was made
                } else {
                    LOGGER.warn("Line {}: Ignored invalid JSON data: {}", lineNumber, jsonElement);
                    i++; // Increment the index even for invalid JSON data
                    iterationsWithoutProgress++;
                }
            }
            //</editor-fold>

            //<editor-fold desc="Second Pass">
            // Second Pass - Process Remaining Properties
            lineNumber = 0;
            for (String currentEntityID : entityIDList) {
                lineNumber++;

                // Find the corresponding JSON object in the original JSON array
                JsonObject jsonObject = findJsonObjectForEntityID(jsonArray, currentEntityID, lineNumber);

                // Retrieve the existing slimeData object from the list
                SammieJamSlimeData slimeDataB = findSammieJamSlimeDataInList(currentEntityID, slimeDataList);

                // Set the entityID to the retrieved slimeData instance
                slimeDataB.setEntityID(currentEntityID);

                // Handle displayName property
                handleDisplayName(jsonObject, lineNumber, currentEntityID, slimeDataB);

                // Handle spawnEggColors property
                handleSpawnEggColors(jsonObject, lineNumber, currentEntityID, slimeDataB);

                // Handle transformItems property
                handleTransformItems(jsonObject, lineNumber, currentEntityID, slimeDataB);

                // Handle appearance property
                handleAppearance(jsonObject, lineNumber, currentEntityID, slimeDataB);

                // Handle transformTo property
                handleTransformTo(jsonObject, lineNumber, currentEntityID, slimeDataB, encounteredEntityIDs);
                LOGGER.debug("Finished processing 'transformTo' property.");

                // Handle spawningEnable property
                boolean spawningEnable = handleSpawningEnable(jsonObject, lineNumber, currentEntityID);
                LOGGER.debug("Finished processing JSON object for entity ID: {}", currentEntityID);
            }
            //</editor-fold>

            // Create an array from the list of SammieJamSlimeData
            SammieJamSlimeData[] slimeDataArray = slimeDataList.toArray(new SammieJamSlimeData[0]);
            return slimeDataArray;
        } catch (IOException e) {
            LOGGER.error("Error reading file: {}", filePath);
            e.printStackTrace();
        } catch (JsonParseException e) {
            LOGGER.error("Error parsing JSON content: {}", e.getMessage());
            e.printStackTrace();
        }

        LOGGER.debug("Finished JSON parsing loop.");
        // Return null if there was an error
        return null;
    }

    //<editor-fold desc="Second Pass Utils">
    private static SammieJamSlimeData findSammieJamSlimeDataInList(String entityID, List<SammieJamSlimeData> slimeDataList) {
        for (SammieJamSlimeData slimeDataB : slimeDataList) {
            if (slimeDataB.getEntityID().equals(entityID)) {
                return slimeDataB;
            }
        }
        // If the entityID is not found, handle it here, e.g., by creating a new instance.
        return new SammieJamSlimeData();
    }



    private static JsonObject findJsonObjectForEntityID(JsonArray jsonArray, String entityID, int lineNumber) {
        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (entityID.equals(jsonObject.get("entityID").getAsString())) {
                    return jsonObject; // Found the matching JSON object
                }
            }
        }
        return null; // Entity ID not found in the JSON array
    }
    //</editor-fold>



    // HANDLERS //

    //<editor-fold desc="handleEntityID">
    private static String handleEntityID(JsonObject jsonObject, int lineNumber) {
        JsonElement entityIDElement = jsonObject.get("entityID");

        if (entityIDElement != null && entityIDElement.isJsonPrimitive()) {
            String currentEntityID = entityIDElement.getAsString();

            if (isValidEntityID(currentEntityID)) {
                return currentEntityID;
            } else {
                LOGGER.error("Line {}: Invalid entityID format for '{}': {}. Skipping.", lineNumber, currentEntityID, jsonObject.toString());
            }
        } else {
            LOGGER.warn("Line {}: JSON object missing or has an invalid 'entityID' property. Skipping.", lineNumber);
        }

        return null;
    }
    //</editor-fold>

    //<editor-fold desc="handleDisplayName">
    private static void handleDisplayName(JsonObject jsonObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        // Check if the JSON object has a "displayName" property
        JsonElement displayNameElement = jsonObject.get("displayName");

        if (displayNameElement != null && displayNameElement.isJsonPrimitive()) {
            String currentDisplayName = displayNameElement.getAsString();
            // Validate the displayName here
            if (isValidDisplayName(currentDisplayName)) {
                slimeDataB.setDisplayName(currentDisplayName); // Set the displayName in the slimeDataB variable
            } else {
                LOGGER.warn("Line {}: Invalid displayName format for '{}': '{}'. Using the default value from the data class.", lineNumber, currentEntityID, currentDisplayName);
            }
        } else {
            LOGGER.info("Line {}: 'displayName' property missing or has an invalid type for '{}'. Using the default value from the data class.", lineNumber, currentEntityID);
        }
    }
    //</editor-fold>

    //<editor-fold desc="handleSpawnEggColors">
    private static void handleSpawnEggColors(JsonObject jsonObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        // Check if the JSON object has a "spawnEggColors" property
        JsonElement spawnEggColorsElement = jsonObject.get("spawnEggColors");

        if (spawnEggColorsElement != null && spawnEggColorsElement.isJsonObject()) {
            // The "spawnEggColors" property exists and is an object
            JsonObject spawnEggColorsJson = spawnEggColorsElement.getAsJsonObject();

            // Primary color
            String primaryColor = spawnEggColorsJson.has("primary") ? spawnEggColorsJson.get("primary").getAsString() : null;

            // Secondary color
            String secondaryColor = spawnEggColorsJson.has("secondary") ? spawnEggColorsJson.get("secondary").getAsString() : null;

            // Validate and process the primary color
            if (primaryColor != null && isValidColor(primaryColor)) {
                slimeDataB.getSpawnEggColors().setPrimary(primaryColor);
            } else {
                LOGGER.warn("Line {}: Invalid primary color format for '{}' in '{}'. Using the default value from the data class.", lineNumber, currentEntityID, primaryColor);
            }

            // Validate and process the secondary color
            if (secondaryColor != null && isValidColor(secondaryColor)) {
                slimeDataB.getSpawnEggColors().setSecondary(secondaryColor);
            } else {
                LOGGER.warn("Line {}: Invalid secondary color format for '{}' in '{}'. Using the default value from the data class.", lineNumber, currentEntityID, secondaryColor);
            }
        } else {
            LOGGER.warn("Line {}: 'spawnEggColors' property missing or has an invalid type for '{}'. Using the default values from the data class.", lineNumber, currentEntityID);
        }
    }


    //</editor-fold>

    //<editor-fold desc="handleTransformItems">
    private static void handleTransformItems(JsonObject jsonObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        // Define and initialize the metadata variable if it's applicable
        int metadata = 0; // Initialize with a default value

        // Check if the JSON object has a "transformItems" property
        JsonElement transformItemsElement = jsonObject.get("transformItems");

        if (transformItemsElement != null) {
            // Check if the "transformItems" property exists

            if (transformItemsElement.isJsonArray()) {
                // The "transformItems" property exists and is an array
                JsonArray transformItemsArray = transformItemsElement.getAsJsonArray();

                if (!transformItemsArray.isJsonNull() && transformItemsArray.size() > 0) {
                    // The JSON array is not empty, proceed with processing
                    List<SammieJamSlimeData.TransformItem> transformItemsDataList = getTransformItems(transformItemsElement, metadata);

                    // Set the properties of the existing SammieJamSlimeData instance (slimeDataB)
                    slimeDataB.setTransformItems(transformItemsDataList);
                } else {
                    // The JSON array is empty, handle this case if needed
                }
            } else {
                // The "transformItems" property exists but is not an array
                // Set it to an empty array
                transformItemsElement.getAsJsonObject().add("transformItems", new JsonArray());
            }
        } else {
            // The "transformItems" property does not exist, create it with an empty array
            jsonObject.add("transformItems", new JsonArray());
        }
    }
    //</editor-fold>

    //<editor-fold desc="getTransformItems">
    private static List<SammieJamSlimeData.TransformItem> getTransformItems(JsonElement transformItemsElement, int metadata) {
        JsonArray transformItemsArray = transformItemsElement.getAsJsonArray();
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
                        // Extract and validate the "consumeItem" property
                        boolean consumeItem = getBooleanOrDefault(itemObject, "consumeItem", FALLBACK_CONSUME_ITEM);

                        // Extract and validate the "reduceDurability" property
                        boolean reduceDurability = getBooleanOrDefault(itemObject, "reduceDurability", FALLBACK_REDUCE_DURABILITY);

                        // Extract and validate the "nbt" property
                        JsonElement nbtElement = itemObject.get("nbt");
                        NBTTagCompound nbtCompound = null; // Initialize as null
                        if (nbtElement != null && nbtElement.isJsonObject()) {
                            // The "nbt" property exists and is an object, proceed to parse and validate it
                            JsonObject nbtObject = nbtElement.getAsJsonObject();
                            String nbtString = nbtObject.toString();

                            try {
                                NBTTagCompound parsedNBT = JsonToNBT.getTagFromJson(nbtString);
                                // Store the parsed NBT data
                                nbtCompound = parsedNBT;
                            } catch (NBTException e) {
                                LOGGER.warn("Invalid 'nbt' property format for '{}'. Skipping the NBT data.", itemID);
                            }
                        }

                        // Create an instance of TransformItem and add it to the list
                        SammieJamSlimeData.TransformItem transformItem = new SammieJamSlimeData.TransformItem(itemID, metadata, consumeItem, reduceDurability, nbtCompound);
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
    private static void handleAppearance(JsonObject jsonObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        // Check if the JSON object has an "appearance" property
        JsonElement appearanceElement = jsonObject.get("appearance");

        // Create the fallback appearance object
        JsonObject fallbackAppearance = createFallbackAppearance();

        if (appearanceElement != null && appearanceElement.isJsonObject()) {
            JsonObject appearanceObject = appearanceElement.getAsJsonObject();

            // Check and validate the "type" property
            JsonElement typeElement = appearanceObject.get("type");
            if (typeElement != null && typeElement.isJsonPrimitive() && typeElement.getAsJsonPrimitive().isString()) {
                String type = typeElement.getAsString();
                if (type.equals("texture")) {
                    handleTextureAppearance(appearanceObject, lineNumber, currentEntityID, slimeDataB);
                    return; // No need to set fallback appearance if texture is handled
                } else if (type.equals("color")) {
                    handleColorAppearance(appearanceObject, lineNumber, currentEntityID, slimeDataB);
                    return; // No need to set fallback appearance if color is handled
                }
            }
        }

        // Set the appearance property in slimeDataB with fallback data
        slimeDataB.setAppearance(fallbackAppearance);
    }

    // Create the fallback appearance object
    private static JsonObject createFallbackAppearance() {
        JsonObject fallbackAppearance = new JsonObject();
        fallbackAppearance.addProperty("type", "texture");
        fallbackAppearance.addProperty("source", fallbackTexture.toString());
        return fallbackAppearance;
    }





    //</editor-fold>

    //<editor-fold desc="handleTextureAppearance">
    private static void handleTextureAppearance(JsonObject appearanceObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        // Handle "texture" type
        JsonElement sourceElement = appearanceObject.get("source");
        if (sourceElement != null && sourceElement.isJsonPrimitive() && sourceElement.getAsString() != null) {
            String source = sourceElement.getAsString();
            // Validate and process the "source" value for "texture" type
            if (!isValidTextureSource(source, lineNumber, currentEntityID, slimeDataB)) {
                // Invalid source, fallback has already been set
                return;
            }
            // Process the valid source here and update slimeDataB accordingly
        } else {
            LOGGER.warn("Line {}: 'source' property missing or invalid for 'texture' appearance. Skipping.", lineNumber);
        }
    }
    //</editor-fold>

    //<editor-fold desc="handleColorAppearance">
    private static boolean handleColorAppearance(JsonObject appearanceObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        // Handle "color" type
        JsonElement sourceElement = appearanceObject.get("source");
        if (sourceElement != null && sourceElement.isJsonArray()) {
            JsonArray colorArray = sourceElement.getAsJsonArray();
            // Validate and process the "source" value for "color" type and update slimeDataB accordingly
            boolean isValidColorSource = isValidColorSource(colorArray, lineNumber, currentEntityID, slimeDataB);
            if (!isValidColorSource) {
                LOGGER.warn("Line {}: Invalid 'source' property for 'color' appearance. Using the fallback texture.", lineNumber);
                // Use the fallback appearance for "color" type and update slimeDataB accordingly
                setFallbackAppearance(slimeDataB, DEFAULT_SLIME_TEXTURE);
            }
            return isValidColorSource;
        } else {
            LOGGER.warn("Line {}: 'source' property missing or invalid for 'color' appearance. Using the fallback texture.", lineNumber);
            // Use the fallback appearance for "color" type and update slimeDataB accordingly
            setFallbackAppearance(slimeDataB, DEFAULT_SLIME_TEXTURE);
            return false;
        }
    }


    //</editor-fold>

    //<editor-fold desc="handleTransformTo">
    private static void handleTransformTo(JsonObject jsonObject, int lineNumber, String currentEntityID, SammieJamSlimeData slimeData, Set<String> encounteredEntityIDs) {
        // Check if the "transformTo" property exists in the JSON object
        JsonElement transformToElement = jsonObject.get("transformTo");

        if (transformToElement == null || !transformToElement.isJsonObject()) {
            // "transformTo" property is missing or not an object, supply defaults
            LOGGER.warn("Line {}: Missing or invalid 'transformTo' property for '{}'. Defaulting to a listType of 'blacklist' with an empty list array.", lineNumber, currentEntityID);

            // Set the default values
            JsonObject defaultTransformTo = new JsonObject();
            defaultTransformTo.addProperty("listType", "blacklist");
            defaultTransformTo.add("list", new JsonArray()); // An empty list
            jsonObject.add("transformTo", defaultTransformTo);
        } else {
            // The "transformTo" property exists and is an object
            JsonObject transformToObject = transformToElement.getAsJsonObject();

            // Extract the "listType" property
            boolean listType = isValidListType(transformToObject, lineNumber, currentEntityID);

            if (!listType) {
                // Log a warning for invalid "listType" property and set default
                LOGGER.warn("Line {}: Invalid 'listType' value for '{}' in 'transformTo'. Using fallback (blacklist).", lineNumber, currentEntityID);

                // Set the default listType value
                transformToObject.addProperty("listType", "blacklist");
            }

            // Extract or create the "list" property
            JsonElement listElement = transformToObject.get("list");
            if (listElement == null || !listElement.isJsonArray()) {
                LOGGER.warn("Line {}: Missing or invalid 'transformTo' 'list' property. Using empty list.", lineNumber);

                // Set the default list as an empty array
                transformToObject.add("list", new JsonArray());
            } else if (!listElement.isJsonArray()) {
                // Log a warning for invalid "list" property and replace with an empty array
                LOGGER.warn("Line {}: Invalid 'transformTo' 'list' property. Using empty list.", lineNumber);
                transformToObject.remove("list");
                transformToObject.add("list", new JsonArray());
            }

            // Call isValidList to validate and populate the list
            JsonArray listArray = transformToObject.getAsJsonArray("list");
            List<String> list = isValidList(listArray, lineNumber, currentEntityID, encounteredEntityIDs);

            // Remove and re-add the list property to ensure proper JSON formatting
            transformToObject.remove("list");
            for (String entityID : list) {
                listArray.add(entityID);
            }
            transformToObject.add("list", listArray);
        }
    }
    //</editor-fold>

    //<editor-fold desc="handleSpawningEnable">
    private static boolean handleSpawningEnable(JsonObject jsonObject, int lineNumber, String currentEntityID) {
        // Check if the "spawningEnable" property exists in the JSON object
        JsonElement spawningEnableElement = jsonObject.get("spawningEnable");

        if (spawningEnableElement != null && spawningEnableElement.isJsonPrimitive()) {
            boolean spawningEnable = spawningEnableElement.getAsBoolean();
            return spawningEnable;
        } else {
            // Log a warning for missing or invalid "spawningEnable" property
            LOGGER.warn("Line {}: Missing or invalid 'spawningEnable' property. Defaulting to 'true'.", lineNumber);
            // Return true as the default value
            return true;
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

        Set<ItemStack> items = ItemMetaHelper.getFromString("item ID", itemID, true);

        if (items.isEmpty()) {
            LOGGER.warn("Invalid item ID format for '{}'. Skipping.", itemID);
            return false;
        }

        return true; // Valid item ID format
    }


    //</editor-fold>

    //<editor-fold desc="isValidTextureSource">
    private static boolean isValidTextureSource(String source, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        if (!source.endsWith(".png") && !source.endsWith(".json")) {
            // The provided source does not have a '.png' or '.json' extension
            LOGGER.warn("Line {}: Invalid 'source' property format for 'texture' appearance for '{}'. Using the fallback texture.", lineNumber, currentEntityID);
            // Use the fallback appearance for "texture" type and update slimeDataB accordingly
            setFallbackAppearance(slimeDataB, DEFAULT_SLIME_TEXTURE);
            return false;
        }
        return true;
    }


    //</editor-fold>

    //<editor-fold desc="isValidColorSource">
    private static boolean isValidColorSource(JsonArray colorArray, int lineNumber, String currentEntityID, SammieJamSlimeData slimeDataB) {
        int numValues = colorArray.size();

        if (numValues < 1 || numValues > 4) {
            // Invalid number of values in the array
            LOGGER.warn("Line {}: Invalid 'source' property format for 'color' appearance. Using the fallback texture.", lineNumber);
            // Use the fallback appearance for "color" type and update slimeDataB accordingly
            setFallbackAppearance(slimeDataB, DEFAULT_SLIME_TEXTURE);
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

        // Now you can use these normalized values as needed and update slimeDataB accordingly

        return true; // Source is valid
    }




    //</editor-fold>

    //<editor-fold desc="isValidListType">
    private static boolean isValidListType(JsonObject transformToObject, int lineNumber, String currentEntityID) {
        JsonElement listTypeElement = transformToObject.get("listType");

        if (listTypeElement != null && listTypeElement.isJsonPrimitive()) {
            String listType = listTypeElement.getAsString();
            if ("whitelist".equals(listType) || "blacklist".equals(listType)) {
                // Valid "listType" value found in JSON
                return "whitelist".equals(listType); // Return true for "whitelist" and false for "blacklist"
            } else {
                // Log a warning for an invalid listType
                LOGGER.warn("Line {}: Invalid 'listType' value for '{}' in 'transformTo'. Using fallback (blacklist).", lineNumber, currentEntityID);
            }
        } else {
            // Log a warning for missing or invalid "listType" property
            LOGGER.warn("Line {}: Missing or invalid 'listType' property in 'transformTo'. Using fallback (blacklist).", lineNumber);
        }

        // Return false only if you are sure that "blacklist" should be the default behavior.
        // Otherwise, consider returning true as a safe default.
        return false;
    }


    //</editor-fold>

    //<editor-fold desc="isValidList">
    private static List<String> isValidList(JsonArray listArray, int lineNumber, String currentEntityID, Set<String> encounteredEntityIDs) {
        List<String> list = new ArrayList<>();

        for (JsonElement entityIDElement : listArray) {
            if (entityIDElement.isJsonPrimitive()) {
                String entityID = entityIDElement.getAsString();

                // Check if the entityID matches the currentEntityID
                if (entityID.equals(currentEntityID)) {
                    LOGGER.warn("Line {}: Entity ID '{}' cannot be in its own transformTo 'list'. Skipping.", lineNumber, currentEntityID);
                    continue;
                }

                // Check if the entityID matches any already encountered entityID
                if (!encounteredEntityIDs.contains(entityID)) {
                    LOGGER.warn("Line {}: Entity ID '{}' in 'transformTo' 'list' is not a valid entity ID. Skipping.", lineNumber, entityID);
                    continue;
                }

                // Valid entity ID, add it to the list
                list.add(entityID);
            } else {
                // Log a warning for invalid entity ID format
                LOGGER.warn("Line {}: Invalid entity ID format in 'transformTo' 'list' array. Skipping.", lineNumber);
            }
        }

        return list;
    }
    //</editor-fold>


    ////////////////










    private static SammieJamSlimeData createSammieJamSlimeData(String currentEntityID, String currentDisplayName, String primaryColor, String secondaryColor, List<SammieJamSlimeData.TransformItem> transformItemsDataList, String listType, List<String> list, boolean spawningEnable) {
        SammieJamSlimeData slimeData = new SammieJamSlimeData();
        slimeData.setEntityID(currentEntityID);
        if (currentDisplayName != null) {
            slimeData.setDisplayName(currentDisplayName);
        }
        slimeData.setSpawnEggColors(new SammieJamSlimeData.SpawnEggColors(primaryColor, secondaryColor));
        slimeData.setTransformItems(transformItemsDataList);

        // Create and set the TransformTo object
        SammieJamSlimeData.TransformTo transformTo = new SammieJamSlimeData.TransformTo();
        transformTo.setListType(listType);
        transformTo.setList(list);

        slimeData.setTransformTo(transformTo); // Set the transformTo property
        slimeData.setSpawningEnable(spawningEnable); // Set the spawningEnable property

        return slimeData;
    }





}