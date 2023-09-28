package kombee.sammiejamslimes.entities.jamslimes;

import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.world.World;

public class EntityJamSlime4 extends EntityJamSlimeBase {
    public EntityJamSlime4(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean getCanSpawnHere() {
        SammieJamSlimeData slimeData = getSammieJamSlimeData();
        return slimeData != null && slimeData.isSpawningEnable() && super.getCanSpawnHere();
    }
}