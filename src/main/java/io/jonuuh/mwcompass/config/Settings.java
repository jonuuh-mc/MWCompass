package io.jonuuh.mwcompass.config;

import io.jonuuh.mwcompass.MWCompass;
import io.jonuuh.mwcompass.util.ChatLogger;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Settings
{
    private static Settings instance;
    private final Configuration configuration;
    //    private final Map<String, Setting> settingsMap = new HashMap<>();
    private final Map<Character, EnumChatFormatting> colorMap = new HashMap<>();
    private int loginsSinceLastUpdateNotification;

    public static void createInstance(File configDir)
    {
        if (instance != null)
        {
            throw new IllegalStateException("Settings instance has already been created");
        }

        instance = new Settings(configDir);
    }

    public static Settings getInstance()
    {
        if (instance == null)
        {
            throw new NullPointerException("Settings instance has not been created");
        }

        return instance;
    }

    // Load settings (read data from Configuration, write it into settingsMap)
    private Settings(File configFile)
    {
        this.configuration = new Configuration(configFile);

        loginsSinceLastUpdateNotification = configuration.get("main", "loginsSinceLastUpdateNotification", 0).getInt();

//        settingsMap.put("settingA", new Setting(getProperty("settingA").getInt()));
//        settingsMap.put("settingB", new Setting(getProperty("settingB").getInt()));
//        settingsMap.put("settingC", new Setting(getProperty("settingC").getInt()));

        initColorMap();
    }

    public int getLoginsSinceLastUpdateNotification()
    {
        return loginsSinceLastUpdateNotification;
    }

    public void incrementLoginsSinceLastUpdateNotification()
    {
//        this.loginsSinceLastUpdateNotification %= 5;
        this.loginsSinceLastUpdateNotification++;
    }

    //    public Map<String, Setting> getSettingsMap()
//    {
//        return settingsMap;
//    }

    public Map<Character, EnumChatFormatting> getColorMap()
    {
        return colorMap;
    }

    // Save settings (read data from settingsMap, write it into Configuration)
    public void save()
    {
        configuration.get("main", "loginsSinceLastUpdateNotification", 0).setValue(loginsSinceLastUpdateNotification);

//        getProperty("settingA").setValue(settingsMap.get("settingA").getAnInt());
//        getProperty("settingB").setValue(settingsMap.get("settingB").getAnInt());
//        getProperty("settingC").setValue(settingsMap.get("settingC").getAnInt());

        if (configuration.hasChanged())
        {
            configuration.save();
            ChatLogger.addLog("Saved configuration");
        }
    }

//    private Property getProperty(String settingKey)
//    {
//        int defaultValue = 0; // default needed in case property does not exist
//        return configuration.get("<category>", settingKey, defaultValue);
//    }

    private void initColorMap()
    {
        colorMap.put('R', EnumChatFormatting.RED);
        colorMap.put('G', EnumChatFormatting.GREEN);
        colorMap.put('B', EnumChatFormatting.BLUE);
        colorMap.put('Y', EnumChatFormatting.YELLOW);
    }
}
