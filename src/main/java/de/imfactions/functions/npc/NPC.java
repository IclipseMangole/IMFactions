package de.imfactions.functions.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerInteractManager;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import static de.imfactions.functions.npc.NPCManager.factions;

public abstract class NPC {

    public static ArrayList<NPC> npcs = new ArrayList<>();

    private EntityPlayer entityPlayer;
    private PlayerInteractManager playerInteractManager;
    private EntityArmorStand entityArmorStand;
    private EntityHorse sittingHorse;
    private String displayName;
    private Property skin;
    private boolean upsideDown;
    private boolean nameVisible;
    private BukkitTask bukkitTask;

    public NPC(String displayName, Property skin, Location location, boolean nameVisible, boolean upsideDown) {
        npcs.add(this);
        this.displayName = displayName;
        this.skin = skin;
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), upsideDown ? "Grumm" : displayName);
        profile.getProperties().get("textures").add(skin);
        entityPlayer = new EntityPlayer(server, world, profile);
        playerInteractManager = new PlayerInteractManager(entityPlayer);
        this.upsideDown = upsideDown;
        this.nameVisible = nameVisible;

        setProperties(location);

        //Displayname
        entityArmorStand = new EntityArmorStand(world, location.getX(), upsideDown ? (location.getY() - 2.7) : location.getY(), location.getZ());
        entityArmorStand.setNoGravity(true);
        entityArmorStand.setInvisible(true);
        entityArmorStand.setCustomName(new ChatComponentText(displayName));
        entityArmorStand.setCustomNameVisible(nameVisible);
    }

    private void setProperties(Location location) {
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw() / 360 * 256, location.getPitch() / 360 * 256);
        entityPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw() / 360 * 256, location.getPitch() / 360 * 256);
        entityPlayer.getBukkitEntity().setRemoveWhenFarAway(false);
        entityPlayer.getBukkitEntity().setCanPickupItems(false);
        entityPlayer.getBukkitEntity().setCustomName("");
        entityPlayer.getBukkitEntity().setCustomNameVisible(false);
    }

    public void show() {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    public void show(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;

        // Show the Second Skin Layer
        DataWatcher watcher = this.entityPlayer.getDataWatcher();
        watcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 127);

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) entityPlayer.getYRot()));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.entityPlayer.getId(), (byte) entityPlayer.getYRot(), (byte) entityPlayer.getXRot(), true));
        connection.sendPacket(new PacketPlayOutEntityMetadata(this.entityPlayer.getId(), watcher, true));
        hideEntityName(player);
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entityArmorStand));
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true));
    }

    public void hideEntityName(Player player) {
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
        if (!team.getPlayerNameSet().contains(entityPlayer.getName())) team.getPlayerNameSet().add(displayName);
        if (!team.getPlayerNameSet().contains("Grumm")) team.getPlayerNameSet().add("Grumm");
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
        PacketPlayOutScoreboardTeam packet = PacketPlayOutScoreboardTeam.a(team, created);
        ((CraftPlayer) player).getHandle().b.sendPacket(packet);
    }

    public void remove() {
        sittingHorse = null;
        broadcastPackets(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer), new PacketPlayOutEntityDestroy(entityPlayer.getId(), entityArmorStand.getId()));
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

    public void setNameVisible(boolean visible) {
        if (this.nameVisible != visible) {
            nameVisible = visible;
            entityArmorStand.setCustomNameVisible(visible);
            broadcastPackets(new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true));
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        entityArmorStand.setCustomName(IChatBaseComponent.a(displayName));
        broadcastPackets(new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true));
    }

    public void follow(Player player) {
        if (isSitting()) {
            standUp();
        }
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
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
        moveArmorstandRel(x, y, z);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(entityPlayer.getId(), (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), yaw, pitch, false);
        PacketPlayOutEntityHeadRotation packet1 = new PacketPlayOutEntityHeadRotation(this.entityPlayer, yaw);
        broadcastPackets(packet, packet1);
    }

    public void rotate(Location location) {
        byte yaw = getYaw(getVector(location));
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), yaw, (byte) 0, false);
        PacketPlayOutEntityHeadRotation packet1 = new PacketPlayOutEntityHeadRotation(this.entityPlayer, yaw);
        broadcastPackets(packet, packet1);
    }

    private void moveArmorstandRel(double x, double y, double z) {
        entityArmorStand.setLocation(entityArmorStand.locX() + x, entityArmorStand.locY() + y, entityArmorStand.locZ() + z, 0, 0);
        PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityArmorStand.getId(), (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), true);
        broadcastPackets(packet);
    }

    public void setUpsideDown(boolean upsideDown) {
        if (!isSitting()) {
            this.upsideDown = upsideDown;
            remove();
            entityArmorStand.setLocation(entityPlayer.locX(), upsideDown ? (entityPlayer.locY() - 2.7) : entityPlayer.locY(), entityPlayer.locZ(), 0.0f, 0.0f);
            GameProfile profile = new GameProfile(UUID.randomUUID(), upsideDown ? "Grumm" : displayName);
            profile.getProperties().get("textures").add(skin);
            Location loc = getLocation();

            entityPlayer = new EntityPlayer(entityPlayer.getMinecraftServer(), entityPlayer.getWorldServer(), profile);
            setProperties(loc);
            show();
            Bukkit.getScheduler().runTaskLaterAsynchronously(factions, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> hideEntityName(player));
                }
            }, 10);
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
        if(bukkitTask != null && !bukkitTask.isCancelled()) bukkitTask.cancel();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(factions, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {

                if (getDistance(p.getLocation()) > 5) continue;


                rotate(p.getLocation());


            }
        }, 1, 10);
    }

    public void disableRotation() {
        bukkitTask.cancel();
    }

    public boolean isSitting() {
        return sittingHorse != null;
    }

    public void sitDown() {
        if (!isUpsideDown()) {
            if (isSitting()) {
                standUp();
            }
            sittingHorse = new EntityHorse(EntityTypes.M, entityPlayer.getWorld());

            sittingHorse.setPositionRotation(entityPlayer.locX(), entityPlayer.locY() - 1.2, entityPlayer.locZ(), entityPlayer.getYRot(), 0);
            sittingHorse.setInvisible(true);
            entityPlayer.startRiding(sittingHorse);

            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(sittingHorse);
            PacketPlayOutEntityMetadata packet2 = new PacketPlayOutEntityMetadata(sittingHorse.getId(), sittingHorse.getDataWatcher(), true);
            PacketPlayOutEntityHeadRotation packet3 = new PacketPlayOutEntityHeadRotation(this.sittingHorse, (byte) sittingHorse.getYRot());
            PacketPlayOutMount packet4 = new PacketPlayOutMount(sittingHorse);
            broadcastPackets(packet, packet2, packet3, packet3);

            Bukkit.getScheduler().runTaskLaterAsynchronously(factions, new Runnable() {
                @Override
                public void run() {
                    broadcastPackets(packet4);
                }
            }, 20);

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

    public void standUp() {
        if (!isUpsideDown()) {
            if (isSitting()) {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(sittingHorse.getId());
                PacketPlayOutEntity.PacketPlayOutRelEntityMove packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityPlayer.getId(), (short) 0, (short) 1, (short) 0, true);
                broadcastPackets(packet, packet1);
                sittingHorse = null;
            }
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Player is upside down!");
        }
    }


    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public abstract void onInteract(Player player);

    private void broadcastPackets(Packet... packets) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            for (Packet packet : packets) {
                connection.sendPacket(packet);
            }
        });
    }
}
