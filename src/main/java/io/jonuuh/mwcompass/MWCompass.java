package io.jonuuh.mwcompass;

import io.jonuuh.mwcompass.config.Settings;
import io.jonuuh.mwcompass.event.ConnectionListener;
import io.jonuuh.mwcompass.event.GameListener;
import io.jonuuh.mwcompass.util.UpdateChecker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = MWCompass.ModID,
        version = MWCompass.version,
        acceptedMinecraftVersions = "[1.8.9]"
)
public class MWCompass
{
    public static final String ModID = "mwcompass";
    public static final String version = "1.0.0";

    @Mod.EventHandler
    public void FMLPreInit(FMLPreInitializationEvent event)
    {
        Settings.createInstance(event.getSuggestedConfigurationFile());
        UpdateChecker.createInstance();
    }

    @Mod.EventHandler
    public void FMLInit(FMLInitializationEvent event)
    {
        Settings.getInstance().incrementLoginsSinceLastUpdateNotification();
        Settings.getInstance().save();

        MinecraftForge.EVENT_BUS.register(new GameListener());
        MinecraftForge.EVENT_BUS.register(new ConnectionListener());
    }
}
