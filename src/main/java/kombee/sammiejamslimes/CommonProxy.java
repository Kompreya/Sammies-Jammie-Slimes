package kombee.sammiejamslimes;

import kombee.sammiejamslimes.config.ConfigFolderHandler;
import kombee.sammiejamslimes.data.DataManager;
import kombee.sammiejamslimes.handlers.SlimeRightClickHandler;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import kombee.sammiejamslimes.entities.SlimeEntityRegistry;
import kombee.sammiejamslimes.localization.LocalizationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class CommonProxy {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public void preInit(FMLPreInitializationEvent event) {
        ConfigFolderHandler.preInit(event);
        DataManager.loadSlimeData();
        LocalizationHandler.generateLocalizationFile();
    }


    public void init(FMLInitializationEvent event) {
        SlimeEntityRegistry.registerEntities();
        MinecraftForge.EVENT_BUS.register(new SlimeEntityRegistry());
        MinecraftForge.EVENT_BUS.register(new SlimeRightClickHandler(EntityJamSlimeBase.class));
    }

    public void postInit() {
    }
}

