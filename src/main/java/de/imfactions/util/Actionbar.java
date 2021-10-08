package de.imfactions.util;

import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Actionbar {

    public void online(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> send(p, message));
    }


    public void send(Player p, String message) {
        ((CraftPlayer) p).getHandle().b.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\" }"), ChatMessageType.b, p.getUniqueId()));
    }

}
