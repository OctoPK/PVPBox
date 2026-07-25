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
        DEFAULT_FOOTER = "§7§lVersion: §e" + pvpBox.getDescription().getVersion() + " §7| §7§lJoueurs connectés: §e" + pvpBox.getServer().getOnlinePlayers().size() + "§7/§e" + pvpBox.getServer().getMaxPlayers();
        updatePlayerFooter();
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

    public void setEffect(Player player, String effect) {
        List<String> list = footers.getOrDefault(player.getUniqueId(), Arrays.asList("", ""));
        list.set(0, effect);
        footers.put(player.getUniqueId(), list);
        pushTabList(player);
    }

    public void setFooter(Player player, String footer) {
        List<String> list = footers.getOrDefault(player.getUniqueId(), Arrays.asList("", ""));
        list.set(1, footer);
        footers.put(player.getUniqueId(), list);
        pushTabList(player);
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
        List<String> list = footers.getOrDefault(id, new ArrayList<>());
        StringBuilder footer = new StringBuilder();
        if (!list.isEmpty()) {
            for (String line : list) {
                footer.append(line).append("\n");
            }
            footer.delete(footer.length() - 1, footer.length());
        }
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
        setHeader(event.getPlayer(), "§6§lPVPBOX §7- §e§lWIP");
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