package kombee.sammiejamslimes.data;

import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class SammieJamSlimeData {
    private String entityID;
    private String displayName;
    private SpawnEggColors spawnEggColors;
    private List<TransformItem> transformItems;
    private Appearance appearance;
    private TransformTo transformTo;
    private boolean spawningEnable;
    private int dimension;
    private int spawnWeight;
    private int minGroupSize;
    private int maxGroupSize;


    public SammieJamSlimeData() {
        this.entityID = "";
        this.displayName = "";
        this.spawnEggColors = new SpawnEggColors("FF0000", "00FF00");
        this.transformItems = new ArrayList<>();
        this.appearance = new Appearance("texture");
        this.transformTo = new TransformTo();
        this.spawningEnable = true;
        this.spawnWeight = 10;
        this.minGroupSize = 1;
        this.maxGroupSize = 1;
    }

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

    // Getters and setters for fields
    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
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

    public int getDimension() {
        return dimension;
    }

    public int getSpawnWeight() {
        return spawnWeight;
    }

    public int getMinGroupSize() {
        return minGroupSize;
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
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

        public void setPrimary(String primary) {
            this.primary = primary;
        }

        public String getSecondary() {
            return secondary;
        }

        public void setSecondary(String secondary) {
            this.secondary = secondary;
        }
    }


    public static class TransformItem {
        public String getItemID;
        private String itemID;
        private int metadata;
        private boolean consumeItem;
        private boolean reduceDurability;
        private NBTTagCompound nbtData;
        private String transformToEntityID;
        private int index;

        public TransformItem() {
            this.itemID = ""; // Default itemID as an empty string
            this.metadata = 0; // Default metadata as 0. This is how Minecraft does it
            this.consumeItem = true; // Default consumeItem as true. Consider changing later
            this.reduceDurability = false; // Default reduceDurability as false, to account for items without durability
            this.nbtData = null; // Default nbtData as null
        }

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
        public String getTransformToEntityID() {
            return transformToEntityID;
        }
        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static class Appearance {
        private String type;
        private Object source;

        public Appearance(String type) {
            this.type = type;
            this.source = getDefaultSourceForType(type);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }

        public Object getDefaultSourceForType(String type) {
            if ("texture".equals(type)) {
                return "minecraft:slime";
            } else if ("color".equals(type)) {
                return Arrays.asList(0, 0, 0, 0);
            } else {
                return null;
            }
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

        public TransformTo() {
            this.listType = "blacklist";
            this.list = new ArrayList<>();
        }
    }
}
