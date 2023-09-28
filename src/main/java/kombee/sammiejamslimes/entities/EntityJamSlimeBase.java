package kombee.sammiejamslimes.entities;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.monster.IMob;

public class EntityJamSlimeBase extends EntitySlime implements IMob {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private static final DataParameter<String> SLIME_ENTITY_ID = EntityDataManager.createKey(EntityJamSlimeBase.class, DataSerializers.STRING);

    public EntityJamSlimeBase(World worldIn) {
        super(worldIn);
        this.dataManager.register(SLIME_ENTITY_ID, "");
    }

    public SammieJamSlimeData getSammieJamSlimeData() {
        Class<? extends EntityJamSlimeBase> entityClass = this.getClass();
        return SlimeEntityRegistry.getEntityDataForClass(entityClass);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SLIME_ENTITY_ID.equals(key)) {
        }
    }
}






