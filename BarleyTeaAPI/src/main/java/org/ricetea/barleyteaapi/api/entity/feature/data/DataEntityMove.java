package org.ricetea.barleyteaapi.api.entity.feature.data;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

import javax.annotation.Nonnull;

public final class DataEntityMove extends BaseEntityFeatureData<EntityMoveEvent> {

   public DataEntityMove(@Nonnull EntityMoveEvent event) {
      super(event);
   }

   @Nonnull
   public LivingEntity getEntity() {
      return event.getEntity();
   }

   @Nonnull
   public Location getFrom() {
      return event.getFrom();
   }

   public void setFrom(@Nonnull Location from) {
      event.setFrom(from);
   }

   @Nonnull
   public Location getTo() {
      return event.getTo();
   }

   public void setTo(@Nonnull Location to) {
      event.setTo(to);
   }

   public boolean hasChangedPosition() {
      return event.hasChangedPosition();
   }

   public boolean hasExplicitlyChangedPosition() {
      return event.hasExplicitlyChangedPosition();
   }

   public boolean hasChangedBlock() {
      return event.hasChangedBlock();
   }

   public boolean hasExplicitlyChangedBlock() {
      return event.hasExplicitlyChangedBlock();
   }

   public boolean hasChangedOrientation() {
      return event.hasChangedOrientation();
   }
}
