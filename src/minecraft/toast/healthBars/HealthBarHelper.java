package toast.healthBars;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public abstract class HealthBarHelper
{
    /// The section symbol. Used for formatting codes.
    public static final String S = "\u00a7";
    /// The valid styles.
    public static final String[] styles = {
        "percent", "hearts", "health"
    };
    
    /// Sets the entity's name.
    public static void setName(EntityLiving entity, String name) {
        entity.func_94058_c(name);
    }
    
    /// Returns whether the entity has a health bar.
    public static boolean hasHealthBar(EntityLiving entity) {
        return entity.func_94056_bM() /* If the entity has a custom name */ && entity.func_94057_bL() /* Gets the entity's custom name */ .startsWith(S + "H");
    }
    /// Returns whether the entity can get a health bar.
    public static boolean canGetHealthBar(EntityLiving entity) {
        return !entity.func_94056_bM() && !(entity instanceof EntityPlayer || entity instanceof IBossDisplayData);
    }
    
    /// Returns whether the entity has previous health data.
    public static boolean hasPreviousHealth(EntityLiving entity) {
        return entity.getEntityData().hasKey("phb");
    }
    /// Returns the entity's previous health data.
    public static int getPreviousHealth(EntityLiving entity) {
        return entity.getEntityData().getInteger("phb");
    }
    /// Updates the entity's previous health to its current health.
    public static void updatePreviousHealth(EntityLiving entity) {
        entity.getEntityData().setInteger("phb", entity.getHealth());
    }
    
    /// Builds a health bar to be applied to an entity.
    public static String buildHealthBar(EntityLiving entity) {
        String healthBar = S + "H";
        int max = Properties.getInt(Properties.GENERAL, "max_size");
        int health = Math.max(0, entity.getHealth());
        int healthMax = Math.max(0, entity.getMaxHealth());
        String style = Properties.getString(Properties.GENERAL, "style");
        if (max <= 0) {
            if (style.equalsIgnoreCase("percent")) {
                health = toPercent(health, healthMax);
                healthBar += S + getColor(health > 20) + health + "%";
            }
            else {
                boolean color = health * 5 > healthMax;
                if (style.equalsIgnoreCase("hearts")) {
                    health = (health >>> 1) + (health & 1);
                    healthMax = (healthMax >>> 1) + (healthMax & 1);
                }
                healthBar += S + getColor(color) + health;
                if (Properties.getBoolean(Properties.GENERAL, "show_max"))
                    healthBar += "/" + healthMax;
            }
        }
        else {
            String bar = Properties.getString(Properties.GENERAL, "bar");
            int length = bar.length();
            int position = 0;
            boolean percent = style.equalsIgnoreCase("percent");
            if (!percent) {
                if (style.equalsIgnoreCase("hearts")) {
                    health = (health >>> 1) + (health & 1);
                    healthMax = (healthMax >>> 1) + (healthMax & 1);
                }
                percent = healthMax > max;
            }
            if (percent) {
                health = (int)Math.ceil((double)toPercent(health, healthMax) * (double)max / 100D);
                healthMax = max - health;
                healthBar += S + getColor(true);
                while (health-- > 0) {
                    healthBar += Character.toString(bar.charAt(position));
                    if (++position == length)
                        position = 0;
                }
                if (Properties.getBoolean(Properties.GENERAL, "show_max")) {
                    healthBar += S + getColor(false);
                    while (healthMax-- > 0) {
                        healthBar += Character.toString(bar.charAt(position));
                        if (++position == length)
                            position = 0;
                    }
                }
            }
            else {
                healthMax -= health;
                healthBar += S + getColor(true);
                while (health-- > 0) {
                    healthBar += Character.toString(bar.charAt(position));
                    if (++position == length)
                        position = 0;
                }
                if (Properties.getBoolean(Properties.GENERAL, "show_max")) {
                    healthBar += S + getColor(false);
                    while (healthMax-- > 0) {
                        healthBar += Character.toString(bar.charAt(position));
                        if (++position == length)
                            position = 0;
                    }
                }
            }
        }
        return healthBar;
    }
    
    /// Returns the appropriate color.
    public static String getColor(boolean notDamaged) {
        return notDamaged ? Properties.getString(Properties.GENERAL, "color") : Properties.getString(Properties.GENERAL, "color_damaged");
    }
    
    /// Returns the values as an integer percent.
    public static int toPercent(int current, int max) {
        return (int)Math.ceil((double)current * 100D / (double)max);
    }
    
    /// Called when an entity is spawned.
    public static void initHealthBar(EntityLiving entity) {
        if (hasHealthBar(entity) || canGetHealthBar(entity)) {
            updateHealthBar(entity);
            HealthBarHandler.track(entity);
        }
    }
    
    /// Checks to see if the entity needs a health bar update.
    public static void checkForHealthUpdate(EntityLiving entity) {
        int health = Math.max(0, entity.getHealth());
        int healthPrev = -1;
        if (hasPreviousHealth(entity))
            healthPrev = getPreviousHealth(entity);
        if (health != healthPrev) {
            HealthBarHandler.markEntityForUpdate(entity);
            updatePreviousHealth(entity);
        }
    }
    
    /// Updates the entity's health bar and previous health data.
    public static void tryUpdateHealthBar(EntityLiving entity) {
        if (hasHealthBar(entity) || canGetHealthBar(entity))
            updateHealthBar(entity);
    }
    public static void updateHealthBar(EntityLiving entity) {
        setName(entity, buildHealthBar(entity));
        updatePreviousHealth(entity);
    }
}