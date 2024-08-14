package io.jonuuh.mwcompass.gui;

import io.jonuuh.mwcompass.config.Setting;
import io.jonuuh.mwcompass.config.Settings;
import io.jonuuh.mwcompass.util.ChatLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.Collections;
import java.util.Map;

public class Gui extends GuiScreen implements GuiSlider.ISlider
{
    private final Minecraft mc;
    private final Map<String, Setting> settingsMap;
    private int[] center;

    public Gui()
    {
        this.mc = Minecraft.getMinecraft();
        this.settingsMap = Settings.getInstance().getSettingsMap();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui()
    {
        center = new int[]{(this.width / 2), (this.height / 2)};

//        System.out.println(Arrays.toString(center));
        labelList.clear(); // buttonList is cleared before init is called, but labelList isn't for some reason
        GuiLabel label = new GuiLabel(mc.fontRendererObj, 0, (width / 2), (height / 2), 0, 0, -1);
        label.func_175202_a("Label");
        labelList.add(label.setCentered());

        buttonList.add(new GuiButtonExt(0, center[0] - 50, center[1] + 10, 100, 16, "<button>"));
        buttonList.add(new GuiSliderExt(1, center[0] - 50, center[1] + 30, 100, 16, "<prefix>", "<suffix>", 0, 10, 5, false, true, this));
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        Settings.getInstance().save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Tooltip handling
        for (GuiButton button : this.buttonList)
        {
            if (!button.isMouseOver())
            {
                continue;
            }

            this.drawHoveringText(Collections.singletonList("Tooltip"), mouseX, mouseY);
            return; // mouse couldn't be hovering two buttons at once
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button instanceof GuiSliderExt)
        {
            return;
        }

        ChatLogger.addLog(button.displayString);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider)
    {
        ChatLogger.addLog(String.valueOf(slider.getValueInt()));
    }
}
