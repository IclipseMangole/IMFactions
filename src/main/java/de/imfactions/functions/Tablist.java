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

import java.text.DecimalFormat;

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
        line = line + rainbowColor(15, 20, 32) + "╠";
        for (int i = 0; i < 10; i++) {
            line = line + rainbowColor(15, 21 + i, 32) + "═";
        }
        line = line + rainbowColor(15, 31, 32) + "╗";

        return line;
    }


    private String getFooter() {
        return getFooterLine0() + "\n" + getFooterLine1() + "\n" + getFooterLine2() + "\n" + getFooterLine3() + "\n" + getFooterLine4();
    }

    private String getFooterLine0() {
        String line = "";
        line = line + rainbowColor(15, 0, 32) + "╠";
        for (int i = 0; i < 31; i++) {
            line = line + rainbowColor(15, 1 + i, 32) + "═";
        }
        line = line + rainbowColor(15, 31, 32) + "╣";
        return line;
    }

    private String getFooterLine1() {
        String line = "";
        line = line + rainbowColor(15, 0, 32) + "║";
        for (int i = 0; i < 70; i++) {
            line = line + " ";
        }
        line = line + rainbowColor(15, 31, 32) + "║";
        return line;
    }

    //Lobby: 07 Spieler  PvP: 10 Spieler  Plots: 08 Spieler
    private String getFooterLine2() {
        String line = "";
        line = line + rainbowColor(15, 0, 32) + "║";
        for (int i = 0; i < 18; i++) {
            line = line + " ";
        }

        DecimalFormat format = new DecimalFormat("00");
        line = line + ChatColor.of("#5813BF") + "Lobby: §e" + format.format(Bukkit.getWorld("world").getPlayers().size()) + "  " + ChatColor.of("#851818") + "PvP: §e" + format.format(Bukkit.getWorld("FactionPVP_world").getPlayers().size()) + "  " + ChatColor.of("#E07A04") + "Plots: §e" + format.format(Bukkit.getWorld("FactionPlots_world").getPlayers().size());

        for (int i = 0; i < 16; i++) {
            line = line + " ";
        }
        line = line + rainbowColor(15, 31, 32) + "║";
        return line;
    }

    private String getFooterLine3() {
        String line = "";
        line = line + rainbowColor(15, 0, 32) + "║";
        for (int i = 0; i < 70; i++) {
            line = line + " ";
        }
        line = line + rainbowColor(15, 31, 32) + "║";
        return line;
    }

    private String getFooterLine4() {
        String line = "";
        line = line + rainbowColor(15, 0, 32) + "╚";
        for (int i = 0; i < 31; i++) {
            line = line + rainbowColor(15, 1 + i, 32) + "═";
        }
        line = line + rainbowColor(15, 31, 32) + "╝";
        return line;
    }


    private ChatColor rainbowColor(int duration) {
        return rainbowColor(duration, 0, 0);
    }

    private ChatColor rainbowColor(double duration, int place, int max) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        hsv += place * (((duration * 1000.0) / max) / (duration * 1000.0));
        return ChatColor.of(toHex(hsv, 1f, 1f));
    }
}
