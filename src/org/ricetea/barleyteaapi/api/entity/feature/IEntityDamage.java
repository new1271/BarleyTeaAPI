package org.ricetea.barleyteaapi.api.entity.feature;

public interface IEntityDamage {
    boolean handleEntityDamagedByEntity(EntityDamagedByEntityData data);

    boolean handleEntityDamagedByBlock(EntityDamagedByBlockData data);

    boolean handleEntityDamagedByNothing(EntityDamagedByNothingData data);

    boolean handleEntityAttack(EntityDamagedByEntityData data);
}
