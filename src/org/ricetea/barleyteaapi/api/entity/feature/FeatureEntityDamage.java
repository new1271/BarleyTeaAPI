package org.ricetea.barleyteaapi.api.entity.feature;

public interface FeatureEntityDamage {
    boolean handleEntityDamagedByEntity(DataEntityDamagedByEntity data);

    boolean handleEntityDamagedByBlock(DataEntityDamagedByBlock data);

    boolean handleEntityDamagedByNothing(DataEntityDamagedByNothing data);

    boolean handleEntityAttack(DataEntityDamagedByEntity data);
}
