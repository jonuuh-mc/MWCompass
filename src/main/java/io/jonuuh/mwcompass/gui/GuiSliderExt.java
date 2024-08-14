package io.jonuuh.mwcompass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Fixes one 'bug' in GuiSlider where sliders of different heights will all have a slider pointer of height 20 because of a hard coded value
 */
public class GuiSliderExt extends GuiSlider
{
    public GuiSliderExt(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider par)
    {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                updateSlider();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int pointerX = (int)(this.sliderValue * (float)(this.width - 8));

            // Taken from drawButton in GuiButtonExt, fixes the bug
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition + pointerX, this.yPosition, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        }
    }
}