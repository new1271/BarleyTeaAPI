package org.ricetea.barleyteaapi.api.entity.feature;

public interface FeatureNaturalSpawn {
    boolean handleNaturalSpawn(DataCreatureNaturalSpawn data);

    double getPosibility();
}
