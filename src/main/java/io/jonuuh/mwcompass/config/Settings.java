package io.jonuuh.mwcompass.config;

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
    private final Map<Character, EnumChatFormatting> colorMap = new HashMap<>();

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
        initColorMap();
    }

    public Map<Character, EnumChatFormatting> getColorMap()
    {
        return colorMap;
    }

    public void save()
    {
        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }

    private void initColorMap()
    {
        colorMap.put('R', EnumChatFormatting.RED);
        colorMap.put('G', EnumChatFormatting.GREEN);
        colorMap.put('B', EnumChatFormatting.BLUE);
        colorMap.put('Y', EnumChatFormatting.YELLOW);
    }
}
