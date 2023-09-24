package kombee.sammiejamslimes.data;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;

public class SammieJamSlimeData {
    private String entityID;
    private String displayName;
    private SpawnEggColors spawnEggColors;
    private List<TransformItem> transformItems;
    private Appearance appearance;
    private TransformTo transformTo;
    private boolean spawningEnable;

    // Constructor to initialize fields
    public SammieJamSlimeData(String entityID, String displayName, SpawnEggColors spawnEggColors,
                              List<TransformItem> transformItems, Appearance appearance,
                              TransformTo transformTo, boolean spawningEnable) {
        this.entityID = entityID;
        this.displayName = displayName;
        this.spawnEggColors = spawnEggColors;
        this.transformItems = transformItems;
        this.appearance = appearance;
        this.transformTo = transformTo;
        this.spawningEnable = spawningEnable;
    }

    public SammieJamSlimeData() {
        this.entityID = "defaultEntityID";
        this.displayName = "Default Display Name";
        this.spawnEggColors = new SpawnEggColors("defaultPrimaryColor", "defaultSecondaryColor");
    }

    // Getters and setters for fields
    public String getEntityID() {
        return entityID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public SpawnEggColors getSpawnEggColors() {
        return spawnEggColors;
    }

    public void setSpawnEggColors(SpawnEggColors spawnEggColors) {
        this.spawnEggColors = spawnEggColors;
    }

    public List<TransformItem> getTransformItems() {
        return transformItems;
    }

    public void setTransformItems(List<TransformItem> transformItems) {
        this.transformItems = transformItems;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
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

    // Nested classes remain the same
    public static class SpawnEggColors {
        private String primary;
        private String secondary;

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
        private int metadata;
        private boolean consumeItem;
        private boolean reduceDurability;
        private NBTTagCompound nbtData;

        public TransformItem(String itemID, int metadata, boolean consumeItem,
                             boolean reduceDurability, NBTTagCompound nbtData) {
            this.itemID = itemID;
            this.metadata = metadata;
            this.consumeItem = consumeItem;
            this.reduceDurability = reduceDurability;
            this.nbtData = nbtData;
        }

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
