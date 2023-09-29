package kombee.sammiejamslimes;

import kombee.sammiejamslimes.config.ConfigFolderHandler;
import kombee.sammiejamslimes.data.DataManager;
import kombee.sammiejamslimes.entities.SlimeEntityRegistry;
import kombee.sammiejamslimes.localization.LocalizationHandler;
import net.minecraftforge.common.MinecraftForge;
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

    public void init() {
        SlimeEntityRegistry.registerEntities();
        MinecraftForge.EVENT_BUS.register(new SlimeEntityRegistry());
    }
    public void postInit() {
    }
}

