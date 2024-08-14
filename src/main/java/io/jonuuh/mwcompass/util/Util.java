package io.jonuuh.mwcompass.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Util
{
    public static String getScoreboardHeader(Scoreboard sb)
    {
        if (sb == null || sb.getObjectiveInDisplaySlot(1) == null)
        {
            return "";
        }

        String header = sb.getObjectiveInDisplaySlot(1).getDisplayName();
        return header != null ? EnumChatFormatting.getTextWithoutFormattingCodes(header) : "";
    }

    public static List<String> getScoreboardScores(Scoreboard sb, boolean removeFormatting)
    {
        if (sb == null || sb.getObjectiveInDisplaySlot(1) == null)
        {
            return Collections.emptyList();
        }

        return new ArrayList<>(sb.getScores()).stream()
                .sorted(Comparator.comparingInt(score -> -score.getScorePoints())) // fix sidebar scores stored in reverse order
                .filter(score -> score.getObjective().getName().equals(sb.getObjectiveInDisplaySlot(1).getName())) // sidebar score name = sidebar header name?
                .map(score -> getScoreText(sb, score, removeFormatting)) // get text from each score
                .collect(Collectors.toList());
    }

    private static String getScoreText(Scoreboard sb, Score score, boolean removeFormatting)
    {
        ScorePlayerTeam spt = sb.getPlayersTeam(score.getPlayerName()); // getPlayerName: hexadecimal string? (hypixel specific)

        if (spt == null)
        {
            return "";
        }

        // If score text is <16 char, text stored in getColorPrefix; possible overflow (>16) stored in getColorSuffix (hypixel specific)
        String scoreText = spt.formatString("");
        return removeFormatting ? EnumChatFormatting.getTextWithoutFormattingCodes(scoreText) : scoreText;
    }
}
