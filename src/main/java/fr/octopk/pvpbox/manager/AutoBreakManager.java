package fr.octopk.pvpbox.manager;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.HashMap;
import java.util.Map;

public class AutoBreakManager {
    private static final int BREAK_SECONDS = 60;
    private static final HashMap<LocBlock, Integer> listeBlock = new HashMap<>();

    private static class LocBlock {
        public Location location;
        public short placedTypeId;
        public byte placedData;
        public short replaceType;
        public byte replaceData;

        public LocBlock(Location location, short placedTypeId, byte placedData, short replaceType, byte replaceData) {
            this.location = location;
            this.placedTypeId = placedTypeId;
            this.placedData = placedData;
            this.replaceType = replaceType;
            this.replaceData = replaceData;
        }
    }

    public static void addBlock(Block block, Location location, short replaceType, byte replaceData) {
        listeBlock.put(new LocBlock(location, (short) block.getTypeId(), block.getData(), replaceType, replaceData), BREAK_SECONDS);
    }

    public static void onTickAsync() {
        for (Map.Entry<LocBlock, Integer> entry : new HashMap<>(listeBlock).entrySet()) {
            LocBlock lb = entry.getKey();

            int tick = entry.getValue() - 1;

            if (tick <= 0) {
                listeBlock.remove(lb);
                replaceBlock(lb);
                updateAnim(lb, 0);
            } else {
                listeBlock.put(lb, tick);
                updateAnim(lb, tick);
            }
        }
    }

    private static void updateAnim(LocBlock lb, int tick) {
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(
                lb.location.hashCode(),
                new BlockPosition(lb.location.getBlockX(), lb.location.getBlockY(), lb.location.getBlockZ()),
                Math.min(9, Math.max(0, 10 - tick / (BREAK_SECONDS/10)))
        );
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    public static boolean contains(Location location, short type, byte data) {
        for (LocBlock lb : listeBlock.keySet()) {
            short currentType = lb.placedTypeId;
            byte replaceData = lb.placedData;
            Location loc = lb.location;
            if (loc.equals(location) && currentType == type && replaceData == data) return true;
        }
        return false;
    }

    private static void replaceBlock(LocBlock lb) {
        if (lb.placedTypeId == lb.location.getBlock().getTypeId() && lb.placedData == lb.location.getBlock().getData()) {
            lb.location.getBlock().setTypeIdAndData(lb.replaceType, lb.replaceData, false);
        }
    }

    public static void stop() {
        for(LocBlock lb : listeBlock.keySet()) {
            replaceBlock(lb);
        }
    }
}
