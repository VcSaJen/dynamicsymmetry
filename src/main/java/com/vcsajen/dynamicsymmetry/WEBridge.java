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
import com.vcsajen.dynamicsymmetry.exceptions.PlayerNotFoundException;
import com.vcsajen.dynamicsymmetry.exceptions.WorldNotFoundException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by VcSaJen on 17.01.2016.
 */
public class WEBridge {

    private static WorldEdit WEinst;

    private static Vector3i VectorWEToSponge(Vector v)
    {
        return new Vector3i(v.getX(), v.getY(), v.getZ());
    }

    private static Vector VectorSpongeToWE(Vector3i v)
    {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }

    private static LocalSession getPlayerLocalSession(Player player)
    {
        if (WEinst == null) WEinst = WorldEdit.getInstance();
        SessionManager WESessionMan = WEinst.getSessionManager();
        LocalSession result = WESessionMan.findByName(player.getName());
        if (result==null) throw new PlayerNotFoundException();
        return result;
    }

    private static Optional<com.sk89q.worldedit.world.World> getWorldByName(String name)
    {
        for (com.sk89q.worldedit.world.World w:
             WEinst.getPlatformManager().queryCapability(Capability.WORLD_EDITING).getWorlds())
        {
            if (w.getName().equals(name)) return Optional.of(w);
        }
        return Optional.empty();
    }

    /**
     * Get player's current WorldEdit selection, if present.
     * @param player player instance, must not be null.
     * @return Selection, if fully defined
     * @throws WorldNotFoundException if selection world is null, not exist, or unloaded
     */
    public static Optional<WESelection> getSelection(Player player) throws WorldNotFoundException
    {
        WESelection result;

        LocalSession curLocSession = getPlayerLocalSession(player);
        com.sk89q.worldedit.world.World w = curLocSession.getSelectionWorld();
        if (w == null) return Optional.empty();
        RegionSelector regionSelector = curLocSession.getRegionSelector(w);
        Region sel;
        if (!regionSelector.isDefined()) return Optional.empty();
        try {
            sel = regionSelector.getRegion();
            result = new WESelection();
            result.minPos = VectorWEToSponge(sel.getMinimumPoint());
            result.maxPos = VectorWEToSponge(sel.getMaximumPoint());
            com.sk89q.worldedit.world.World weWorld = sel.getWorld();
            if (weWorld==null) throw new WorldNotFoundException();
            result.world = Sponge.getServer().getWorld(weWorld.getName()).orElseThrow(WorldNotFoundException::new);
        } catch (IncompleteRegionException e) {
            return Optional.empty();
        }

        return Optional.of(result);
        //DONE: Сделать поддержку миров
    }

    /**
     * Set player's current WorldEdit selection
     * @param player player instance, must not be null.
     * @param sel fully defined selection. Will be cropped if selection is out of would bounds.
     * @throws WorldNotFoundException if selection world is null, not exist, or unloaded
     */
    public static void setSelection(Player player, WESelection sel) throws WorldNotFoundException
    {
        LocalSession curLocSession = getPlayerLocalSession(player);
        com.sk89q.worldedit.world.World w = getWorldByName(sel.world.getName()).orElseThrow(WorldNotFoundException::new);
        RegionSelector regionSelector = new CuboidRegionSelector(w, VectorSpongeToWE(sel.minPos), VectorSpongeToWE(sel.maxPos));
        curLocSession.setRegionSelector(w, regionSelector);
        try {
            curLocSession.getRegionSelector(w).getIncompleteRegion().shift(new Vector(0, 0, 0));
        } catch (RegionOperationException e) {
            return;
        }
        curLocSession.getRegionSelector(w).learnChanges();
        //curLocSession.dispatchCUISelection(WEinst.wrap(player));
        Sponge.getGame().getCommandManager().process(player, "we cui");

    }
}










