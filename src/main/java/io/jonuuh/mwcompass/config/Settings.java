package io.jonuuh.mwcompass.config;

import io.jonuuh.mwcompass.util.ChatLogger;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Settings
{
    private static Settings instance;
    private final Configuration configuration;
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

    private Settings(File configFile)
    {
        this.configuration = new Configuration(configFile);

        loginsSinceLastUpdateNotification = configuration.get("main", "loginsSinceLastUpdateNotification", 0).getInt();

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

    public Map<Character, EnumChatFormatting> getColorMap()
    {
        return colorMap;
    }

    public void save()
    {
        configuration.get("main", "loginsSinceLastUpdateNotification", 0).setValue(loginsSinceLastUpdateNotification);

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
