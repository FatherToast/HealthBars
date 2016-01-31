package toast.healthBars;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class HealthBarHandler implements ITickHandler
{
    /// List of all entities currently being tracked by this mod.
    private static ArrayList<EntityLiving> trackedEntities = new ArrayList<EntityLiving>(100);
    /// Stack of entities that need health bar updates.
    private static ArrayDeque<EntityLiving> updateStack = new ArrayDeque();
    
    public HealthBarHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        TickRegistry.registerTickHandler(this, Side.SERVER);
    }
    
    /// Marks the entity to be tracked for health bar updates.
    public static void track(EntityLiving entity) {
        trackedEntities.add(entity);
    }
    /// Puts the mob into the update stack.
    public static void markEntityForUpdate(EntityLiving entity) {
        updateStack.add(entity);
    }
    
    /** Called by World.spawnEntityInWorld().
        Entity entity = the entity joining the world.
        World world = the world the entity is joining.
    */
    @ForgeSubscribe(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityLiving)
            HealthBarHelper.initHealthBar((EntityLiving)event.entity);
    }
    
    /// Called at the "start" phase of a tick.
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }
    
    /// Called at the "end" phase of a tick.
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (!updateStack.isEmpty()) {
            try {
                byte limit = 10;
                EntityLiving entity;
                while (limit-- > 0) {
                    entity = updateStack.removeFirst();
                    HealthBarHelper.tryUpdateHealthBar(entity);
                }
            }
            catch (Exception ex) {
            }
        }
        EntityLiving entity;
        for (Iterator<EntityLiving> iterator = trackedEntities.iterator(); iterator.hasNext();) {
            entity = iterator.next();
            if (entity == null || entity.isDead || !HealthBarHelper.hasHealthBar(entity))
                iterator.remove();
            else
                HealthBarHelper.checkForHealthUpdate(entity);
        }
    }
    
    /// Returns the list of ticks this tick handler is interested in receiving.
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }
    
    /// Returns a profiling label for this tick manager.
    @Override
    public String getLabel() {
        return "HealthBarHandler";
    }
}