package kombee.sammiejamslimes.entities;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.data.DataManager;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime1;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime2;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime3;
import kombee.sammiejamslimes.entities.jamslimes.EntityJamSlime4;
import kombee.sammiejamslimes.render.ColorRender;
import kombee.sammiejamslimes.render.TextureRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlimeEntityRegistry {

    private static int entityId = 0;
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    private static Map<Class<? extends EntityJamSlimeBase>, SammieJamSlimeData> entityDataMap = new HashMap<>();
    public static void registerEntities() {
        List<SammieJamSlimeData> slimeDataList = DataManager.getSlimeDataList();

        for (int entityCounter = 1; entityCounter <= slimeDataList.size(); entityCounter++) {
            String entityID = "slime" + entityCounter;
            String fullName = SammieJamSlimes.MODID + ":" + entityID;
            int trackingRange = 80;
            int updateFrequency = 3;
            boolean sendsVelocityUpdates = true;
            int eggPrimaryColor = getColorFromHexString(slimeDataList.get(entityCounter - 1).getSpawnEggColors().getPrimary());
            int eggSecondaryColor = getColorFromHexString(slimeDataList.get(entityCounter - 1).getSpawnEggColors().getSecondary());

            Class<? extends EntityJamSlimeBase> entityClass = getEntityClassForCounter(entityCounter);

            entityDataMap.put(entityClass, slimeDataList.get(entityCounter - 1));

            registerEntity(entityClass, entityID, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimaryColor, eggSecondaryColor);

            Biome[] spawnBiomes = getBiomes();
            EntityRegistry.addSpawn(entityClass, 1000, 1, 3, EnumCreatureType.MONSTER, spawnBiomes); //TODO: Change weightedProb back to vanilla value!! It's 1000 for testing
        }
    }


    private static int getColorFromHexString(String hexColor) {
        return Integer.parseInt(hexColor, 16);
    }

    public static SammieJamSlimeData getEntityDataForClass(Class<? extends EntityJamSlimeBase> entityClass) {
        return entityDataMap.get(entityClass);
    }

    private static Class<? extends EntityJamSlimeBase>[] entityClasses = new Class[]{
            EntityJamSlime1.class,
            EntityJamSlime2.class,
            EntityJamSlime3.class,
            EntityJamSlime4.class
    };

    private static Class<? extends EntityJamSlimeBase> getEntityClassForCounter(int counter) {
        int adjustedCounter = counter - 1; // Adjust the counter to be zero-based
        if (adjustedCounter >= 0 && adjustedCounter < entityClasses.length) {
            return entityClasses[adjustedCounter];
        } else {
            return EntityJamSlime1.class; // Default to EntityJamSlime1 if counter is out of bounds
        }
    }


    private static void registerEntity(Class<? extends EntityJamSlimeBase> entityClass, String entityID, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimaryColor, int eggSecondaryColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(SammieJamSlimes.MODID, entityID), entityClass, entityID, entityId++, SammieJamSlimes.instance, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimaryColor, eggSecondaryColor);
    }

    @SideOnly(Side.CLIENT)
    public static void registerEntityRenderers() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        Map<Class<? extends EntityJamSlimeBase>, String> entityTextureMap = new HashMap<>();

        entityTextureMap.put(EntityJamSlime1.class, "slime1.png");
        entityTextureMap.put(EntityJamSlime2.class, "slime2.png");
        entityTextureMap.put(EntityJamSlime3.class, "slime3.png");
        entityTextureMap.put(EntityJamSlime4.class, "slime4.png");

        for (int entityCounter = 0; entityCounter < entityClasses.length; entityCounter++) {
            String entityID = "slime" + (entityCounter + 1); // Use the same naming convention
            SammieJamSlimeData slimeData = getEntityDataForClass(entityClasses[entityCounter]);

            if (slimeData != null) {
                String appearanceType = slimeData.getAppearance().getType();
                Object appearanceSource = slimeData.getAppearance().getSource();

                RenderLiving<EntityJamSlimeBase> customRenderer = null;

                if ("texture".equals(appearanceType)) {
                    String textureFilename = entityTextureMap.get(entityClasses[entityCounter]);
                    customRenderer = new TextureRender(renderManager, textureFilename);

                } else if ("color".equals(appearanceType)) {
                    customRenderer = new ColorRender(renderManager, slimeData, appearanceType, appearanceSource);
                } else {
                }

                if (customRenderer != null) {
                    renderManager.entityRenderMap.put(entityClasses[entityCounter], customRenderer);
                }
            }
        }
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
}


