package de.imfactions.util;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import java.awt.*;

/**
 * Created by Iclipse on 11.07.2020
 */
public class ColorConverter {
    private ColorConverter() {
    }

    public static String toHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static String toHex(float h, float s, float v) {
        Color color = Color.getHSBColor(h, s, v);
        return toHex(color.getRed(), color.getGreen(), color.getBlue());
    }
}
