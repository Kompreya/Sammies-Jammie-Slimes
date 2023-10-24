package kombee.sammiejamslimes;

import kombee.sammiejamslimes.entities.SlimeEntityRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event); // Call the parent class's method if needed

        // Code that should only run on the client side during pre-initialization
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        SlimeEntityRegistry.registerEntityRenderers();// Call the parent class's method if needed

        // Code that should only run on the client side during initialization
    }

    @Override
    public void postInit() {
        super.postInit(); // Call the parent class's method if needed

        // Code that should only run on the client side during post-initialization
    }
}
