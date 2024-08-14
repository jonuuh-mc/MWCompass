package io.jonuuh.mwcompass;

public enum MapData
{
    // "NESW" -> north to west, clockwise
    ANCHORED("RYBG"),
    AZTEC("YBRG"),
    BARRAGE("YBRG"),
    BLOODMOON("YBRG"),
    CITY("GRBY"),
    DRAGONKEEP("YBRG"),
    DUSKFORGE("BYRG"),
    DYNASTY("GRBY"),
    EBONVEIL("BRGY"),
    EGYPT("RYBG"),
    FORSAKEN("BRGY"),
    GOLDFORGE("YRGB"),
    IMPERIAL("YBRG"),
    KINGDOM("GRBY"),
    KIROBIRO("BRGY"),
    LAUNCHSITE("BYGR"),
    MAD_PIXEL("YBRG"),
    OASIS("GBYR"),
    SERENITY("BRGY"),
    SERPENTS("GRBY"),
    SHADOWSTONE("BYRG"),
    SHANTY_BAY("BRGY"),
    SOLACE("BYGR"),
    STEPPES("YGRB"),
    STONEHOLD("YBRG"),
    WONDERLAND("GYBR");

    private final Character[] dirs;

    MapData(String dirs)
    {
        if (!dirs.matches("[RGBY]{4}"))
        {
            throw new IllegalArgumentException("MapData should have 4 directions");
        }

        this.dirs = new Character[]{dirs.charAt(0), dirs.charAt(1), dirs.charAt(2), dirs.charAt(3)};
    }

    public Character[] getDirs()
    {
        return dirs;
    }

    @Override
    public String toString()
    {
        String name = super.toString().toLowerCase();

        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        int i = name.indexOf('_');
        if (i != -1)
        {
            name = name.substring(0, i + 1) + name.substring(i + 1, i + 2).toUpperCase() + name.substring(i + 2);
            name = name.replace('_', ' ');
        }

        return name;
    }

    public static MapData getMapData(String mapName)
    {
        for (MapData mapData : values())
        {
            if (mapData.toString().equals(mapName))
            {
                return mapData;
            }
        }
        return null;
    }
}
