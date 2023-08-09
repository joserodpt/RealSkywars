package josegamerpt.realskywars.gui.guis;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.SetupRoom;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupRoomSettings {

    private static final Map<UUID, SetupRoomSettings> inventories = new HashMap<>();
    private Inventory inv;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack confirm = Itens.createItemLore(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to confirm your settings."));
    // settings
    private final ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    private final ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    private final ItemStack ieon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &aON&7."));
    private final ItemStack ieoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    private final ItemStack rankedon = Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked Mode is turned &aON&7."));
    private final ItemStack rankedoff = Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked Mode is turned &cOFF&7."));
    private final ItemStack borderon = Itens.createItemLore(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &aON&7."));
    private final ItemStack borderoff = Itens.createItemLore(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &cOFF&7."));

    private final UUID uuid;
    private RSWPlayer p;
    private SetupRoom sr;

    public SetupRoomSettings(RSWPlayer p, SetupRoom sr) {
        this.uuid = p.getUUID();
        this.p = p;
        this.sr = sr;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(p.getSetupRoom().getName() + " Settings"));

        loadInv();
    }

    private void loadInv() {
        inv.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 19, 20, 21, 23, 24, 25, 26, 17}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(10, sr.isSpectatingON() ? specon : specoff);
        inv.setItem(12, sr.isRanked() ? rankedon : rankedoff);
        inv.setItem(14, sr.isInstantEnding() ? ieon : ieoff);
        inv.setItem(16, sr.isBorderEnabled() ? borderon : borderoff);

        inv.setItem(22, confirm);
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    if (e.getCurrentItem() == null) {
                        return;
                    }
                    Player p = (Player) clicker;
                    if (p != null) {
                        UUID uuid = p.getUniqueId();
                        if (inventories.containsKey(uuid)) {
                            SetupRoomSettings current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

                            switch (e.getRawSlot()) {
                                case 22:
                                    p.closeInventory();
                                    current.p.setSetup(current.sr);
                                    RealSkywars.getPlugin().getMapManager().continueSetup(current.p);
                                    break;

                                //Settings
                                case 10:
                                    current.sr.setSpectating(!current.sr.isSpectatingON());
                                    current.loadInv();
                                    break;
                                case 12:
                                    current.sr.setRanked(!current.sr.isRanked());
                                    current.loadInv();
                                    break;
                                case 14:
                                    current.sr.setInstantEnding(!current.sr.isInstantEnding());
                                    current.loadInv();
                                    break;
                                case 16:
                                    current.sr.setBorderEnabled(!current.sr.isBorderEnabled());
                                    current.loadInv();
                                    break;
                            }
                        }
                    }
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unregister();

                        RealSkywars.getPlugin().getMapManager().cancelSetup(RealSkywars.getPlugin().getPlayerManager().getPlayer(p));
                    }
                }
            }
        };
    }

    public void openInventory(RSWPlayer player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getPlayer().getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getPlayer().getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.getPlayer().openInventory(inv);
            }
            register();
        }
    }

    private Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
