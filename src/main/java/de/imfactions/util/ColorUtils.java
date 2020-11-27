package de.imfactions.util;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

/**
 * Created by Iclipse on 11.07.2020
 */
public class ColorUtils {
    private ColorUtils() {
    }

    public static String toHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static String toHex(float h, float s, float v) {
        Color color = Color.getHSBColor(h, s, v);
        return toHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static ChatColor brighter(ChatColor chatColor){
        Color color = Color.decode(chatColor.getName()).brighter();
        return ChatColor.of(color);
    }
}
