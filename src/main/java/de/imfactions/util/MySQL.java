package de.imfactions.util;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Created by Yannick who could get really angry if somebody steal his code!
 * ~Yannick on 08.06.2019 at 21:09 o´ clock
 */
public class MySQL {

    private File getMySQLFile() {
        return new File("plugins/" + factions.getDescription().getName(), "mysql.yml");
    }

    private FileConfiguration getMySQLFileConfiguration() {
        return YamlConfiguration.loadConfiguration(getMySQLFile());
    }

    private void setStandardMySQL() {
        FileConfiguration cfg = getMySQLFileConfiguration();
        cfg.options().copyDefaults(true);
        cfg.addDefault("host", "localhost");
        cfg.addDefault("database", "Factions");
        cfg.addDefault("user", "root");
        cfg.addDefault("password", "dshchangE762");

        cfg.addDefault("prefix", "&5" + factions.getDescription().getName() + " &3MySQL &8&7");
        try {
            cfg.save(getMySQLFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMySQL() {
        FileConfiguration cfg = getMySQLFileConfiguration();
        HOST = cfg.getString("host");
        DATABASE = cfg.getString("database");
        USER = cfg.getString("user");
        PASSWORD = cfg.getString("password");
        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("prefix")) + " ";
    }

    private IMFactions factions;

    private String HOST;
    private String DATABASE;
    private String USER;
    private String PASSWORD;
    private String prefix;
    private int disable;

    private Connection conn;
    private BukkitTask task;

    public MySQL(IMFactions factions) {
        this.factions = factions;
        setStandardMySQL();
        readMySQL();
        disable = 300;
        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                if (disable > 0) {
                    disable--;
                } else {
                    close();
                }
            }
        }, 0, 20);
    }

    public void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DATABASE + "?autoReconnect=false&useSSL=false", USER, PASSWORD);
            System.out.println(prefix + "Verbunden!");
        } catch (SQLException e) {
            System.out.println(prefix + "Keine Verbindung! Fehler: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (!conn.isClosed()) {
                if (conn != null) {
                    conn.close();
                    System.out.println(prefix + "erfolgreich getrennt!");
                }
            }

        } catch (SQLException e) {
            System.out.println(prefix + "Keine Verbindung! Fehler: " + e.getMessage());
        }
    }

    public void update(String querry) {
        Statement st;
        try {
            checkConnection();
            st = conn.createStatement();
            st.executeUpdate(querry);
            st.close();
            return;
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
    }

    public ResultSet querry(String querry) {
        ResultSet rs = null;

        Statement st;
        try {
            checkConnection();
            st = conn.createStatement();
            rs = st.executeQuery(querry);
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }

    private void checkConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            connect();
        }
        disable = 300;
    }

}
