package io.jonuuh.mwcompass.event.render;

import io.jonuuh.mwcompass.util.MapData;
import io.jonuuh.mwcompass.config.Settings;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayDeque;
import java.util.Map;

class Compass extends ArrayDeque<RoundedRect>
{
    Compass(MapData mapData, int paddingAmt, float segmentHeight, float segmentRadius)
    {
        Character[] dirs = mapData.getDirs();
        Map<Character, EnumChatFormatting> colorMap = Settings.getInstance().getColorMap();

        this.offer(new RoundedRect(dirs[0], 0, 0, 0, segmentHeight, segmentRadius, colorMap.get(dirs[0]), 0.75F));
        offerPadding(paddingAmt, segmentHeight, segmentRadius);

        this.offer(new RoundedRect(dirs[1], 0, 0, 0, segmentHeight, segmentRadius, colorMap.get(dirs[1]), 0.75F));
        offerPadding(paddingAmt, segmentHeight, segmentRadius);

        this.offer(new RoundedRect(dirs[2], 0, 0, 0, segmentHeight, segmentRadius, colorMap.get(dirs[2]), 0.75F));
        offerPadding(paddingAmt, segmentHeight, segmentRadius);

        this.offer(new RoundedRect(dirs[3], 0, 0, 0, segmentHeight, segmentRadius, colorMap.get(dirs[3]), 0.75F));
        offerPadding(paddingAmt, segmentHeight, segmentRadius);
    }

    private void offerPadding(int paddingAmt, float height, float radius)
    {
        float rbg = 0.50F;
        float opacity;

        for (int i = 0; i < paddingAmt; i++)
        {
            opacity = ((i + 1) % 5 != 0) ? 0.00F : 0.75F;
            this.offer(new RoundedRect('|', 0, 0, 0, height, radius, new Color(rbg, rbg, rbg, opacity)));
        }
    }
}
