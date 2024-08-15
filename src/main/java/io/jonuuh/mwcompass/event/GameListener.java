package io.jonuuh.mwcompass.event;

import io.jonuuh.mwcompass.MapData;
import io.jonuuh.mwcompass.event.render.CompassRenderer;
import io.jonuuh.mwcompass.util.ChatLogger;
import io.jonuuh.mwcompass.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.regex.Pattern;

public class GameListener
{
    private final Minecraft mc;
    private final Pattern mapMsgPattern = Pattern.compile("^You are currently playing on ([A-Za-z]\\s*)+$");
    private final Pattern ingamePattern = Pattern.compile("^\\s*[0-9]+\\sClass\\sPoints?\\s*$");
    private final KeyBinding keyBinding;

    private CompassRenderer compassRenderer;
    private int ticks = 0;
    private boolean sentMapReq;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (keyBinding.isPressed())
        {
//            setCompassRenderer("Shanty Bay");
        }
    }

    public GameListener(KeyBinding keyBinding)
    {
        this.mc = Minecraft.getMinecraft();
        this.keyBinding = keyBinding;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START || this.mc.theWorld == null)
        {
            return;
        }

        // only run everything once a second
        if (++this.ticks % 20 == 0)
        {
            this.ticks = 0;
        }
        else
        {
            return;
        }

        Scoreboard sb = this.mc.theWorld.getScoreboard();

        if (sb != null && Util.getScoreboardHeader(sb).equals("MEGA WALLS") /*&& !this.mc.thePlayer.capabilities.allowFlying*/)
        {
            for (String cleanScore : Util.getScoreboardScores(sb, true))
            {
                if (cleanScore.contains("Gates Open:") && !this.sentMapReq)
                {
                    this.mc.thePlayer.sendChatMessage("/map");
                    this.sentMapReq = true;
                }
            }
        }
    }

    // After a CompassRenderer has been created, watch when the player leaves or rejoins the game,
    // so we can disable rendering the compass while not ingame
    @SubscribeEvent
    public void onPostRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || this.mc.theWorld == null)
        {
            return;
        }

        Scoreboard sb = this.mc.theWorld.getScoreboard();

        if (sb != null && this.compassRenderer != null && Util.getScoreboardHeader(sb).equals("MEGA WALLS"))
        {
            List<String> cleanScores = Util.getScoreboardScores(sb, true);

            if (cleanScores.size() < 12)
            {
                return;
            }

            String cleanScore = cleanScores.get(11);

            // Actual current ingame status
            boolean isActuallyIngame = ingamePattern.matcher(cleanScore).matches();

            // What the renderer thinks the current ingame status is (last update)
            boolean compassRendererIngame = this.compassRenderer.isIngame();

            // If actually ingame but renderer thinks we're not, update it
            if (isActuallyIngame && !compassRendererIngame)
            {
                this.compassRenderer.setIngame(true);
                ChatLogger.addLog("Rejoined -> enabled CompassRenderer", EnumChatFormatting.GREEN);
            }

            // If actually not ingame but renderer thinks we are, update it
            if (!isActuallyIngame && compassRendererIngame)
            {
                this.compassRenderer.setIngame(false);
                ChatLogger.addLog("Left game -> disabled CompassRenderer", EnumChatFormatting.GREEN);
            }
        }
    }

    // Parse /map messages and create compass renderer
    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event)
    {
        String msg = event.message.getUnformattedText();

        // If waiting for a map msg and the event msg is one
        if (this.sentMapReq && mapMsgPattern.matcher(msg).matches())
        {
            event.setCanceled(true); // Don't show the /map res
            this.sentMapReq = false;

            // Set the compass renderer
            String parsedMapName = msg.substring("You are currently playing on ".length()).trim();
            setCompassRenderer(parsedMapName);
        }
    }

    private void setCompassRenderer(String parsedMapName)
    {
        MapData mapData = MapData.getMapData(parsedMapName);

        // Unregister old compassRenderer
        if (this.compassRenderer != null)
        {
            MinecraftForge.EVENT_BUS.unregister(this.compassRenderer);
        }

        // Create new compassRenderer
        if (mapData != null)
        {
            this.compassRenderer = new CompassRenderer(mapData);
            MinecraftForge.EVENT_BUS.register(this.compassRenderer);
            ChatLogger.addLog("Set new CompassRenderer for: '" + parsedMapName + "'", EnumChatFormatting.GREEN);
            return;
        }

        ChatLogger.addLog("Failed to set new CompassRenderer for: '" + parsedMapName + "'", EnumChatFormatting.RED);
    }
}
