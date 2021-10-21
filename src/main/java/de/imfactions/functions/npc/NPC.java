package de.imfactions.functions.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import static de.imfactions.util.ColorUtils.convert;

public abstract class NPC {

    public static ArrayList<NPC> npcs = new ArrayList<>();

    private final Plugin plugin;

    private EntityPlayer entityPlayer;
    private EntityArmorStand entityArmorStand;
    private EntityHorse sittingHorse;
    private String displayName;
    private String prefix;
    private ChatColor color;
    private final Property skin;
    private boolean upsideDown;
    private boolean sitting;
    private boolean nameVisible;
    private BukkitTask bukkitTask;

    public NPC(Plugin plugin, String displayName, String prefix, ChatColor color, Property skin, Location location, boolean nameVisible, boolean upsideDown) {
        npcs.add(this);
        this.plugin = plugin;
        this.displayName = displayName;
        this.prefix = prefix;
        this.color = color;
        this.skin = skin;
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), upsideDown ? "Grumm" : displayName);
        profile.getProperties().get("textures").add(skin);
        entityPlayer = new EntityPlayer(server, world, profile);
        this.upsideDown = upsideDown;
        this.sitting = false;
        this.nameVisible = nameVisible;
        setProperties(location);
    }

    private void setProperties(Location location) {
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw() / 360 * 256, location.getPitch() / 360 * 256);
        entityPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw() / 360 * 256, location.getPitch() / 360 * 256);
        entityPlayer.getBukkitEntity().setRemoveWhenFarAway(false);
        entityPlayer.getBukkitEntity().setCanPickupItems(false);
        entityPlayer.getBukkitEntity().setCustomName(displayName);
        entityPlayer.getBukkitEntity().setCustomNameVisible(!upsideDown);
    }

    public void show() {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    public void show(Player player) {
        if (((CraftPlayer) player).getHandle().getWorld() != entityPlayer.getWorld()) return;
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;

        // Show the Second Skin Layer
        DataWatcher watcher = this.entityPlayer.getDataWatcher();
        watcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 127);

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) entityPlayer.getYRot()));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.entityPlayer.getId(), (byte) entityPlayer.getYRot(), (byte) entityPlayer.getXRot(), true));
        connection.sendPacket(new PacketPlayOutEntityMetadata(this.entityPlayer.getId(), watcher, true));
        if (upsideDown) {
            enableArmorstand();
            hideEntityName();
        }
        setPrefix(prefix, color);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer)), 50);
    }

    public void remove(Player player) {
        ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer));
        if (entityArmorStand != null) {
            ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId(), entityArmorStand.getId()));
            entityArmorStand = null;
        } else {
            ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
        }
    }

    public void remove() {
        sittingHorse = null;
        Bukkit.getOnlinePlayers().forEach(this::remove);
    }


    public boolean isNameVisible() {
        return nameVisible;
    }

    public void setNameVisible(boolean visible) {
        if (this.nameVisible != visible) {
            nameVisible = visible;
            if (visible) {
                showName();
            } else {
                hideName();
            }
        }
    }

    private void showName() {
        if (upsideDown) {
            enableArmorstand();
        } else {
            showEntityName();
        }
    }

    public void hideName() {
        disableArmorstand();
        hideEntityName();
    }


    private void enableArmorstand() {
        if (entityArmorStand != null) return;
        entityArmorStand = new EntityArmorStand(entityPlayer.getWorld(), entityPlayer.locX(), entityPlayer.locY() - 2.7, entityPlayer.locZ());
        entityArmorStand.setNoGravity(true);
        entityArmorStand.setInvisible(true);
        entityArmorStand.setCustomName(new ChatComponentText(prefix + color + displayName));
        entityArmorStand.setCustomNameVisible(nameVisible);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
        PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
        broadcastPackets(packet, packet1);
    }

    private void disableArmorstand() {
        if (entityArmorStand == null) return;
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
        broadcastPackets(packet);
        entityArmorStand = null;
    }

    private void showEntityName() {
        if (entityPlayer.getCustomNameVisible()) return;
        entityPlayer.setCustomNameVisible(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            showPrefix(player);
            org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
            Team team = scoreboard.getTeam("npcHideName");
            team.removeEntry(displayName);
            /*
            Scoreboard scoreboard = ((CraftPlayer) player).getHandle().getScoreboard();
            ScoreboardTeam team = scoreboard.getTeam("packetTeam");
            if (team == null) return;
            PacketPlayOutScoreboardTeam packet = PacketPlayOutScoreboardTeam.a(team);
            ((CraftPlayer) player).getHandle().b.sendPacket(packet);
             */
        }
    }

    private void hideEntityName() {
        if (!entityPlayer.getCustomNameVisible()) return;
        entityPlayer.setCustomNameVisible(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            hidePrefix(player);
            org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
            Team team = scoreboard.getTeam("npcHideName");
            team.addEntry(displayName);


            /*
            Scoreboard scoreboard = ((CraftPlayer) player).getHandle().getScoreboard();
            ScoreboardTeam team;
            boolean created;
            if (scoreboard.getTeam("packetTeam") == null) {
                team = new ScoreboardTeam(scoreboard, "packetTeam");
                created = true;
            } else {
                team = scoreboard.getTeam("packetTeam");
                created = false;
            }
            if (!team.getPlayerNameSet().contains("Grumm")) team.getPlayerNameSet().add("Grumm");
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
            PacketPlayOutScoreboardTeam packet = PacketPlayOutScoreboardTeam.a(team, created);
            ((CraftPlayer) player).getHandle().b.sendPacket(packet);
             */
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        remove();
        GameProfile profile = new GameProfile(UUID.randomUUID(), displayName);
        profile.getProperties().get("textures").add(skin);
        Location loc = getLocation();

        entityPlayer = new EntityPlayer(entityPlayer.getMinecraftServer(), entityPlayer.getWorldServer(), profile);
        setProperties(loc);
        show();
        Bukkit.getOnlinePlayers().forEach(player -> {
            Scoreboard scoreboard = player.getScoreboard();
            String teamName = "NPC" + entityPlayer.getId();
            Team team = scoreboard.getTeam(teamName);
            team.getEntries().forEach(team::removeEntry);
            team.addEntry(displayName);
        });
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setPrefix(String prefix, ChatColor color) {
        this.prefix = prefix;
        this.color = color;
        Bukkit.getOnlinePlayers().forEach(player -> {
            Scoreboard scoreboard = player.getScoreboard();
            String teamName = "NPC" + entityPlayer.getId();
            Team team = scoreboard.getTeam(teamName) == null ? scoreboard.registerNewTeam(teamName) : scoreboard.getTeam(teamName);
            team.setPrefix(prefix);
            team.setColor(convert(color));
            if(!team.hasEntry(displayName)) {
                if (isNameVisible()) showPrefix(player);
            }
        });
    }

    private void showPrefix(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = "NPC" + entityPlayer.getId();
        Team team = scoreboard.getTeam(teamName);
        if (!team.hasEntry(displayName)) {
            team.addEntry(displayName);
        }
    }

    private void hidePrefix(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = "NPC" + entityPlayer.getId();
        Team team = scoreboard.getTeam(teamName);
        if (team.hasEntry(displayName)) {
            team.removeEntry(displayName);
        }
    }

    public void follow(Player player) {
        if (isSitting()) {
            standUp();
        }
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Vector vector = getVector(player.getEyeLocation());
                byte yaw = getYaw(getVector(player.getLocation()));
                vector.setY(0);
                if (getDistance(vector) > (5.612 * 0.05)) {
                    vector.normalize();
                    vector.multiply(5.612).multiply(0.05);
                    step(vector.getX(), vector.getY(), vector.getZ(), yaw, (byte) 0);
                }
            }
        }, 1, 1);
    }

    public void stopFollowing() {
        bukkitTask.cancel();
    }

    public void step(double x, double y, double z, byte yaw, byte pitch) {
        entityPlayer.setLocation(entityPlayer.locX() + x, entityPlayer.locY() + y, entityPlayer.locZ() + z, yaw, pitch);
        if (upsideDown) moveArmorstandRel(x, y, z);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(entityPlayer.getId(), (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), yaw, pitch, false);
        PacketPlayOutEntityHeadRotation packet1 = new PacketPlayOutEntityHeadRotation(this.entityPlayer, yaw);
        broadcastPackets(packet, packet1);
    }

    public void rotate(Location location) {
        Vector vector = getVector(location);
        byte yaw = getYaw(vector);
        byte pitch = getPitch(vector);
        entityPlayer.setYRot(yaw);
        entityPlayer.setXRot(pitch);
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), yaw, pitch, true);
        PacketPlayOutEntityHeadRotation packet1 = new PacketPlayOutEntityHeadRotation(this.entityPlayer, yaw);
        broadcastPackets(packet, packet1);
    }

    private void moveArmorstandRel(double x, double y, double z) {
        if (entityArmorStand == null) return;
        entityArmorStand.setLocation(entityArmorStand.locX() + x, entityArmorStand.locY() + y, entityArmorStand.locZ() + z, 0, 0);
        PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityArmorStand.getId(), (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), true);
        broadcastPackets(packet);
    }

    public void setUpsideDown(boolean upsideDown) {
        if (this.upsideDown == upsideDown) return;
        if (!isSitting()) {
            this.upsideDown = upsideDown;
            remove();
            GameProfile profile = new GameProfile(UUID.randomUUID(), upsideDown ? "Grumm" : displayName);
            profile.getProperties().get("textures").add(skin);
            Location loc = getLocation();

            entityPlayer = new EntityPlayer(entityPlayer.getMinecraftServer(), entityPlayer.getWorldServer(), profile);
            setProperties(loc);
            show();
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Player is sitting!");
        }
    }

    public boolean isUpsideDown() {
        return upsideDown;
    }


    public boolean isWalking() {
        return bukkitTask != null && !bukkitTask.isCancelled();
    }

    public Vector getVector(Location location) {
        return location.subtract(getLocation()).toVector();
    }

    public Location getLocation() {
        return new Location(entityPlayer.getWorld().getWorld(), entityPlayer.locX(), entityPlayer.locY(), entityPlayer.locZ());
    }

    public double getDistance(Location location) {
        return getDistance(getVector(location));
    }

    public double getDistance(Vector vector) {
        vector.setY(0);
        return vector.length();
    }

    public byte getYaw(Vector vector) {
        double x = vector.getX();
        double z = vector.getZ();
        float bukkitYaw = getBukkitYaw(x, z);
        return (byte) (bukkitYaw / 360 * 265);
    }

    public byte getPitch(Vector vector) {
        double DistanceXZ = Math.sqrt(Math.sqrt(vector.getX()) + Math.sqrt(vector.getZ()));
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + vector.getY() * vector.getY());
        double newPitch = Math.acos(vector.getY() / DistanceY) * 180 / Math.PI - 90;
        return (byte) newPitch;
    }

    public float getBukkitYaw(double x, double z) {
        if (x == 0) {
            return z > 0 ? 0 : 179;
        } else if (z == 0) {
            return x > 0 ? -90 : 90;
        }
        double degrees = Math.toDegrees(Math.atan(x / z));
        if (x < 0 && z > 0) {
            return (float) -degrees;
        }
        if (x < 0 && z < 0) {
            return (float) (180 - degrees);
        }
        if (x > 0 && z < 0) {
            return (float) (-180 - degrees);
        }
        if (x > 0 && z > 0) {
            return (float) (-1 * degrees);
        }
        throw new NumberFormatException();
    }


    public void enableRotation() {
        if (bukkitTask != null && !bukkitTask.isCancelled()) bukkitTask.cancel();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().getWorld() != getLocation().getWorld()) continue;
                if (getDistance(p.getLocation()) > 5) continue;

                rotate(p.getLocation());


            }
        }, 1, 2);
    }

    public void disableRotation() {
        bukkitTask.cancel();
    }

    public boolean isSitting() {
        return sitting;
    }

    public boolean isInSitDownAnimation() {
        return !sitting && sittingHorse != null;
    }

    public void sitDown() {
        if (!isUpsideDown()) {
            if (isSitting()) {
                standUp();
            }
            sittingHorse = new EntityHorse(EntityTypes.M, entityPlayer.getWorld());

            sittingHorse.setPositionRotation(entityPlayer.locX(), entityPlayer.locY() - 1.3, entityPlayer.locZ(), entityPlayer.getYRot(), 0);
            sittingHorse.setInvisible(true);
            entityPlayer.startRiding(sittingHorse);

            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(sittingHorse);
            PacketPlayOutEntityMetadata packet2 = new PacketPlayOutEntityMetadata(sittingHorse.getId(), sittingHorse.getDataWatcher(), true);
            PacketPlayOutEntityHeadRotation packet3 = new PacketPlayOutEntityHeadRotation(this.sittingHorse, (byte) sittingHorse.getYRot());
            PacketPlayOutMount packet4 = new PacketPlayOutMount(sittingHorse);
            broadcastPackets(packet, packet2, packet3, packet3);

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    broadcastPackets(packet4);
                    sitting = true;
                    if (upsideDown) moveArmorstandRel(0, -0.4, 0);
                }
            }, 20);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Player is upside down!");
        }
    }

    public void standUp() {
        if (!isUpsideDown()) {
            if (isSitting()) {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(sittingHorse.getId());
                broadcastPackets(packet);
                remove();
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> show(), 1);
                if (upsideDown) moveArmorstandRel(0, 0.4, 0);
                sitting = false;
            }
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Player is upside down!");
        }
    }

    public void setItemInMainHand(ItemStack item) {
        entityPlayer.getInventory().setItem(0, CraftItemStack.asNMSCopy(item));
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair(EnumItemSlot.a, CraftItemStack.asNMSCopy(item)));
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityPlayer.getId(), list);
        broadcastPackets(packet);
    }


    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public abstract void onInteract(Player player, boolean sneaking);

    private void broadcastPackets(Packet... packets) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            for (Packet packet : packets) {
                connection.sendPacket(packet);
            }
        });
    }
}