package toast.healthBars;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.network.NetworkMod;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.Configuration;

@Mod(modid = "HealthBars", name = "Health Bars", version = "1.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class _HealthBars
{
    /** TO DO **\
    >> currentTasks
        * Code mod
        * Utilize properties
    >> tasks
        * Numbers turn damaged color at 20% or less health
    >> goals
        * Compatibility
    \** ** ** **/
    /// If true, this mod starts up in debug mode.
    public static final boolean debug = false;
    
    /// Called before initialization. Loads the properties/configurations.
    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {
        debugConsole("Loading in debug mode!");
        Properties.init(new Configuration(event.getSuggestedConfigurationFile()));
    }
    
    /// Called during initialization. Registers entities, mob spawns, and renderers.
    @Mod.Init
    public void init(FMLInitializationEvent event) {
        new HealthBarHandler();
    }
    
    /// Called after initialization. Used to check for dependencies.
    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    /// Prints the message to the console with this mod's name tag.
    public static void console(String message) {
        System.out.println("[HealthBars] " + message);
    }
    
    /// Prints the message to the console with this mod's name tag.
    public static void debugConsole(String message) {
        if (debug)
            System.out.println("[HealthBars] (debug) " + message);
    }
    
    /// Prints the message to the console with this mod's name tag.
    public static void debugException(String message) {
        if (debug)
            throw new RuntimeException("[HealthBars] " + message);
    }
}