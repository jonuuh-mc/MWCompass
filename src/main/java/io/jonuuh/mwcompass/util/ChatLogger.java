package io.jonuuh.mwcompass.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;

public class ChatLogger
{
    private static final IChatComponent header = new ChatComponentText("\u00a78[\u00a77MWCompass\u00a78] ");

    public static void addLog(String log)
    {
        addLog(log, EnumChatFormatting.WHITE);
    }

    public static void addLog(String log, EnumChatFormatting color)
    {
        addLog(new ChatComponentText(log).setChatStyle(new ChatStyle().setColor(color)));
    }

    public static void addLog(String log, ChatStyle chatStyle)
    {
        addLog(new ChatComponentText(log).setChatStyle(chatStyle));
    }

    public static void addLog(IChatComponent chatComponent)
    {
        addLog(chatComponent, true);
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////

    public static void addCenteredLog(String log)
    {
        addCenteredLog(new ChatComponentText(log));
    }

    public static void addCenteredLog(String log, ChatStyle logStyle)
    {
        addCenteredLog(new ChatComponentText(log).setChatStyle(logStyle));
    }

    public static void addCenteredLog(IChatComponent chatComponent)
    {
        String padding = getPaddingToCenter(chatComponent.getUnformattedText(), ' ');
        addPaddedLog(chatComponent, new ChatComponentText(padding));
    }

    private static void addPaddedLog(IChatComponent chatComponent, IChatComponent paddingComponent)
    {
        IChatComponent paddedComponent = paddingComponent.createCopy().appendSibling(chatComponent).appendSibling(paddingComponent);
        addLog(paddedComponent, false);
    }

//    public static void addTitleLog(String title, ChatStyle titleStyle, EnumChatFormatting barColor)
//    {
//        if (!barColor.isColor())
//        {
//            System.out.println("[MWCompass] EnumChatFormatting param should be a color");
//            barColor = EnumChatFormatting.WHITE;
//        }
//
//        IChatComponent titleComponent = new ChatComponentText(title).setChatStyle(titleStyle.setStrikethrough(false));
//
//        String padding = getPaddingToCenter(title, ' ');
//        ChatStyle barStyle = new ChatStyle().setColor(barColor).setStrikethrough(true);
//        IChatComponent barComponent = new ChatComponentText(padding).setChatStyle(barStyle);
//
//        addPaddedLog(titleComponent, barComponent);
//    }
//
//    public static void addBarLog(EnumChatFormatting barColor)
//    {
//        if (!barColor.isColor())
//        {
//            System.out.println("[MWCompass] EnumChatFormatting param should be a color");
//            barColor = EnumChatFormatting.WHITE;
//        }
//
//        String padding = getPaddingToCenter("", ' ');
//        ChatStyle barStyle = new ChatStyle().setColor(barColor).setStrikethrough(true);
//
//        addLog(new ChatComponentText(padding + padding).setChatStyle(barStyle), false);
//    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////

//    public static void addStrLogs(Collection<String> logs)
//    {
//        logs.forEach(ChatLogger::addLog);
//    }
//
//    public static void addComponentLogs(Collection<IChatComponent> logs)
//    {
//        logs.forEach(ChatLogger::addLog);
//    }
//
//    public static void addCenteredStrLogs(Collection<String> logs)
//    {
//        logs.forEach(ChatLogger::addCenteredLog);
//    }
//
//    public static void addCenteredComponentLogs(Collection<IChatComponent> logs)
//    {
//        logs.forEach(ChatLogger::addCenteredLog);
//    }
//
//    public static void addFancyLogsBox(Collection<IChatComponent> content, String title, EnumChatFormatting titleColor, EnumChatFormatting barColor)
//    {
//        addTitleLog(title, new ChatStyle().setColor(titleColor), barColor);
//        content.forEach(ChatLogger::addCenteredLog);
//        addBarLog(barColor);
//    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////

    private static void addLog(IChatComponent chatComponent, boolean doHeader)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (player != null)
        {
            player.addChatMessage(doHeader ? header.createCopy().appendSibling(chatComponent) : chatComponent);
        }
    }

    private static String getPaddingToCenter(String text, Character paddingChar)
    {
        int chatWidth = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth();
        int logWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
        int paddingCharWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(String.valueOf(paddingChar));

        if (logWidth >= chatWidth)
        {
            return "";
        }

        char[] padding = new char[((chatWidth - logWidth) / paddingCharWidth) / 2];
        Arrays.fill(padding, paddingChar);
        return new String(padding);
    }
}