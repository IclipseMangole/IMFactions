package de.imfactions.functions.texture;

import com.mojang.authlib.properties.Property;
import de.imfactions.IMFactions;

import java.util.UUID;

public class TextureUtil {
    private final IMFactions factions;
    private final TextureTable textureTable;
    private final TextureFetcher textureFetcher;

    public TextureUtil(IMFactions factions) {
        this.factions = factions;
        textureTable = new TextureTable(factions);
        textureFetcher = new TextureFetcher(textureTable);
    }

    public Property getSkin(UUID uuid) {
        return textureFetcher.getSkin(uuid);
    }

    public Property getSkin(String url) {
        try {
            return textureFetcher.getSkin(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzNDIwMTk0MDQ2NCwKICAicHJvZmlsZUlkIiA6ICI0ZGU4MjQxNjJkZTU0MzU5YWFlMDBmMzQ1ZmMyZTY0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOYXRoYW5fS2luZyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lZDg1NWRhMmQyYTc3N2FiZjg2ZGEyNDhhZjY0OTA5YzdhMzE1NTU3MDM0NmJhMjY5ZjNlZGNlNjE2NmI1MTUyIgogICAgfQogIH0KfQ==", "u7kYPod1Taepjye/r5vRSbKs0EfiZZhW7HWoIcbJQwqv6CB5wpQBHjvxU27rsrphw4V2bIK6hKMWRAvLmXmbP8s22k3lJL5xi7zHa7nAZNyIXoDb6kUXQGvLWLgUKMIULawd4qkGHMj3eentRcZbIRANIZ1zyfwh9ofszq4SRZKJ+t0JTK39myZsQI/Hd7QQTlwOglQ1eHlVXlrWAlAeTSCaUkYcQPK8yh5AINfqfIz15V6Xp2RHI/iuPUDAm2+OlxGvoOBh8PhrrKaXy8XGLnq7DFrnhE0KqrrRZiXUR0YlrbEhfdCdO5FMbItEZpevW+GsSJUPxGqj8ggMdPNNhPrtUDxCrsmRYeTev93q/fAgbCJ82se1Lgr7m2y2yaNDcso/BML2ci0+ZzVc00LfQ3T7M0nhWcHyrAnZ9wSAq82Qsbga/6QOh8/sSmsy/TAwgAg941GqyNmYKWmpBLzaHfQCnamR5LPxvUnUks1aDXt6rrT6QliuhJ3WJTrXXmNx1A/gUdkb43DugkN+lDCMIGt8yP7Bi8FxOYebpq/S5AB25cwbaxCN8mUh95TgeupdB6bzdimQSJHV4jKoWOBEL2aJ0XvcbHw7gEvLGjIcmrvwB+90OKZkKtHATKJTHYKuSnXp69JR61gzbvLcur3hqLNSFALhWvJT7sjijNcNnqQ=");
    }
}
