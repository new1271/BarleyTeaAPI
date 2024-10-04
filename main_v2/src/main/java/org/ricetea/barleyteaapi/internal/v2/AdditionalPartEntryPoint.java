package org.ricetea.barleyteaapi.internal.v2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.internal.additional.IAdditionalPartEntryPoint;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.internal.connector.BulitInSoftDepend;
import org.ricetea.barleyteaapi.internal.connector.ProtocolLibConnector;
import org.ricetea.barleyteaapi.internal.connector.patch.ProtocolLibConnectorPatch;
import org.ricetea.barleyteaapi.internal.v2.connector.handler.ProtocolLibConnectorPatchImpl;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.DefaultItemRendererImpl2;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.util.AlternativeItemStateImpl2;
import org.ricetea.barleyteaapi.internal.v2.listener.CraftListener2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AdditionalPartEntryPoint implements IAdditionalPartEntryPoint {

    @Nonnull
    private final BarleyTeaAPI apiInst;
    @Nullable
    private Object protocolLibConnectorPatch;

    public AdditionalPartEntryPoint(@Nonnull BarleyTeaAPI apiInst) {
        this.apiInst = apiInst;
    }

    @Override
    public void onEnable(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(CraftListener2.getInstance(), apiInst);
        apiInst.loadApiImplementation(Bukkit.getServicesManager(), new DefaultItemRendererImpl2(), ItemRenderer.class);
        apiInst.loadApiImplementation(Bukkit.getServicesManager(), new AlternativeItemStateImpl2(), AlternativeItemState.class);
    }

    @Override
    public void applyPatchs() {
        if (apiInst.getSoftDependRegister().get(BulitInSoftDepend.ProtocolLib) instanceof ProtocolLibConnector connector) {
            ProtocolLibConnectorPatchImpl impl = new ProtocolLibConnectorPatchImpl();
            connector.addPatch(impl);
            protocolLibConnectorPatch = impl;
        }
    }

    @Override
    public void onDisable() {
        if (apiInst.getSoftDependRegister().get(BulitInSoftDepend.ProtocolLib) instanceof ProtocolLibConnector connector) {
            if (protocolLibConnectorPatch instanceof ProtocolLibConnectorPatch patch)
                connector.removePatch(patch);
        }
    }

}
