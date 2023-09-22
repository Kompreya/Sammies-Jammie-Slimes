package kombee.sammiejamslimes.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kombee.sammiejamslimes.SammieJamSlimes;
import net.minecraft.nbt.NBTTagCompound;
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
        this.entityID = entityID;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Same as entityID. Revisit later. Validation occurs in parse.
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public SpawnEggColors getSpawnEggColors() {
        return spawnEggColors;
    }

    /*
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
    */
    public void setSpawnEggColors(SpawnEggColors spawnEggColors) {
        this.spawnEggColors = spawnEggColors;
    }


    public void setTransformItems(List<TransformItem> transformItems) {
        this.transformItems = transformItems;
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

    public void setTransformTo(TransformTo transformTo) {
        this.transformTo = transformTo;
    }

    public boolean isSpawningEnable() {
        return spawningEnable;
    }

    public void setSpawningEnable(boolean spawningEnable) {
        this.spawningEnable = spawningEnable;
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
        private int metadata; // Metadata field
        private boolean consumeItem;
        private boolean reduceDurability;
        private NBTTagCompound nbtData; // New field for NBT data

        // Constructor with metadata and NBT data parameters
        public TransformItem(String itemID, int metadata, boolean consumeItem, boolean reduceDurability, NBTTagCompound nbtData) {
            this.itemID = itemID;
            this.metadata = metadata;
            this.consumeItem = consumeItem;
            this.reduceDurability = reduceDurability;
            this.nbtData = nbtData;
        }

        // Getters for all fields
        public String getItemID() {
            return itemID;
        }

        public int getMetadata() {
            return metadata;
        }

        public boolean isConsumeItem() {
            return consumeItem;
        }

        public boolean isReduceDurability() {
            return reduceDurability;
        }

        public NBTTagCompound getNbtData() {
            return nbtData;
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

        public void setListType(String listType) {
            this.listType = listType;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }



}
