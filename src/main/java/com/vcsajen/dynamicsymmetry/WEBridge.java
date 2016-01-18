package com.vcsajen.dynamicsymmetry;

import com.flowpowered.math.vector.Vector3i;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.LocalSession;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;
import java.util.Optional;

/**
 * Created by VcSaJen on 17.01.2016.
 */
public class WEBridge {

    private static WorldEdit WEinst;

    private static Vector3i VectorWEToSponge(Vector v)
    {
        Vector3i result = new Vector3i(v.getX(), v.getY(), v.getZ());
        return result;
    }

    private static Vector VectorSpongeToWE(Vector3i v)
    {
        Vector result = new Vector(v.getX(), v.getY(), v.getZ());
        return result;
    }

    private static LocalSession getPlayerLocalSession(Player player)
    {
        if (WEinst == null) WEinst = WorldEdit.getInstance();
        SessionManager WESessionMan = WEinst.getSessionManager();
        return WESessionMan.findByName(player.getName());
    }

    private static com.sk89q.worldedit.world.World getWorldByName(String name)
    {
        for (com.sk89q.worldedit.world.World w:
             WEinst.getPlatformManager().queryCapability(Capability.WORLD_EDITING).getWorlds())
        {
            if (w.getName().equals(name)) return w;
        }
        return null;
    }

    public static Optional<WESelection> getSelection(Player player)
    {
        LocalSession curLocSession = getPlayerLocalSession(player);
        RegionSelector regionSelector = curLocSession.getRegionSelector(curLocSession.getSelectionWorld());
        Region sel;
        Optional<WESelection> result = Optional.empty();
        try {
            sel = regionSelector.getRegion();
            result = Optional.of(new WESelection());
            result.get().minPos = VectorWEToSponge(sel.getMinimumPoint());
            result.get().maxPos = VectorWEToSponge(sel.getMaximumPoint());
            result.get().world = Sponge.getServer().getWorld(sel.getWorld().getName()).get();
        }
        catch (IncompleteRegionException e) {

        }

        return result;
        //DONE: Сделать поддержку миров
    }

    public static void setSelection(Player player, WESelection sel)
    {
        LocalSession curLocSession = getPlayerLocalSession(player);
        com.sk89q.worldedit.world.World w = getWorldByName(sel.world.getName());
        RegionSelector regionSelector = new CuboidRegionSelector(w, VectorSpongeToWE(sel.minPos), VectorSpongeToWE(sel.maxPos));
        curLocSession.setRegionSelector(w, regionSelector);
        try {
            curLocSession.getRegionSelector(w).getIncompleteRegion().shift(new Vector(0, 0, 0));
        } catch (RegionOperationException e) {}
        curLocSession.getRegionSelector(w).learnChanges();
        //curLocSession.dispatchCUISelection(WEinst.wrap(player));
        Sponge.getGame().getCommandManager().process(player, "we cui");
    }
}










