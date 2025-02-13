package io.jonuuh.mwcompass.event.render;

import io.jonuuh.mwcompass.util.MapData;
import io.jonuuh.mwcompass.config.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class CompassRenderer
{
    private final Minecraft mc;
    private final Map<Character, EnumChatFormatting> colorMap;
    private final String mapName;
    private final float trackWidth;
    private final float trackHeight;
    private final Compass compass;

    private float lastYaw;
    private boolean isIngame;

    public CompassRenderer(MapData mapData)
    {
        this.mc = Minecraft.getMinecraft();
        this.colorMap = Settings.getInstance().getColorMap();
        this.mapName = mapData.toString();

        this.trackWidth = 480;
        this.trackHeight = 1.0F;

        this.lastYaw = 0.0F;
        this.isIngame = true;

        this.compass = new Compass(mapData, 1.0F, 8.0F, 1.0F);
    }

    public String getMapName()
    {
        return mapName;
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

        float playerYaw = mc.thePlayer.rotationYaw;
        float normalizedPlayerYaw = (playerYaw % 360 < 0) ? playerYaw % 360 + 360 : playerYaw % 360;

        if (playerYaw != lastYaw /*Math.abs(playerYaw - lastYaw) > 0.05F*/)
        {
            // Add 90 to yaw to move centered yaw to the side
            compass.setLeftPointer(normalizedPlayerYaw + 90);
            this.lastYaw = playerYaw;
        }

        drawCompass();
    }

    private void drawCompass()
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

        // Draw map title
        GL11.glPushMatrix();
        GL11.glScaled(0.8D, 0.8D, 0);
        int titleWidth = mc.fontRendererObj.getStringWidth(mapName);
        mc.fontRendererObj.drawString(EnumChatFormatting.GOLD + mapName + EnumChatFormatting.RESET, -(titleWidth / 2.0F), -18, -1, true);
        GL11.glPopMatrix();

        // Enable transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Draw compass track
        float trackRadius = 1;
        RoundedRect compassTrack = new RoundedRect(null, 0, 0, trackWidth - (trackRadius * 2) /*+ (4 * 2)*/, 0, trackRadius, EnumChatFormatting.GRAY, 0.75F);
        compassTrack.draw(GL11.GL_POLYGON);

        float segmentIncrement = trackWidth / compass.getWindowSize();
        float segmentX = (-trackWidth / 2.0F) + (segmentIncrement / 2.0F);
        int clippingAmount = 2;

        // Draw compass segments
        for (int i = 0; i < compass.getWindowSize(); i++, segmentX += segmentIncrement) // 0 - 359
        {
            //  1 <= 2 || 359 >= 358
            if (i < clippingAmount || i >= compass.getWindowSize() - clippingAmount)
            {
                continue;
            }

            RoundedRect segment = compass.getCompass()[(compass.getLeftPointer() + i) % compass.getCompassLength()];

            if (segment != null)
            {
                segment.setCenterX(segmentX);
                segment.draw(GL11.GL_POLYGON);

                // Draw segment label for pointers
                if (segment.getLabel() != '-')
                {
                    drawSegmentLabel(segment.getLabel(), segmentX, 0);
                }
            }
        }

        // Draw 'preview' labels
        int leftCharWidth = this.mc.fontRendererObj.getCharWidth(compass.getLeftPreviewLabel());
        int rightCharWidth = this.mc.fontRendererObj.getCharWidth(compass.getRightPreviewLabel());
        float halfSegmentSpacing = segmentIncrement / 2.0F;

        drawSegmentLabel(compass.getLeftPreviewLabel(), (-trackWidth / 2.0F) + (segmentIncrement), halfSegmentSpacing - leftCharWidth);
        drawSegmentLabel(compass.getRightPreviewLabel(), (+trackWidth / 2.0F) - (segmentIncrement), -halfSegmentSpacing + rightCharWidth);

        // Disable transparency
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }

    private void drawSegmentLabel(Character segmentId, float segmentX, float xOffset)
    {
        int charWidth = mc.fontRendererObj.getCharWidth(segmentId);
        String text = colorMap.get(segmentId) + String.valueOf(segmentId) + EnumChatFormatting.RESET;

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.fontRendererObj.drawString(text, (segmentX + xOffset) - (charWidth / 2.0F), (trackHeight * 6), -1, true);
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

