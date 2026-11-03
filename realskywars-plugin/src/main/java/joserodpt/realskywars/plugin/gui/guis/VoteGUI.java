package joserodpt.realskywars.plugin.gui.guis;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realutils.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.THING;

public class VoteGUI {

    private static Map<UUID, VoteGUI> inventories = new HashMap<>();
    private Inventory inv;
    private ItemStack close = Items.createItem(Material.OAK_DOOR, 1, "&cClose", Collections.singletonList("&fClick here to close this menu."));
    private final UUID uuid;
    private final RSWPlayer p;

    public enum VoteSetting {CHESTS, TIME, PROJECTILE}

    private VoteSetting def = VoteSetting.CHESTS;

    public VoteGUI(RSWPlayer p) {
        this.p = p;
        this.uuid = p.getUUID();
        this.inv = Bukkit.getServer().createInventory(null, 54, TranslatableLine.MENU_VOTE_TITLE.get(p));

        fillChest();
    }

    //the options of each vote, in order: option n is cast as vote value n + 1
    public static TranslatableLine[] optionLines(RSWMap.VoteType type) {
        switch (type) {
            case CHESTS:
                return new TranslatableLine[]{TranslatableLine.VOTE_CHEST_BASIC, TranslatableLine.VOTE_CHEST_NORMAL, TranslatableLine.VOTE_CHEST_EPIC};
            case TIME:
                return new TranslatableLine[]{TranslatableLine.VOTE_TIME_DAY, TranslatableLine.VOTE_TIME_SUNSET, TranslatableLine.VOTE_TIME_NIGHT, TranslatableLine.VOTE_TIME_RAIN};
            default:
                return new TranslatableLine[]{TranslatableLine.VOTE_PROJECTILE_NORMAL, TranslatableLine.VOTE_PROJECTILE_BREAK};
        }
    }

    //the permission each option needs, in the same order as optionLines
    private static String[] optionPermissions(RSWMap.VoteType type) {
        switch (type) {
            case CHESTS:
                return new String[]{"rsw.basic", "rsw.normal", "rsw.epic"};
            case TIME:
                return new String[]{"rsw.day", "rsw.sunset", "rsw.night", "rsw.rain"};
            default:
                return new String[]{"rsw.normal-projectile", "rsw.break-projectile"};
        }
    }

    /**
     * Whether votes are still taken in this player's match, telling them if not.
     */
    public static boolean canVoteNow(RSWPlayer p) {
        RSWMap match = p.getMatch();
        if (match == null || p.getState() != RSWPlayer.PlayerState.CAGE) {
            return false;
        }
        if (match.getStartMapTimer() != null && match.getStartMapTimer().getSecondsLeft() <= RSWConfig.file().getInt("Config.Vote-Before-Seconds")) {
            TranslatableLine.CANT_VOTE.send(p, true);
            return false;
        }
        return true;
    }

    /**
     * Casts a vote, used by both this inventory and the Bedrock form.
     *
     * @param value the vote value, starting at 1 for the first option
     * @return false if the player already voted on this or lacks the permission (they are told which)
     */
    public static boolean castVote(RSWPlayer p, RSWMap.VoteType type, int value) {
        TranslatableLine[] lines = optionLines(type);
        if (value < 1 || value > lines.length) {
            return false;
        }

        if (!p.isBot() && p.getMatch().hasVotedFor(type, p.getUUID())) {
            TranslatableLine.ALREADY_VOTED.send(p, true);
            return false;
        }
        if (!p.getPlayer().hasPermission(optionPermissions(type)[value - 1])) {
            TranslatableLine.CMD_NO_PERM.send(p, true);
            return false;
        }

        p.getMatch().addVote(p.getUUID(), type, value);
        p.sendMessage(TranslatableLine.CAST_VOTE.with(THING, lines[value - 1].get(p)).get(p, true));
        return true;
    }

    public void fillChest() {
        this.inv.clear();

        //selection items
        this.inv.setItem(10, Items.createItem(Material.CHEST, 1, TranslatableLine.MENU_CHESTS_TITLE.get(p)));
        this.inv.setItem(19, Items.createItem(Material.CLOCK, 1, TranslatableLine.MENU_TIME_TITLE.get(p)));
        this.inv.setItem(28, Items.createItem(Material.ARROW, 1, TranslatableLine.MENU_PROJECTILES_TITLE.get(p)));

        this.inv.setItem(37, close);

        switch (def) {
            case CHESTS:
                this.inv.setItem(13, Items.createItem(Material.WOODEN_SWORD, 1, TranslatableLine.VOTE_CHEST_BASIC.get(p)));
                this.inv.setItem(14, Items.createItem(Material.CHEST, 1, TranslatableLine.VOTE_CHEST_NORMAL.get(p)));
                this.inv.setItem(15, Items.createItem(Material.ENDER_CHEST, 1, TranslatableLine.VOTE_CHEST_EPIC.get(p)));
                break;
            case TIME:
                this.inv.setItem(22, Items.createItem(Material.YELLOW_CONCRETE, 1, TranslatableLine.VOTE_TIME_DAY.get(p)));
                this.inv.setItem(23, Items.createItem(Material.RED_CONCRETE, 1, TranslatableLine.VOTE_TIME_SUNSET.get(p)));
                this.inv.setItem(24, Items.createItem(Material.BLACK_CONCRETE, 1, TranslatableLine.VOTE_TIME_NIGHT.get(p)));
                this.inv.setItem(25, Items.createItem(Material.WATER_BUCKET, 1, TranslatableLine.VOTE_TIME_RAIN.get(p)));
                break;
            case PROJECTILE:
                this.inv.setItem(31, Items.createItem(Material.EGG, 1, TranslatableLine.VOTE_PROJECTILE_NORMAL.get(p)));
                this.inv.setItem(33, Items.createItem(Material.COBBLESTONE, 1, TranslatableLine.VOTE_PROJECTILE_BREAK.get(p)));
                break;
        }

        for (int number : new int[]{0, 1, 2, 9, 11, 18, 20, 27, 29, 36, 38, 45, 46, 47}) {
            this.inv.setItem(number, Items.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));
        }
    }

    public void openInventory(Player target) {
        Inventory inv = getInventory();
        InventoryView openInv = target.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = target.getOpenInventory().getTopInventory();
            if (!inv.equals(openTop)) {
                target.openInventory(inv);
            }
            register();
        }
    }

    //used on reload so nobody keeps clicking a GUI built from the old config
    public static void closeAll() {
        for (final VoteGUI current : new ArrayList<>(inventories.values())) {
            final Player p = Bukkit.getPlayer(current.uuid);
            if (p != null && p.getOpenInventory().getTopInventory().equals(current.getInventory())) {
                p.closeInventory();
            }
        }
        inventories.clear();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        VoteGUI current = inventories.get(uuid);
                        if (!current.getInventory().equals(e.getInventory())) {
                            return;
                        }

                        e.setCancelled(true);
                        if (e.getCurrentItem() == null) {
                            return;
                        }

                        RSWPlayer p = current.p;

                        switch (e.getRawSlot()) {
                            case 10:
                                current.def = VoteSetting.CHESTS;
                                current.fillChest();
                                break;
                            case 19:
                                current.def = VoteSetting.TIME;
                                current.fillChest();
                                break;
                            case 28:
                                current.def = VoteSetting.PROJECTILE;
                                current.fillChest();
                                break;

                            //Chest vote
                            case 13:
                            case 14:
                            case 15:
                                if (!castVote(p, RSWMap.VoteType.CHESTS, e.getRawSlot() - 12)) {
                                    p.closeInventory();
                                }
                                break;

                            //time vote
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                                if (!castVote(p, RSWMap.VoteType.TIME, e.getRawSlot() - 21)) {
                                    p.closeInventory();
                                }
                                break;

                            //projectile vote
                            case 31:
                                if (!castVote(p, RSWMap.VoteType.PROJECTILES, 1)) {
                                    p.closeInventory();
                                }
                                break;
                            case 33:
                                if (!castVote(p, RSWMap.VoteType.PROJECTILES, 2)) {
                                    p.closeInventory();
                                }
                                break;

                            case 37:
                                p.closeInventory();
                                break;
                        }
                    }
                }
            }

            @EventHandler
            public void onDrag(final InventoryDragEvent e) {
                final VoteGUI current = inventories.get(e.getWhoClicked().getUniqueId());
                //dragging over this GUI's slots would drop the dragged items into it
                if (current != null && current.getInventory().equals(e.getInventory())) {
                    e.setCancelled(true);
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
                    final VoteGUI current = inventories.get(uuid);
                    if (current != null && e.getInventory().equals(current.getInventory())) {
                        current.unregister();
                    }
                }
            }
        };
    }

    public Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}