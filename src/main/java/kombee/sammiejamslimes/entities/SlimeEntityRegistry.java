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
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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

            registerEntity(entityClass, entityID, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimaryColor, eggSecondaryColor);

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

    private static Class<? extends EntityJamSlimeBase>[] entityClasses = new Class[]{
            EntityJamSlime1.class,
            EntityJamSlime2.class,
            EntityJamSlime3.class,
            EntityJamSlime4.class
    };

    private static Class<? extends EntityJamSlimeBase> getEntityClassForCounter(int counter) {
        if (counter >= 0 && counter < entityClasses.length) {
            return entityClasses[counter];
        } else {
            return EntityJamSlime1.class; // Default to EntityJamSlime1 if counter is out of bounds
        }
    }

    private static void registerEntity(Class<? extends EntityJamSlimeBase> entityClass, String entityID, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimaryColor, int eggSecondaryColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(SammieJamSlimes.MODID, entityID), entityClass, entityID, entityId++, SammieJamSlimes.instance, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimaryColor, eggSecondaryColor);
    }

    @SideOnly(Side.CLIENT)
    public static void registerEntityRenderers() {
        List<SammieJamSlimeData> slimeDataList = DataManager.getSlimeDataList();
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        for (SammieJamSlimeData slimeData : slimeDataList) {
            Class<? extends EntityJamSlimeBase> entityClass = getEntityClassForRegistrationOrder(slimeDataList.indexOf(slimeData));
            String appearanceType = slimeData.getAppearance().getType();
            Object appearanceSource = slimeData.getAppearance().getSource();

            RenderLiving<EntityJamSlimeBase> customRenderer = null;

            if ("texture".equals(appearanceType)) {
                customRenderer = new TextureRender(renderManager, slimeData, appearanceType, appearanceSource);
            } else if ("color".equals(appearanceType)) {
                customRenderer = new ColorRender(renderManager, slimeData, appearanceType, appearanceSource);
            } else {
                // Handle the default case if needed
            }

            if (customRenderer != null) {
                renderManager.entityRenderMap.put(entityClass, customRenderer);
            }
        }
    }


    private static Class<? extends EntityJamSlimeBase> getEntityClassForRegistrationOrder(int registrationOrder) {
        Class<? extends EntityJamSlimeBase>[] entityClasses = new Class[]{
                EntityJamSlime1.class,
                EntityJamSlime2.class,
                EntityJamSlime3.class,
                EntityJamSlime4.class
        };

        if (registrationOrder >= 0 && registrationOrder < entityClasses.length) {
            return entityClasses[registrationOrder];
        } else {
            return EntityJamSlime1.class; // Default to EntityJamSlime1 if out of bounds
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


