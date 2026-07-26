package fr.octopk.pvpbox.manager;

import fr.octopk.pvpbox.PVPBox;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TabManager implements Listener {

    private final PVPBox pvpBox;

    private static TabManager instance;
    private static String DEFAULT_FOOTER;

    private final Map<UUID, String> headers = new ConcurrentHashMap<>();
    private final Map<UUID, List<String>> footers = new ConcurrentHashMap<>();

    private TabManager(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
        pvpBox.getServer().getPluginManager().registerEvents(this, pvpBox);
        updateFooter();
    }

    private void updateFooter() {
        updateFooter(pvpBox.getServer().getOnlinePlayers().size());
    }

    private void updateFooter(int nbOnlinePlayers) {
        DEFAULT_FOOTER = "§7§lVersion: §e" + pvpBox.getDescription().getVersion() + " §7| §7§lJoueurs connectés: §e" + nbOnlinePlayers + "§7/§e" + pvpBox.getServer().getMaxPlayers();
        updatePlayerFooter();
    }

    public static TabManager getInstance(PVPBox pvpBox) {
        if (instance == null) {
            instance = new TabManager(pvpBox);
        }
        return instance;
    }

    public void setFooter(Player player, List<String> footer) {
        footers.put(player.getUniqueId(), footer);
        pushTabList(player);
    }

    public void setSpeed(Player player, int speed) {
        List<String> list = footers.getOrDefault(player.getUniqueId(), Arrays.asList("", "", "", ""));
        list.set(0, "§e§l" + speed + " ⚡⚡§r");
        footers.put(player.getUniqueId(), list);
        pushTabList(player);
    }

    public void setStrenght(Player player, int strenght) {
        List<String> list = footers.getOrDefault(player.getUniqueId(), Arrays.asList("", "", "", ""));
        list.set(1, "§c§l" + strenght + " ⚔§r");
        footers.put(player.getUniqueId(), list);
        pushTabList(player);
    }

    public void setResistance(Player player, int resistance) {
        List<String> list = footers.getOrDefault(player.getUniqueId(), Arrays.asList("", "", "", ""));
        list.set(2, "§9§l" + resistance + " ♦§r");
        footers.put(player.getUniqueId(), list);
        pushTabList(player);
    }

    public void setFooter(Player player, String footer) {
        List<String> list = footers.getOrDefault(player.getUniqueId(), Arrays.asList("", "", "", ""));
        list.set(3, footer);
        footers.put(player.getUniqueId(), list);
        pushTabList(player);
    }

    public void clearEffect(Player player) {
        setFooter(player, Arrays.asList("", "", "", DEFAULT_FOOTER));
    }

    public void setHeaderFooter(Player player, String header, List<String> footer) {
        headers.put(player.getUniqueId(), header);
        footers.put(player.getUniqueId(), footer);
        pushTabList(player);
    }

    public void setHeader(Player player, String header) {
        headers.put(player.getUniqueId(), header);
        pushTabList(player);
    }

    public void clearTabList(Player player) {
        UUID id = player.getUniqueId();
        headers.remove(id);
        footers.remove(id);
        sendHeaderFooterPacket(player, "", "");
    }

    private void pushTabList(Player player) {
        UUID id = player.getUniqueId();
        List<String> list = footers.getOrDefault(id, Arrays.asList("", "", "", ""));
        StringBuilder footer = new StringBuilder();
        if (list.get(0).isEmpty()) {
            footer.append("\n").append(list.get(3));
        } else footer.append("\n").append(list.get(0)).append("    ").append(list.get(1)).append("    ").append(list.get(2)).append("\n\n").append(list.get(3));
        sendHeaderFooterPacket(player, headers.getOrDefault(id, ""), footer.toString());
    }

    private void sendHeaderFooterPacket(Player player, String header, String footer) {
        try {
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            setPrivateField(packet, "a", toChatComponent(header));
            setPrivateField(packet, "b", toChatComponent(footer));

            sendPacket(player, packet);
        } catch (ReflectiveOperationException e) {
            pvpBox.getLogger().warning("Failed to send tab header/footer packet to player " + player.getName() + ": " + e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        headers.remove(id);
        footers.remove(id);
        updateFooter(event.getPlayer().getServer().getOnlinePlayers().size() - 1);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateFooter();
        UUID id = event.getPlayer().getUniqueId();
        setHeader(event.getPlayer(), "\n§6§lPVPBOX §7- §e§lWIP\n");
        setFooter(event.getPlayer(), DEFAULT_FOOTER);
    }

    private void updatePlayerFooter() {
        footers.forEach((uuid, footer) -> {
            Player player = pvpBox.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                setFooter(player, DEFAULT_FOOTER);
            }
        });
    }

    public void shutdown() {
        headers.clear();
        footers.clear();
    }

    private IChatBaseComponent toChatComponent(String text) {
        String escaped = text == null ? "" : text.replace("\"", "\\\"");
        String translated = ChatColor.translateAlternateColorCodes('&', escaped);

        return ChatSerializer.a("{\"text\":\"" +translated.replace("\u00A7", "\\u00A7") + "\"}");
    }

    private void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>)packet);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}