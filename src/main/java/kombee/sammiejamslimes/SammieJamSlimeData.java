package kombee.sammiejamslimes;

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

    public String getEntityID() {
        return entityID;
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
}
