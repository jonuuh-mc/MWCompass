package io.jonuuh.mwcompass.event;

import io.jonuuh.mwcompass.util.UpdateChecker;
import io.jonuuh.mwcompass.config.Settings;
import io.jonuuh.mwcompass.util.ChatLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConnectionListener
{
    private final Minecraft mc;
    private final IChatComponent openUrlChatComponent;

    public ConnectionListener()
    {
        this.mc = Minecraft.getMinecraft();

        String url = "https://github.com/jonuuh/MWCompass/releases";
        this.openUrlChatComponent = new ChatComponentText("GitHub Releases");
        ChatStyle chatStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).setColor(EnumChatFormatting.BLUE).setUnderlined(true);
        this.openUrlChatComponent.setChatStyle(chatStyle);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.entity != mc.thePlayer)
        {
            return;
        }

        ChatLogger.addLog("Client player joined the world");

        if ((Settings.getInstance().getLoginsSinceLastUpdateNotification() - 1) % 5 == 0)
        {
            if (!UpdateChecker.getInstance().getIsLatestVersion())
            {
                IChatComponent chatComponent = new ChatComponentText("New update available here: ").appendSibling(openUrlChatComponent.createCopy());
                ChatLogger.addLog(chatComponent, true);
            }
        }
    }
}
