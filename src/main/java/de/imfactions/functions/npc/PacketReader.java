package de.imfactions.functions.npc;

import de.imfactions.util.UUIDFetcher;
import de.imfactions.util.reflection.ReflectionException;
import de.imfactions.util.reflection.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.world.EnumHand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PacketReader {

    Channel channel;
    public static Map<UUID, Channel> channels = new HashMap<UUID, Channel>();

    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        channel = craftPlayer.getHandle().b.a.k;
        channels.put(UUIDFetcher.getUUID(player.getName()), channel);

        if (channel.pipeline().get("PacketInjector") != null) return;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) throws Exception {
                arg.add(packet);
                readPacket(player, packet);
            }
        });
    }

    public void readPacket(Player player, PacketPlayInUseEntity packet) {
        if (isInteract(packet)) {
            if (isMainHand(packet)) {
                int id = (int) getValue(packet, "a");

                for (NPC npc : NPC.npcs) {
                    if (npc.getEntityPlayer().getId() == id) {
                        npc.onInteract(player, packet.b());
                    }
                }
            }
        }
    }

    private boolean isInteract(PacketPlayInUseEntity packet) {
        try {
            int ordinal = ((Enum) ReflectionUtil.invokeMethod(getValue(packet, "b"), "a")).ordinal();
            return ordinal == 0;
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isMainHand(PacketPlayInUseEntity packet) {
        EnumHand hand = (EnumHand) getValue(getValue(packet, "b"), "a");
        return hand.ordinal() == 0;
    }

    private Object getValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void eject(Player player) {
        channel = channels.get(UUIDFetcher.getUUID(player.getName()));
        if (channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
    }

}
