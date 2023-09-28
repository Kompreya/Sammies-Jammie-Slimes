package kombee.sammiejamslimes.entities;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.data.DataManager;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime1;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime2;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime3;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime4;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlimeEntityRegistry {

    private static int entityId = 0;
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private static int entityCounter = 0;

    private static Map<Class<? extends EntityJamSlimeBase>, SammieJamSlimeData> entityDataMap = new HashMap<>();
    public static void registerEntities() {
        List<SammieJamSlimeData> slimeDataList = DataManager.getSlimeDataList();

        for (SammieJamSlimeData slimeData : slimeDataList) {
            String entityID = slimeData.getEntityID();
            String fullName = SammieJamSlimes.MODID + ":" + entityID;
            int trackingRange = 80;
            int updateFrequency = 3;
            boolean sendsVelocityUpdates = true;
            int eggPrimaryColor = getColorFromHexString(slimeData.getSpawnEggColors().getPrimary());
            int eggSecondaryColor = getColorFromHexString(slimeData.getSpawnEggColors().getSecondary());

            Class<? extends EntityJamSlimeBase> entityClass = getEntityClassForCounter(entityCounter);

            entityDataMap.put(entityClass, slimeData);

            registerEntity(entityClass, entityID, fullName, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimaryColor, eggSecondaryColor);

            entityCounter++;

            Biome[] spawnBiomes = getBiomes();
            EntityRegistry.addSpawn(entityClass, 1000, 1, 3, EnumCreatureType.MONSTER, spawnBiomes);
        }
    }

    private static int getColorFromHexString(String hexColor) {
        return Integer.parseInt(hexColor, 16);
    }

    public static SammieJamSlimeData getEntityDataForClass(Class<? extends EntityJamSlimeBase> entityClass) {
        return entityDataMap.get(entityClass);
    }


    private static Class<? extends EntityJamSlimeBase> getEntityClassForCounter(int counter) {
        // Reflection against jamslimes subclasses
        switch (counter) {
            case 0:
                return EntityJamSlime1.class;
            case 1:
                return EntityJamSlime2.class;
            case 2:
                return EntityJamSlime3.class;
            case 3:
                return EntityJamSlime4.class;
            default:
                return EntityJamSlime1.class;
        }
    }


    private static void registerEntity(Class<? extends EntityJamSlimeBase> entityClass, String entityID, String fullName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimaryColor, int eggSecondaryColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(fullName), entityClass, entityID, entityId++, SammieJamSlimes.instance, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimaryColor, eggSecondaryColor);
    }

    private static Biome[] getBiomes() {
        // This just gets ALL biomes for now. Might configure differently later.
        Biome[] biomes = new Biome[Biome.REGISTRY.getKeys().size()];
        int i = 0;

        for (ResourceLocation location : Biome.REGISTRY.getKeys()) {
            Biome biome = Biome.REGISTRY.getObject(location);
            biomes[i++] = biome;
        }

        return biomes;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    }

    @SubscribeEvent
    public void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
    }
}


