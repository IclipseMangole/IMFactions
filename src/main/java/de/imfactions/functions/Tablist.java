package de.imfactions.functions;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)__   ____  __   ║
//  ║    /  // __/ /  // // )\ / ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                          =
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              =
//  ╚══════════════════════════════════════╝

import de.imfactions.IMFactions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

/**
 * Created by Iclipse on 10.07.2020
 */
public class Tablist {

    private IMFactions factions;

    public Tablist(IMFactions factions) {
        this.factions = factions;

        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    setTablist(player);
                });
            }
        }, 0, 1);
    }

    private void setTablist(Player player) {
        player.setPlayerListHeader(getHeader());
        player.setPlayerListFooter(getFooter());
    }

    private String getHeader() {
        String line0 = "§8----------«" + ChatColor.of(rainbowColor(15)) + " §lIM§8-§f§lNETWORK§r§8 »----------";
        //String line1 = dsp.get("tablist.header1", p, p.getName());
        //String line2 = dsp.get("tablist.header2", p, IMAPI.getServerName());

        return line0;
    }

    private String getFooter() {
        String line1 = "";
        for (int i = 0; i < 70; i++) {
            line1 += ChatColor.of(rainbowColor(15, i, 70)) + "▮";
        }
        //String line0 = dsp.get("tablist.footer1", p);
        //String line1 = dsp.get("tablist.footer2", p);
        //String line2 = dsp.get("tablist.footer3", p);
        return line1;
    }

    private String toHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }


    private String rainbowColor(int duration) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        Color color = Color.getHSBColor(hsv, 1f, 1f);
        return toHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    private String rainbowColor(double duration, int place, int max) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        hsv += place * (((duration * 1000.0) / max) / (duration * 1000.0));
        Color color = Color.getHSBColor(hsv, 1f, 1f);
        return toHex(color.getRed(), color.getGreen(), color.getBlue());
    }
}
