package fr.octopk.pvpbox.utility;

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
        public Block block;
        public Location location;
        public LocBlock(Block block, Location location) {
            this.block = block;
            this.location = location;
        }
    }

    public static void addBlock(Block block, Location location) {
        listeBlock.put(new LocBlock(block, location), BREAK_SECONDS);
    }

    public static void onTyckAsync() {
        for (Map.Entry<LocBlock, Integer> entry : new HashMap<>(listeBlock).entrySet()) {
            LocBlock lb = entry.getKey();
            Block block = lb.block;
            Material type = lb.location.getBlock().getType();
            int tick = entry.getValue() - 1;

            if (tick <= 0) {
                listeBlock.remove(lb);
                if (block.getType() == type) {
                    lb.location.getBlock().setType(Material.AIR);
                }
                updateAnim(lb, 0);
            } else {
                listeBlock.put(lb, tick);
                updateAnim(lb, tick);
            }
        }
    }

    private static void updateAnim(LocBlock lb, int tick) {
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(
                lb.block.getLocation().hashCode(),
                new BlockPosition(lb.location.getBlockX(), lb.location.getBlockY(), lb.location.getBlockZ()),
                Math.min(9, Math.max(0, 10 - tick / 6))
        );
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }
}
