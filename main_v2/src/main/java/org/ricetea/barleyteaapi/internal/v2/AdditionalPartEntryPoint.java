package org.ricetea.barleyteaapi.internal.v2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.internal.additional.IAdditionalPartEntryPoint;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.internal.connector.BulitInSoftDepend;
import org.ricetea.barleyteaapi.internal.connector.ProtocolLibConnector;
import org.ricetea.barleyteaapi.internal.listener.EntitySpawnListener;
import org.ricetea.barleyteaapi.internal.v2.connector.patch.ProtocolLibConnectorPatchImpl;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.DefaultItemRendererImpl2;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.util.AlternativeItemStateImpl2;
import org.ricetea.barleyteaapi.internal.v2.listener.CraftListener2;
import org.ricetea.barleyteaapi.internal.v2.listener.patch.EntitySpawnListenerPatchImpl;
import org.ricetea.utils.ObjectUtil;

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
        apiInst.loadApiImplementation(Bukkit.getServicesManager(), new AlternativeItemStateImpl2(), AlternativeItemState.class);
    }

    @Override
    public void applyPatchs() {
        EntitySpawnListener.getInstance().addPatch(EntitySpawnListenerPatchImpl.getInstance());
        if (apiInst.getSoftDependRegister().get(BulitInSoftDepend.ProtocolLib) instanceof ProtocolLibConnector connector) {
            connector.addPatch(ProtocolLibConnectorPatchImpl.getInstance());
        }
    }

    @Override
    public void onDisable() {
        ObjectUtil.safeCall(EntitySpawnListenerPatchImpl.getInstanceUnsafe(), EntitySpawnListener.getInstance()::removePatch);
        if (apiInst.getSoftDependRegister().get(BulitInSoftDepend.ProtocolLib) instanceof ProtocolLibConnector connector) {
            ProtocolLibConnectorPatchImpl patch = ProtocolLibConnectorPatchImpl.getInstanceUnsafe();
            if (patch != null)
                connector.removePatch(patch);
        }
    }

}
