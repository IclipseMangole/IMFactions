package de.imfactions.util;

import de.imfactions.IMFactions;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Actionbar {
    public static void online(String message, boolean prefix) {
        Bukkit.getOnlinePlayers().forEach(p -> send(p, prefix ? IMFactions.getInstance().getData().getPrefix() : "" + message));
    }

    public static void online(String message) {
        online(message, false);
    }

    public static void send(Player p, String message, boolean prefix) {
        send(p, prefix ? IMFactions.getInstance().getData().getPrefix() : "" + message);
    }

    public static void send(Player p, String message) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\" }"), ChatMessageType.GAME_INFO));
    }

}
