package toast.healthBars;

import java.util.HashMap;
import java.util.Random;
import net.minecraftforge.common.Configuration;

/**
    This helper class automatically creates, stores, and retrieves properties.
    Supported data types:
        String, boolean, int, double
    
    Any property can be retrieved as an Object or String.
    Any non-String property can also be retrieved as any other non-String property.
    Retrieving a number as a boolean will produce a randomized output depending on the value.
*/
public abstract class Properties
{
    /// Mapping of all properties in the mod to their values.
    private static final HashMap<String, Object> map = new HashMap();
    /// Common category names.
        public static final String GENERAL = "_general";
        
    /// Initializes these properties.
    public static void init(Configuration config) {
        config.load();
        String color = "a";
        String colorDamaged = "4";
        
        add(config, GENERAL, "bar", "|", "The string that makes up health bars.");
        add(config, GENERAL, "color", color, "The color code (0-f) for health bars.\n(See http://www.minecraftwiki.net/wiki/File:Colors.png)");
        add(config, GENERAL, "color_damaged", colorDamaged, "The color code (0-f) for damaged health bars.");
        add(config, GENERAL, "max_size", 20, "The maximum health bar length. If this is 0, a number will be displayed instead of a health bar.");
        add(config, GENERAL, "show_max", true, "If this is true, the max health will be shown.");
        add(config, GENERAL, "style", HealthBarHelper.styles[0], "How the health bar shows information. Valid styles: " + buildStylesList());
        
        config.addCustomCategoryComment(GENERAL, "General and/or miscellaneous options.");
        validateBar();
        validateColorCodes(color, colorDamaged);
        validateStyle();
        config.save();
    }
    
    /// Passes to the mod.
    public static void debugException(String message) {
        _HealthBars.debugException(message);
    }
    
    /// Ensures that the color codes are actually color codes.
    public static void validateBar() {
        if (getString(GENERAL, "bar") == "")
            map.put(GENERAL + "@bar", "|");
    }
    
    /// Ensures that the color codes are actually color codes.
    public static void validateColorCodes(String defaultColor, String defaultColorDamaged) {
        String color = getString(GENERAL, "color");
        String colorDamaged = getString(GENERAL, "color_damaged");
        int colorCode;
        try {
            colorCode = Integer.parseInt(color, 16);
            if (colorCode < 0 || colorCode > 0xf)
                Integer.parseInt(null);
        }
        catch (Exception ex) {
            map.put(GENERAL + "@color", defaultColor);
        }
        try {
            colorCode = Integer.parseInt(colorDamaged, 16);
            if (colorCode < 0 || colorCode > 0xf)
                Integer.parseInt(null);
        }
        catch (Exception ex) {
            map.put(GENERAL + "@color_damaged", defaultColorDamaged);
        }
    }
    
    /// Ensures that the color codes are actually color codes.
    public static void validateStyle() {
        String style = getString(GENERAL, "style");
        for (String validStyle : HealthBarHelper.styles)
            if (style.equalsIgnoreCase(validStyle))
                return;
        map.put(GENERAL + "@style", HealthBarHelper.styles[0]);
    }
    
    /// Ensures that the color codes are actually color codes.
    public static String buildStylesList() {
        String list = "";
        for (int i = 0; i < HealthBarHelper.styles.length; i++) {
            list += HealthBarHelper.styles[i] + ", ";
        }
        return list.substring(0, list.length() - 2);
    }
    
    /// Loads the property as the specified value.
    public static void add(Configuration config, String category, String field, String defaultValue, String comment) {
        map.put(category + "@" + field, config.get(category, field, defaultValue, comment).getString());
    }
    public static void add(Configuration config, String category, String field, int defaultValue, String comment) {
        map.put(category + "@" + field, Integer.valueOf(config.get(category, field, defaultValue, comment).getInt(defaultValue)));
    }
    public static void add(Configuration config, String category, String field, boolean defaultValue, String comment) {
        map.put(category + "@" + field, Boolean.valueOf(config.get(category, field, defaultValue, comment).getBoolean(defaultValue)));
    }
    public static void add(Configuration config, String category, String field, double defaultValue, String comment) {
        map.put(category + "@" + field, Double.valueOf(config.get(category, field, defaultValue, comment).getDouble(defaultValue)));
    }
    
    /// Gets the Object property.
    public static Object getProperty(String category, String field) {
        return map.get(category + "@" + field);
    }
    
    /// Gets the value of the property (instead of an Object representing it).
    public static String getString(String category, String field) {
        return getProperty(category, field).toString();
    }
    public static boolean getBoolean(String category, String field) {
        Object property = getProperty(category, field);
        if (property instanceof Boolean)
            return ((Boolean)property).booleanValue();
        debugException("Tried to get boolean for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return false;
    }
    public static int getInt(String category, String field) {
        Object property = getProperty(category, field);
        if (property instanceof Number)
            return ((Number)property).intValue();
        if (property instanceof Boolean)
            return ((Boolean)property).booleanValue() ? 1 : 0;
        debugException("Tried to get int for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0;
    }
    public static double getDouble(String category, String field) {
        Object property = getProperty(category, field);
        if (property instanceof Number)
            return ((Number)property).doubleValue();
        if (property instanceof Boolean)
            return ((Boolean)property).booleanValue() ? 1.0 : 0.0;
        debugException("Tried to get double for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0.0;
    }
}