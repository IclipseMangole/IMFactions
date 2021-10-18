package de.imfactions.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

import static de.imfactions.util.ColorConverter.toHex;

public class ColorUtils {


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

    public static org.bukkit.ChatColor convert(ChatColor bungeeColor){
        org.bukkit.ChatColor returnColor = org.bukkit.ChatColor.WHITE;
        double difference = -1;
        for (int i = 0; i < 16; i++) {
            org.bukkit.ChatColor value = org.bukkit.ChatColor.getByChar(Integer.toHexString(i).charAt(0));
            if(difference == -1){
                returnColor = value;
                difference = calcDifference(value, bungeeColor);
                continue;
            }
            if(difference > calcDifference(value, bungeeColor)){
                returnColor = value;
            }
        }
        return returnColor;
    }

    public static double calcDifference(org.bukkit.ChatColor bukkitChatColor, ChatColor bungeeChatColor){
        Color bukkitColor = bukkitChatColor.asBungee().getColor();
        Color bungeeColor = bungeeChatColor.getColor();
        return Math.sqrt(bungeeColor.getRed() - bukkitColor.getRed()) + Math.sqrt(bungeeColor.getGreen() - bukkitColor.getGreen()) + Math.sqrt(bungeeColor.getBlue() - bukkitColor.getBlue());
    }


}
