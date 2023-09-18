package kombee.sammiejamslimes;

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

    // Getters and Setters for slime data
    public String getEntityID() {
        return entityID;
    }

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

    public SpawnEggColors getSpawnEggColors() {
        return spawnEggColors;
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

    public boolean hasEntityIDError() {
        return entityIDError;
    }
    private boolean isValidEntityID(String entityID) {
        // Check if the entityID is not null and not empty
        if (entityID == null || entityID.isEmpty()) {
            return false;
        }

        // Checking for entityID formatting. Accepts lowercase, alphanumeric, underscore.
        return entityID.matches("[a-z0-9_]");
    }

}
