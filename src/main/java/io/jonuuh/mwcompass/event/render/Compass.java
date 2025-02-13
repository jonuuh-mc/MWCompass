package io.jonuuh.mwcompass.event.render;

import io.jonuuh.mwcompass.config.Settings;
import io.jonuuh.mwcompass.util.MapData;

class Compass
{
    private final RoundedRect[] compass;
    private final float scale;
    private final int windowSize;
    private final int compassLength;
    private final int paddingAmt;
    private final float segmentHeight;
    private final float segmentRadius;

    private int leftPointer;

    Compass(MapData mapData, float scale, float segmentHeight, float segmentRadius)
    {
        Character[] dirs = mapData.getDirs();
        this.scale = scale;
        this.compassLength = Math.round(360 * scale);

        this.compass = new RoundedRect[compassLength];
        this.windowSize = compassLength / 2;
        this.paddingAmt = (compassLength - 4) / 4;

        this.segmentHeight = segmentHeight;
        this.segmentRadius = segmentRadius;

        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35
        // ^                 ^                         ^                          ^
        //                                 |                 x                 |
        //        |                                                                  |                       --->

        // 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35
        // 1  2  3  4  5  6  7  8  ^  1  2  3  4  5  6  7  8  ^  1  2  3  4  5  6  7  8  ^  1  2  3  4  5  6  7  8  ^

        addPointerSegment(0, dirs[0]);
        addPadding(1);

        addPointerSegment(paddingAmt + 1, dirs[1]);
        addPadding(paddingAmt + 2);

        addPointerSegment((paddingAmt * 2) + 2, dirs[2]);
        addPadding((paddingAmt * 2) + 3);

        addPointerSegment((paddingAmt * 3) + 3, dirs[3]);
        addPadding((paddingAmt * 3) + 4);

//        System.out.println(Arrays.toString(compass));
//        System.out.println(compass.length);
//
//        int count = 0;
//        for (int i = 0; i < compass.length; i++)
//        {
//            RoundedRect segment = compass[i];
//
//            if (segment.getLabel() == '-')
//            {
//                count++;
//            }
//            else
//            {
//                System.out.println(count + ": " + segment.getLabel());
//            }
//        }
//        System.out.println((-1 % compassLength) + ": " + compass.length + " " + compassLength);
    }

    RoundedRect[] getCompass()
    {
        return compass;
    }

    int getLeftPointer()
    {
        return leftPointer;
    }

    void setLeftPointer(float normalizedPlayerYaw)
    {
        this.leftPointer = Math.round(normalizedPlayerYaw * scale);
//        System.out.println((this.getLeftPointer() /*+ i*/) % this.getCompassLength());
    }

    int getWindowSize()
    {
        return windowSize;
    }

    int getCompassLength()
    {
        return compassLength;
    }

    Character getLeftPreviewLabel()
    {
        // Offset to account for clipped segments
        return getPreviewLabel(leftPointer - 0, false);
    }

    Character getRightPreviewLabel()
    {
        // Offset to account for clipped segments
        return getPreviewLabel(leftPointer + windowSize - 1, true);
    }

    private Character getPreviewLabel(int start, boolean forward)
    {
        char previewLabel = '?';
        int i = start;

        while (previewLabel == '?')
        {
            RoundedRect segment = compass[i % compassLength];

            if (segment != null)
            {
                Character possibleLabel = segment.getLabel();

                if (possibleLabel != '-')
                {
                    previewLabel = possibleLabel;
                }
            }

            i = (forward ? i + 1 : i - 1);
        }

        return previewLabel;
    }

    private void addPadding(int start)
    {
        for (int i = start; i < start + paddingAmt; i++)
        {
            if (i % 10 == 0)
            {
                addTickSegment(i, '-', new Color(0.5F, 0.5F, 0.5F, 0.75F));
            }
        }
    }

    private void addPointerSegment(int index, Character label)
    {
        compass[index] = new RoundedRect(label, 0, 0, 0, segmentHeight, segmentRadius, Settings.getInstance().getColorMap().get(label), 0.75F);
    }

    private void addTickSegment(int index, Character label, Color color)
    {
        compass[index] = new RoundedRect(label, 0, 0, 0, segmentHeight, segmentRadius, color);
    }
}
