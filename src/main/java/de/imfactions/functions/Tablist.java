package de.imfactions.functions;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║
//  ╚══════════════════════════════════════╝

import de.imfactions.IMFactions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

import static de.imfactions.util.ColorConverter.toHex;

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

    //«»
    private String getHeader() {

        return getHeaderLine0();
    }

    private String getHeaderLine0() {
        String line = " " + factions.getData().getPurple() + " §lIM§8-" + factions.getData().getWhite() + "§lNETWORK§r§8! ";
        //Front
        line = rainbowColor(15, 11, 32) + "╣" + line;
        for (int i = 0; i < 10; i++) {
            line = rainbowColor(15, 10 - i, 32) + "═" + line;
        }
        line = rainbowColor(15, 0, 32) + "╔" + line;

        //Back
        line = line + rainbowColor(15, 21, 32) + "╠";
        for (int i = 0; i < 10; i++) {
            line = line + rainbowColor(15, 22 + i, 32) + "═";
        }
        line = line + rainbowColor(15, 32, 32) + "╗";

        return line;
    }


    private String getFooter() {
        return "";
    }


    private ChatColor rainbowColor(int duration) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        Color color = Color.getHSBColor(hsv, 1f, 1f);
        return ChatColor.of(toHex(color.getRed(), color.getGreen(), color.getBlue()));
    }

    private ChatColor rainbowColor(double duration, int place, int max) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        hsv += place * (((duration * 1000.0) / max) / (duration * 1000.0));
        return ChatColor.of(toHex(hsv, 1f, 1f));
    }
}
