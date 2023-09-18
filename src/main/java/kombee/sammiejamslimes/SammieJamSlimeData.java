package kombee.sammiejamslimes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class SammieJamSlimeData {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private String entityID;
    private String displayName;
    private SpawnEggColors spawnEggColors;
    private List<TransformItem> transformItems;
    private Appearance appearance;
    private TransformTo transformTo;
    private boolean spawningEnable;
    private boolean entityIDError = false;
    private boolean displayNameError = false;

    // Getters and Setters for slime data
    public String getEntityID() {
        return entityID;
    }

    // Currently unused. Validation is being performed during parse. Modify this later based on needs of code handling entity IDs
    public void setEntityID(String entityID) {
        // Validate the entityID before setting it
        if (isValidEntityID(entityID)) {
            this.entityID = entityID;
            entityIDError = false; // Reset the error flag if valid
        } else {
            entityIDError = true; // Set the error flag if invalid
            LOGGER.error("Invalid entityID: {}. Expected format: lowercase alphanumeric or underscore.", entityID);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    // Same as entityID. Revisit later. Validation occurs in parse.
    public void setDisplayName(String displayName) {
        // Validate displayName to ensure it's not empty
        if (displayName != null && !displayName.isEmpty()) {
            this.displayName = displayName;
            displayNameError = false; // Reset the error flag if valid
        } else {
            displayNameError = true; // Set the error flag if empty
            LOGGER.warn("displayName is empty. Using unlocalized name. Set displayName in slimes.json or in your localization file.");
        }
    }

    public SpawnEggColors getSpawnEggColors() {
        return spawnEggColors;
    }

    public void setSpawnEggColors(JsonObject spawnEggColorsJson) {
        // Validate the spawnEggColorsJson object
        if (isValidSpawnEggColorsJson(spawnEggColorsJson)) {
            // Extract and set the primary and secondary colors
            this.spawnEggColors = new SpawnEggColors(
                    spawnEggColorsJson.get("primary").getAsString(),
                    spawnEggColorsJson.get("secondary").getAsString()
            );
        } else {
            // Set default colors or handle the error as needed
            this.spawnEggColors = new SpawnEggColors("FF0000", "00FF00");
        }
    }


    public List<TransformItem> getTransformItems() {
        return transformItems;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public TransformTo getTransformTo() {
        return transformTo;
    }

    public boolean isSpawningEnabled() {
        return spawningEnable;
    }

    public static class SpawnEggColors {
        private String primary;
        private String secondary;

        // Constructor that accepts primary and secondary colors
        public SpawnEggColors(String primary, String secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        public String getPrimary() {
            return primary;
        }

        public String getSecondary() {
            return secondary;
        }
    }


    public static class TransformItem {
        private String itemID;
        private boolean consumeItem;
        private boolean reduceDurability;

        public String getItemID() {
            return itemID;
        }

        public boolean shouldConsumeItem() {
            return consumeItem;
        }

        public boolean shouldReduceDurability() {
            return reduceDurability;
        }
    }

    public static class Appearance {
        private String type;
        private Map<String, Object> source;

        public String getType() {
            return type;
        }

        public Map<String, Object> getSource() {
            return source;
        }
    }

    public static class TransformTo {
        private String listType;
        private List<String> list;

        public String getListType() {
            return listType;
        }

        public List<String> getList() {
            return list;
        }
    }

    //Invalid formatting and data type handling


    // Unused, leftover from validation move to jsonfileloader. Maybe usable later, unsure.
    public boolean hasEntityIDError() {
        return entityIDError;
    }

    public boolean hasDisplayNameError() {
        return displayNameError;
    }
    public boolean isValidEntityID(String entityID) {
        // Check if the entityID is not null and not empty
        if (entityID == null || entityID.isEmpty()) {
            return false;
        }

        // Checking for entityID formatting. Accepts lowercase, alphanumeric, underscore.
        return entityID.matches("[a-z0-9_]");
    }

    private boolean isValidSpawnEggColorsJson(JsonObject spawnEggColorsJson) {
        // Check if spawnEggColorsJson is not null
        if (spawnEggColorsJson == null) {
            return false;
        }

        // Check if "primary" and "secondary" properties exist and have valid formats
        JsonElement primaryElement = spawnEggColorsJson.get("primary");
        JsonElement secondaryElement = spawnEggColorsJson.get("secondary");

        if (primaryElement == null || !primaryElement.isJsonPrimitive() || !isValidColor(primaryElement.getAsString())) {
            return false;
        }

        if (secondaryElement == null || !secondaryElement.isJsonPrimitive() || !isValidColor(secondaryElement.getAsString())) {
            return false;
        }

        // Check if "primary" color is empty
        if (primaryElement.getAsString().isEmpty()) {
            LOGGER.warn("The 'primary' color is empty in spawnEggColors. Using default primary color.");
        }

        // Check if "secondary" color is empty
        if (secondaryElement.getAsString().isEmpty()) {
            LOGGER.warn("The 'secondary' color is empty in spawnEggColors. Using default secondary color.");
        }

        return true;
    }

    private boolean isValidColor(String color) {
        // You can implement your color format validation logic here
        // For example, check if the color is a valid hexadecimal RGB code
        // This depends on your specific requirements
        // Here's a basic example:
        return color.matches("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$");
    }

}
