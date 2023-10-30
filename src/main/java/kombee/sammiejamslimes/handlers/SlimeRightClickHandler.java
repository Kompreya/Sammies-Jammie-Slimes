package kombee.sammiejamslimes.handlers;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlimeRightClickHandler {
    private final Class<? extends EntityJamSlimeBase> slimeClass;
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public SlimeRightClickHandler(Class<? extends EntityJamSlimeBase> slimeClass) {
        this.slimeClass = slimeClass;
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isRemote) {
            return;
        }
        if (event.getTarget() != null && slimeClass.isInstance(event.getTarget())) {
            EntityJamSlimeBase slime = (EntityJamSlimeBase) event.getTarget();
            int slimeSize = slime.getSlimeSize();
            ItemStack heldItem = event.getEntityPlayer().getHeldItem(event.getHand());

            if (!heldItem.isEmpty()) {
                float currentHealth = slime.getHealth();
                SlimeTransformHandler.transformSlime(slime, heldItem, slimeSize, currentHealth);
            }
        }
    }


}
