package org.ricetea.barleyteaapi.internal.v2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.internal.additional.IAdditionalPartEntryPoint;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.DefaultItemRendererImpl2;
import org.ricetea.barleyteaapi.internal.v2.listener.CraftListener2;

import javax.annotation.Nonnull;

public final class AdditionalPartEntryPoint implements IAdditionalPartEntryPoint {

    @Nonnull
    private final BarleyTeaAPI apiInst;

    public AdditionalPartEntryPoint(@Nonnull BarleyTeaAPI apiInst) {
        this.apiInst = apiInst;
    }

    @Override
    public void onEnable(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(CraftListener2.getInstance(), apiInst);
        apiInst.loadApiImplementation(Bukkit.getServicesManager(), new DefaultItemRendererImpl2(), ItemRenderer.class);
    }

    @Override
    public void onDisable() {

    }

}
