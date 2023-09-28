package kombee.sammiejamslimes.entities.jamslimes;

import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.world.World;

public class EntityJamSlime3 extends EntityJamSlimeBase {
    public EntityJamSlime3(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean getCanSpawnHere() {
        SammieJamSlimeData slimeData = getSammieJamSlimeData();
        return slimeData != null && slimeData.isSpawningEnable() && super.getCanSpawnHere();
    }
}