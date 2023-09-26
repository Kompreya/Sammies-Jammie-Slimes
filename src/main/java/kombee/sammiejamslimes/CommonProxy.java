package kombee.sammiejamslimes;

import kombee.sammiejamslimes.config.ConfigFolderHandler;
import kombee.sammiejamslimes.data.JSONFileLoader;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlime;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CommonProxy {
    private static final int ENTITY_TRACKING_RANGE = 80;
    private static final int ENTITY_UPDATE_FREQUENCY = 3;
    private static final boolean ENTITY_SEND_VELO_UPDATES = true;
    private World overworld;
    private List<SammieJamSlimeData> slimeDataList = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Calling preInit in CommonProxy");
        ConfigFolderHandler.preInit(event);
        overworld = DimensionManager.getWorld(0);
        loadAndLogSlimeData();
    }

    public void init() {
        LOGGER.info("Calling init in CommonProxy");
        registerSlimeEntities(ForgeRegistries.ENTITIES, overworld);
        MinecraftForge.EVENT_BUS.register(this);
    }
    public void postInit() {
        LOGGER.info("Calling postInit in CommonProxy");
        // Common post-initialization code for both client and server
    }

    private void loadAndLogSlimeData() {
        SammieJamSlimeData[] slimeDataArray = JSONFileLoader.loadSlimeData("config/sammiejamslimes/slimes.json", true);

        if (slimeDataArray != null) {
            LOGGER.info("Loaded SammieJamSlime data:");

            // Copy the data to the class member slimeDataList
            slimeDataList.addAll(Arrays.asList(slimeDataArray));

            for (SammieJamSlimeData slimeData : slimeDataList) {
                LOGGER.info("Entity ID: {}", slimeData.getEntityID());
                LOGGER.info("Display Name: {}", slimeData.getDisplayName());
                // Add more logging as needed for data inspection
            }
        } else {
            LOGGER.warn("Failed to load SammieJamSlime data. This may be due to one of the following reasons:\n"
                    + "1. The 'slimes.json' file is missing or incorrectly located in 'config/sammiejamslimes'.\n"
                    + "2. The 'slimes.json' file is empty or contains invalid JSON data.\n"
                    + "3. There was an I/O error while attempting to load the file.\n"
                    + "Please check your mod's configuration and ensure the 'slimes.json' file is properly formatted and located.");
        }
    }

    private void registerSlimeEntities(IForgeRegistry<EntityEntry> registry, World world) {

        int ENTITY_ID = 0;
        for (SammieJamSlimeData slimeData : slimeDataList) {
            ResourceLocation entityRegistryName = new ResourceLocation(SammieJamSlimes.MODID, slimeData.getEntityID());

            // Register the entity using EntityRegistry
            EntityRegistry.registerModEntity(
                    entityRegistryName,
                    EntityJamSlime.class,
                    entityRegistryName.toString(),
                    ENTITY_ID++,
                    SammieJamSlimes.instance,  // Use your mod ID here
                    ENTITY_TRACKING_RANGE,
                    ENTITY_UPDATE_FREQUENCY,
                    ENTITY_SEND_VELO_UPDATES
            );

            // Log the successful registration
            LOGGER.info("Registered entity: {}", entityRegistryName);
        }
    }

    private World getWorldForDimension(int dimension) {
        DimensionType overworldDimensionType = DimensionType.OVERWORLD;
        WorldServer worldServer = DimensionManager.getWorld(dimension);

        if (worldServer != null && worldServer.provider.getDimensionType() == overworldDimensionType) {
            return worldServer;
        } else {
            throw new RuntimeException("Overworld dimension not found.");
        }
    }
}

