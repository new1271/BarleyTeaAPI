package org.ricetea.barleyteaapi.internal.v2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.internal.additional.IAdditionalPartEntryPoint;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.internal.connector.BulitInSoftDepend;
import org.ricetea.barleyteaapi.internal.connector.InteractiveChatConnector;
import org.ricetea.barleyteaapi.internal.connector.ProtocolLibConnector;
import org.ricetea.barleyteaapi.internal.listener.EntityDamageListener;
import org.ricetea.barleyteaapi.internal.listener.EntitySpawnListener;
import org.ricetea.barleyteaapi.internal.listener.patch.EntityDamageListenerPatch;
import org.ricetea.barleyteaapi.internal.v2.connector.patch.InteractiveChatConnectorPatchImpl;
import org.ricetea.barleyteaapi.internal.v2.connector.patch.ProtocolLibConnectorPatchImpl;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.DefaultItemRendererImpl2;
import org.ricetea.barleyteaapi.internal.v2.item.renderer.util.AlternativeItemStateImpl2;
import org.ricetea.barleyteaapi.internal.v2.listener.CraftListener2;
import org.ricetea.barleyteaapi.internal.v2.listener.patch.EntityDamageListenerPatchImpl;
import org.ricetea.barleyteaapi.internal.v2.listener.patch.EntitySpawnListenerFilterImpl;
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
        EntityDamageListener.getInstance().addPatch(EntityDamageListenerPatchImpl.getInstance());
        EntitySpawnListener.getInstance().addFilter(EntitySpawnListenerFilterImpl.getInstance());
        var softDependRegister = apiInst.getSoftDependRegister();
        if (softDependRegister.get(BulitInSoftDepend.ProtocolLib) instanceof ProtocolLibConnector connector) {
            connector.addPatch(ProtocolLibConnectorPatchImpl.getInstance());
        }
        if (softDependRegister.get(BulitInSoftDepend.InteractiveChat) instanceof InteractiveChatConnector connector) {
            connector.addPatch(InteractiveChatConnectorPatchImpl.getInstance());
        }
    }

    @Override
    public void onDisable() {
        EntityDamageListener.getInstance().removePatch(EntityDamageListenerPatchImpl.getInstance());
        EntitySpawnListener.getInstance().removeFilter(EntitySpawnListenerFilterImpl.getInstance());
        var softDependRegister = apiInst.getSoftDependRegister();
        if (softDependRegister.get(BulitInSoftDepend.ProtocolLib) instanceof ProtocolLibConnector connector) {
            ProtocolLibConnectorPatchImpl patch = ProtocolLibConnectorPatchImpl.getInstanceUnsafe();
            if (patch != null)
                connector.removePatch(patch);
        }
        if (softDependRegister.get(BulitInSoftDepend.InteractiveChat) instanceof InteractiveChatConnector connector) {
            InteractiveChatConnectorPatchImpl patch = InteractiveChatConnectorPatchImpl.getInstanceUnsafe();
            if (patch != null)
                connector.removePatch(patch);
        }
    }

}
