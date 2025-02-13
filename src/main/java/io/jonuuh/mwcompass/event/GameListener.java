package io.jonuuh.mwcompass.event;

import io.jonuuh.mwcompass.util.MapData;
import io.jonuuh.mwcompass.event.render.CompassRenderer;
import io.jonuuh.mwcompass.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.regex.Pattern;

public class GameListener
{
    private final Minecraft mc;
    private final Pattern mapMsgPattern = Pattern.compile("^You\\sare\\scurrently\\splaying\\son\\s([A-Za-z]\\s*)+$");
//    private final Pattern pregamePattern = Pattern.compile("^\\s*Map:\\s([A-Za-z]\\s*)+$");
    private final Pattern ingamePattern = Pattern.compile("^\\s*[0-9]+\\sClass\\sPoints?\\s*$");

    private CompassRenderer compassRenderer;
//    private final KeyBinding keyBinding;
    private boolean sentMapQuery;
    private String lastIngameScore;

    public GameListener(/*KeyBinding keyBinding*/)
    {
        this.mc = Minecraft.getMinecraft();
//        this.keyBinding = keyBinding;
        this.sentMapQuery = false;
    }

//    @SubscribeEvent
//    public void onKeyInput(InputEvent.KeyInputEvent event)
//    {
//        if (keyBinding.isPressed())
//        {
////            setCompassRenderer(MapData.SHANTY_BAY);
//        }
//    }

    @SubscribeEvent
    public void onPostRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || mc.theWorld == null)
        {
            return;
        }

        Scoreboard sb = mc.theWorld.getScoreboard();

        if (sb != null && Util.getScoreboardHeader(sb).equals("MEGA WALLS"))
        {
            String possibleIngameScore = Util.getScoreboardScoreAtIndex(sb, true, 11);

            if (possibleIngameScore == null)
            {
                return;
            }

            // If score didn't change, early return (nothing happened)
            if (!possibleIngameScore.equals(lastIngameScore))
            {
                lastIngameScore = possibleIngameScore;
//                ChatLogger.addLog("lastIngameScore = " + lastIngameScore, EnumChatFormatting.GREEN);
                return;
            }

            // TODO: keep track of last to avoid making 200 matchers a second?
//            boolean isPregame = pregameScore != null && pregamePattern.matcher(pregameScore).matches();
            boolean isIngame = ingamePattern.matcher(possibleIngameScore).matches();

            // First game of the session
            if (compassRenderer == null && !sentMapQuery)
            {
                if (isIngame)
                {
//                    ChatLogger.addLog("First isIngame of session", EnumChatFormatting.GREEN);
//                    System.out.println("First isIngame of session");
                    sentMapQuery = true;
                    mc.thePlayer.sendChatMessage("/map");
//                    ChatLogger.addLog("Sent map query...", EnumChatFormatting.GREEN);
                }
            }

            // After a CompassRenderer has been created, watch for when the player leaves or rejoins the game to disable rendering
            if (compassRenderer != null)
            {
                boolean lastIngameUpdate = compassRenderer.isIngame();

                if (isIngame && !lastIngameUpdate && !sentMapQuery)
                {
                    compassRenderer.setIngame(true);
//                    ChatLogger.addLog("Rejoined -> enabled CompassRenderer", EnumChatFormatting.GREEN);

                    mc.thePlayer.sendChatMessage("/map"); // TODO: handles edge case? -> join new game then get warped back into old one?
                    sentMapQuery = true;
//                    ChatLogger.addLog("Sent map query...", EnumChatFormatting.GREEN);
                }

                if (!isIngame && lastIngameUpdate)
                {
                    compassRenderer.setIngame(false);
//                    ChatLogger.addLog("Left game -> disabled CompassRenderer", EnumChatFormatting.GREEN);
                }
            }
        }
    }

    // Create compass renderer(s) using parsed map name from /map
    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event)
    {
        String msg = event.message.getUnformattedText();

        // If waiting for a map msg and the event msg is one
        if (sentMapQuery && mapMsgPattern.matcher(msg).matches())
        {
//            event.setCanceled(true); // Don't show the /map msg
            sentMapQuery = false;
            // Parse map name
            String parsedMapName = msg.substring("You are currently playing on ".length()).trim();
            MapData mapData = MapData.getMapData(parsedMapName);

            // Set the compass renderer
            if (mapData != null)
            {
                setCompassRenderer(mapData);
            }
        }
    }

    private void setCompassRenderer(MapData mapData)
    {
        String mapName = mapData.toString();

        if (compassRenderer != null)
        {
            if (compassRenderer.getMapName().equals(mapName))
            {
                // If the previous compassRenderer CAN be reused for the current incoming map, don't create a new one
//                ChatLogger.addLog("Reusing previous CompassRenderer for: '" + mapName + "'", EnumChatFormatting.GREEN);
                return;
            }
            else
            {
                // Unregister the previous compassRenderer if it CAN'T be reused for the current incoming map
                MinecraftForge.EVENT_BUS.unregister(compassRenderer);
            }
        }

        // Create new compassRenderer
        compassRenderer = new CompassRenderer(mapData);
        MinecraftForge.EVENT_BUS.register(compassRenderer);
//        ChatLogger.addLog("Set new CompassRenderer for: '" + mapName + "'", EnumChatFormatting.GREEN);
    }
}
