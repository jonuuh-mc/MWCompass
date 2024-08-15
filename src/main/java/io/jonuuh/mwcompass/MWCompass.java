package io.jonuuh.mwcompass;

import io.jonuuh.mwcompass.config.Settings;
import io.jonuuh.mwcompass.event.ConnectionListener;
import io.jonuuh.mwcompass.event.GameListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(
        modid = MWCompass.ModID,
        version = "1.0.0",
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
        KeyBinding keyBinding = new KeyBinding("<description>", Keyboard.KEY_NONE, ModID);
        ClientRegistry.registerKeyBinding(keyBinding);

        Settings.getInstance().incrementLoginsSinceLastUpdateNotification();
        Settings.getInstance().save();
//        ClientCommandHandler.instance.registerCommand(new Command());

        MinecraftForge.EVENT_BUS.register(new GameListener(keyBinding));
        MinecraftForge.EVENT_BUS.register(new ConnectionListener());
    }
}
