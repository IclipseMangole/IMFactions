package de.imfactions.util;

import net.md_5.bungee.api.ChatColor;

import static de.imfactions.util.ColorConverter.toHex;

public class RainbowColor {
    public static ChatColor rainbowColor(int duration) {
        return rainbowColor(duration, 1, 10);
    }

    public static ChatColor rainbowColor(double duration, int place, int max) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        hsv += place * (((duration * 1000.0) / max) / (duration * 1000.0));
        return ChatColor.of(toHex(hsv, 1f, 1f));
    }

    public static String rainbowColor(double duration, String line, boolean bold){
        String boldString = (bold ? String.valueOf(ChatColor.BOLD) : "");
        StringBuilder coloredLine = new StringBuilder();
        for(int i = 0; i < line.length(); i++){
            coloredLine.append(rainbowColor(duration, i, line.length())).append(boldString).append(line.charAt(i));
        }
        return coloredLine.toString();
    }
}
