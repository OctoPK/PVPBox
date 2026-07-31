package fr.octopk.pvpbox.meteoroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class MeteorExplosion {

    private final World   world;
    private final float   power;
    private final double  x;
    private final double  y;
    private final double  z;
    private final boolean createFire;

    public MeteorExplosion(World world, float power, double x, double y, double z, boolean createFire) {
        this.world = world;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createFire = createFire;
    }

    public void explode(BlockConsumer onExplode) {
        List<Block> blocksToDestroy = getBlocksToDestroy();
        destroyBlocks(blocksToDestroy, onExplode);

        if (this.createFire) {
            createFire(blocksToDestroy);
        }
    }

    private float getBlastResistance(int x, int y, int z) {
        return ((CraftWorld) this.world).getHandle().getType(
                new BlockPosition(
                    x,
                    y,
                    z
                ))
            .getBlock().a((Entity) null);
    }

    private List<Block> getBlocksToDestroy() {
        Set<Block> set = new HashSet<>();
        int i = 16;

        Random random = ThreadLocalRandom.current();
        for (int x = 0; x < i; x++) {
            for (int y = 0; y < i; y++) {
                for (int z = 0; z < i; z++) {
                    if (x != 0 && x != 15 && y != 0 && y != 15 && z != 0 && z != 15) {
                        continue;
                    }

                    double d = x / 15f * 2f - 1f;
                    double e = y / 15f * 2f - 1f;
                    double f = z / 15f * 2f - 1f;
                    double g = Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    float h = this.power * (0.7f + random.nextFloat() * 0.6f);
                    double m = this.x;
                    double n = this.y;
                    double o = this.z;

                    for (float p = 0.3f; h > 0.0f; h -= 0.225f) {
                        int posX = (int) Math.floor(m);
                        int posY = (int) Math.floor(n);
                        int posZ = (int) Math.floor(o);
                        if (posY >= 256 || posY < 0) {
                            break;
                        }

                        h -= (this.getBlastResistance(posX, posY, posZ) + 0.3f) * 0.3f;
                        if (h > 0f) {
                            set.add(this.world.getBlockAt(posX, posY, posZ));
                        }

                        m += d * p;
                        n += e * p;
                        o += f * p;
                    }
                }
            }
        }

        return new ArrayList<>(set);
    }

    private void destroyBlocks(List<Block> blocks, BlockConsumer onExplode) {
        Collections.shuffle(blocks, ThreadLocalRandom.current());

        for (Block block : blocks) {
            onExplode.accept(block);
        }
    }

    private void createFire(List<Block> blocks) {
        Random random = ThreadLocalRandom.current();

        for (Block block : blocks) {
            if (random.nextInt(3) == 0 && block.isEmpty() && block.getRelative(BlockFace.DOWN).getType().isSolid()) {
                block.setType(Material.FIRE);
            }
        }
    }

    @FunctionalInterface
    public interface BlockConsumer {

        void accept(Block block);
    }
}
