package fr.octopk.pvpbox.meteoroid;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class TrackedBlock {

    private final short    type;
    private final byte     data;
    private final Location location;

    public TrackedBlock(short type, byte data, Location location) {
        this.type = type;
        this.data = data;
        this.location = location;
    }

    public static TrackedBlock of(Block block) {
        return new TrackedBlock((short) block.getTypeId(), block.getData(), block.getLocation());
    }

    public short getType() {
        return this.type;
    }

    public byte getData() {
        return this.data;
    }

    public Location getLocation() {
        return this.location;
    }

    public void restore() {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.FIRE) {
            block.setTypeIdAndData(type, data, false);
        }
    }
}
