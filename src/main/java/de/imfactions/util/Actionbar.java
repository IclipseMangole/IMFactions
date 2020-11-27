package de.imfactions.util;

import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Actionbar {

    public void online(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> send(p, message));
    }


    public void send(Player p, String message) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\" }"), ChatMessageType.GAME_INFO, p.getUniqueId()));
    }

}
