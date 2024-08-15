package io.jonuuh.mwcompass.event.render;

import io.jonuuh.mwcompass.util.MapData;
import io.jonuuh.mwcompass.config.Settings;
import io.jonuuh.mwcompass.util.ChatLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;

public class CompassRenderer
{
    private final Minecraft mc;
    private final Map<Character, EnumChatFormatting> colorMap;
    private final String mapName;
    private final int scale;
    private final int paddingAmt;
    private final float trackWidth;
    private final float trackHeight;
    private final Deque<RoundedRect> immutableCompass;

    private Deque<RoundedRect> lastCompass;
    private float lastYaw;
    private boolean isIngame;

    public CompassRenderer(MapData mapData)
    {
        this.mc = Minecraft.getMinecraft();
        this.colorMap = Settings.getInstance().getColorMap();
        this.mapName = mapData.toString();

        this.scale = 2;
        this.paddingAmt = ((360 / this.scale) - 4) / 4;
        this.trackWidth = 480;
        this.trackHeight = 1.0F;

        this.lastYaw = 0.0F;
        this.isIngame = true;

        this.immutableCompass = new Compass(mapData, this.paddingAmt, (this.trackHeight * 8), 1.0F);
        this.lastCompass = this.immutableCompass;

        ChatLogger.addLog("Created Compass from MapData: " + Arrays.toString(mapData.getDirs()), EnumChatFormatting.GREEN);
    }

    public boolean isIngame()
    {
        return isIngame;
    }

    public void setIngame(boolean ingame)
    {
        isIngame = ingame;
    }

    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event)
    {
        if (!isIngame() || event.type != RenderGameOverlayEvent.ElementType.BOSSHEALTH)
        {
            return;
        }

        event.setCanceled(true); // remove default boss bar

        float playerYaw = this.mc.thePlayer.rotationYaw;
        playerYaw = (playerYaw % 360 < 0) ? playerYaw % 360 + 360 : playerYaw % 360; // normalize yaw

        int centerIndex = (int) (playerYaw / this.scale);

        // total compass rendered width - total compass track rendered width, divide by 6 to get # of segments that fit on the track
        // then divide by 2 to get the # of segments per side, then add one to not overlap rounded track edge
        int clippingAmtPerSide = (((this.immutableCompass.size() * 6) - ((int) this.trackWidth)) / 6 / 2) + 1;

        Deque<RoundedRect> compass;

        if (playerYaw != lastYaw)
        {
            compass = ((ArrayDeque<RoundedRect>) this.immutableCompass).clone();

            for (int i = 0; i < centerIndex; i++)
            {
                compass.offer(compass.pollFirst());
            }

            this.lastYaw = playerYaw;
            this.lastCompass = compass;
        }
        else
        {
            compass = this.lastCompass;
        }

        drawCompass(compass, clippingAmtPerSide);
    }

    private void drawCompass(Deque<RoundedRect> compass, int clippingAmtPerSide)
    {
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        GL11.glPushMatrix();

        // Translate right
        GL11.glTranslated(scaledResolution.getScaledWidth_double() / 2.0D, 0, 0);

        // Scale compass given scaled resolution width
        double glScale = getCompassGLScale(scaledResolution.getScaledWidth_double());
        GL11.glScaled(glScale, glScale, 0);

        // Translate down
        GL11.glTranslated(0, 18, 0);

        // Disable depth testing
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Draw map title
        GL11.glPushMatrix();
        GL11.glScaled(0.8D, 0.8D, 0);
        int titleWidth = mc.fontRendererObj.getStringWidth(mapName);
        this.mc.fontRendererObj.drawString(EnumChatFormatting.GOLD + mapName + EnumChatFormatting.RESET, -(titleWidth / 2.0F), -18, -1, true);
        GL11.glPopMatrix();

        // Enable transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Draw compass track
        float trackRadius = 1;
        RoundedRect compassTrack = new RoundedRect(null, 0, 0, this.trackWidth - (trackRadius * 2) - 0, 0, trackRadius, EnumChatFormatting.GRAY, 0.75F);
        compassTrack.draw(GL11.GL_POLYGON);

        // (four sets of padding, rendered width of 6 each, + four directions, rendered width of 6 each, all div by 2)
        // + # clipped segments per side * rendered width, + half a segment to render at center
        int segmentX = (((-this.paddingAmt * 4 * 6) + (-4 * 6)) / 2) + (clippingAmtPerSide * 6) + 3;
        int i = 0;

        // Draw compass segments
        for (RoundedRect segment : compass)
        {
            // Skip drawing segments that don't overlap compass track
            if (i <= clippingAmtPerSide || i > compass.size() - clippingAmtPerSide)
            {
                i++;
                continue;
            }

            segment.setCenterX(segmentX);
            segment.draw(GL11.GL_POLYGON);
            drawSegmentLabel(segment, segmentX);
            segmentX += 6;
            i++;
        }

        // Disable transparency
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // Re-enable depth testing
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix();
    }

    private void drawSegmentLabel(RoundedRect segment, int x)
    {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        Character segmentId = segment.getId();

        if (segmentId != '|')
        {
            int charWidth = this.mc.fontRendererObj.getCharWidth(segmentId);
            this.mc.fontRendererObj.drawString(this.colorMap.get(segmentId) + String.valueOf(segmentId) + EnumChatFormatting.RESET, x - (charWidth / 2.0F), (this.trackHeight * 6), -1, true);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
    }

    private double getCompassGLScale(double scaledWidth)
    {
        float srWidth = (float) scaledWidth;
        float srWidth_min = 0.0F;
        float srWidth_max = 960.0F;
        float scale_min = 0.5F;
        float scale_max = 1.0F;

        double slope = (scale_max - scale_min) / (srWidth_max - srWidth_min);
        return scale_min + slope * (srWidth - srWidth_min);
    }
}

