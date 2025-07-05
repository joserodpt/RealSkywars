package joserodpt.realskywars.plugin.listeners;

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

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.effects.RSWBowTrail;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.plugin.gui.GUIManager;
import joserodpt.realskywars.plugin.gui.guis.MapSettingsGUI;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerItemsGUI;
import joserodpt.realskywars.plugin.gui.guis.ShopGUI;
import joserodpt.realskywars.plugin.gui.guis.VoteGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerListener implements Listener {
    private final RealSkywarsAPI rs;

    public PlayerListener(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    //block commands
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        RSWPlayer rsw = rs.getPlayerManagerAPI().getPlayer(player);
        if (rsw != null && rsw.getPlayer() != null && rsw.isInMatch() && !player.isOp()) {
            String command = event.getMessage();
            boolean block = true;
            for (String s : RSWConfig.file().getStringList("Config.Allowed-Commands")) {
                if (command.startsWith("/" + s)) {
                    block = false;
                    break;
                }
            }
            if (block) {
                TranslatableLine.BLOCKED_COMMAND.send(rsw, true);
                Debugger.print(PlayerListener.class, "blocked " + command + " for " + event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    //special items handling
    @EventHandler
    public void pegar(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            RSWPlayer gp = rs.getPlayerManagerAPI().getPlayer((Player) e.getEntity());
            if (gp == null) {
                return;
            }
            switch (gp.getState()) {
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent e) {
        RSWPlayer gp = rs.getPlayerManagerAPI().getPlayer(e.getPlayer());
        if (gp == null) {
            return;
        }
        switch (gp.getState()) {
            case SPECTATOR:
            case EXTERNAL_SPECTATOR:
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickItems(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) e.getWhoClicked());
            if (p != null) {
                switch (p.getState()) {
                    case SPECTATOR:
                    case EXTERNAL_SPECTATOR:
                    case CAGE:
                        e.setCancelled(true);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void items(PlayerInteractEvent e) {
        RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(e.getPlayer());
        if (p == null) {
            return;
        }
        switch (e.getAction()) {
            case PHYSICAL:
                if (e.getClickedBlock().getType() == Material.FARMLAND && (p.getState() == RSWPlayer.PlayerState.SPECTATOR || p.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR)) {
                    e.setCancelled(true);
                    break;
                }
                if (rs.getLobbyManagerAPI().getLobbyLocation() != null && e.getPlayer().getWorld().equals(rs.getLobbyManagerAPI().getLobbyLocation().getWorld())) {
                    switch (e.getClickedBlock().getType()) {
                        case STONE_PRESSURE_PLATE:
                            e.getPlayer().performCommand("rsw play SOLO");
                            break;
                        case HEAVY_WEIGHTED_PRESSURE_PLATE:
                            e.getPlayer().performCommand("rsw play TEAMS");
                            break;
                    }
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (p != null) {
                    if (p.getPlayer() != null && p.getPlayer().isOp()) {
                        if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPARATOR) {
                            RSWMap map = rs.getMapManagerAPI().getMap(p.getPlayer().getWorld());
                            if (map != null && map.isUnregistered()) {
                                MapSettingsGUI m = new MapSettingsGUI(p, map);
                                m.openInventory(p);
                                return;
                            }
                        } else if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.CHEST_MINECART) {
                            p.getPlayer().performCommand("rsw finish");
                            return;
                        }
                    }
                    if (p.isInMatch()) {
                        switch (p.getState()) {
                            case PLAYING:
                                //fill chests
                                if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Chest) {
                                    RSWChest chest = p.getMatch().getChest(e.getClickedBlock().getLocation());
                                    if (chest != null) {
                                        chest.populate();
                                        if (chest.isOpened()) {
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> chest.startTasks(p.getMatch()), 1);
                                        }
                                    }
                                }
                                if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                                    e.setCancelled(true);
                                    rs.getPlayerManagerAPI().trackPlayer(p);
                                }
                                break;
                            case CAGE:
                                switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                                    case BOW:
                                        e.setCancelled(true);
                                        PlayerItemsGUI v = new PlayerItemsGUI(p, RSWBuyableItem.ItemCategory.KIT);
                                        v.openInventory(p);
                                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                        break;
                                    case MINECART:
                                        e.setCancelled(true);
                                        p.getMatch().removePlayer(p);
                                        break;
                                    case HOPPER:
                                        e.setCancelled(true);

                                        if (p.getMatch().getStartMapTimer() != null) {
                                            if (p.getMatch().getStartMapTimer().getSecondsLeft() > RSWConfig.file().getInt("Config.Vote-Before-Seconds")) {
                                                VoteGUI vg = new VoteGUI(p);
                                                vg.openInventory(p.getPlayer());
                                            } else {
                                                TranslatableLine.CANT_VOTE.send(p, true);
                                            }
                                        } else {
                                            VoteGUI vg = new VoteGUI(p);
                                            vg.openInventory(p.getPlayer());
                                        }

                                        break;
                                }
                                break;
                            case SPECTATOR:
                            case EXTERNAL_SPECTATOR:
                                switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                                    case TOTEM_OF_UNDYING:
                                        e.setCancelled(true);
                                        e.getPlayer().performCommand("rsw play " + p.getMatch().getGameMode().name());
                                        break;
                                    case MAP:
                                        e.setCancelled(true);
                                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                        GUIManager.openSpectate(p);
                                        break;
                                    case MINECART:
                                        e.setCancelled(true);
                                        p.getMatch().removePlayer(p);
                                        break;
                                    case EMERALD:
                                        e.setCancelled(true);
                                        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Spectator-Shop")) {
                                            ShopGUI ss = new ShopGUI(p, RSWBuyableItem.ItemCategory.SPEC_SHOP);
                                            ss.openInventory(p);
                                            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                        }
                                        break;
                                    default:
                                        e.setCancelled(true);
                                        break;
                                }
                                break;
                        }
                    }
                }

                //sign join
                if (e.getClickedBlock() != null) {
                    if (e.getClickedBlock().getState() instanceof Sign) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        if (Text.strip(sign.getLine(0)).equals(Text.strip(rs.getLanguageManagerAPI().getPrefix()))) {
                            String mapName = Text.strip(sign.getLine(1));
                            RSWMap game = rs.getMapManagerAPI().getMap(mapName);

                            if (game != null) {
                                if (e.getPlayer().isSneaking() && (e.getPlayer().isOp() || e.getPlayer().hasPermission("rs.admin"))) {
                                    //remove sign
                                    game.removeSign(e.getClickedBlock());
                                } else {
                                    game.addPlayer(p);
                                }
                            } else {
                                TranslatableLine.CMD_NO_MAP_FOUND.send(p, true);
                            }
                        }
                    }
                }


                if (rs.getLobbyManagerAPI().isInLobby(p.getLocation().getWorld())) {
                    if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
                        switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                            case BOOK:
                                e.setCancelled(true);
                                GUIManager.openPlayerProfile(p);
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                break;
                            case NETHER_STAR:
                                e.setCancelled(true);
                                MapsListGUI v = new MapsListGUI(p);
                                v.openInventory(p);
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                break;
                            case EMERALD:
                                e.setCancelled(true);
                                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Shop")) {
                                    ShopGUI ss = new ShopGUI(p, RSWBuyableItem.ItemCategory.CAGE_BLOCK);
                                    ss.openInventory(p);
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }

                                break;
                        }
                    }
                }
                break;
        }
    }

    //player block interactions

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());

        if (event.getPlayer().isOp()) {
            if (event.getBlock().getType() == Material.BEACON) {
                Location loc = event.getBlock().getLocation();

                RSWMap mp = rs.getMapManagerAPI().getMap(event.getBlock().getLocation().getWorld());
                if (mp != null && mp.isUnregistered()) {
                    mp.removeCage(loc);
                    Text.send(event.getPlayer(), rs.getLanguageManagerAPI().getPrefix() + "&cYou removed this cage.");
                    return;
                }
            }

            if (event.getBlock().getType() == Material.CHEST) {
                Location loc = event.getBlock().getLocation();

                RSWMap mp = rs.getMapManagerAPI().getMap(event.getBlock().getLocation().getWorld());
                if (mp != null && mp.isUnregistered()) {
                    mp.removeChest(loc);
                    Text.send(event.getPlayer(), rs.getLanguageManagerAPI().getPrefix() + "&cYou removed this chest.");
                    return;
                }
            }
        }

        if (p != null) {
            switch (p.getState()) {
                case PLAYING:
                    if (event.getBlock().getType() == Material.CHEST) {
                        RSWChest chest = p.getMatch().getChest(event.getBlock().getLocation());
                        if (chest != null) {
                            if (chest.isOpened()) {
                                chest.clearHologram();
                                chest.cancelTasks();
                            } else {
                                chest.populate();
                            }
                        }
                    }
                    break;
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                case CAGE:
                    event.setCancelled(true);
                case LOBBY_OR_NOGAME:
                    if (rs.getLobbyManagerAPI().isInLobby(p.getLocation().getWorld()) && !p.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        RSWPlayer pg = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());

        if (event.getPlayer().isOp()) {
            if (event.getBlock().getType().equals(Material.BEACON)) {
                RSWMap mp = rs.getMapManagerAPI().getMap(event.getBlock().getLocation().getWorld());
                if (mp != null && mp.isUnregistered()) {

                    switch (mp.getGameMode()) {
                        case SOLO:
                            if ((mp.getCages().size() + 1) > mp.getMaxPlayers()) {
                                pg.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "&cYou can't place more cages than the max players.");
                                return;
                            }
                            break;
                        case TEAMS:
                            if ((mp.getCages().size() + 1) > mp.getMaxTeamsNumber()) {
                                pg.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "&cYou can't place more cages than the max teams.");
                                return;
                            }
                            break;
                    }

                    mp.addCage(event.getBlock().getLocation());
                    pg.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "&aYou placed a new cage.");
                    return;
                }
            }

            if (event.getBlock().getType() == Material.CHEST) {
                RSWMap mp2 = rs.getMapManagerAPI().getMap(event.getBlock().getLocation().getWorld());
                if (mp2 != null && mp2.isUnregistered()) {

                    if (event.getPlayer().getInventory().getHeldItemSlot() == RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest1")) {
                        mp2.addChest(event.getBlock(), RSWChest.Type.NORMAL);
                        pg.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "Added Normal Chest.");
                    } else if (event.getPlayer().getInventory().getHeldItemSlot() == RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest2")) {
                        mp2.addChest(event.getBlock(), RSWChest.Type.MID);
                        pg.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "Added Mid Chest.");
                    }

                    return;
                }
            }
        }

        switch (pg.getState()) {
            case SPECTATOR:
            case EXTERNAL_SPECTATOR:
            case CAGE:
                event.setCancelled(true);
                break;
        }
    }

    //player join, leave, drop item, player damage, on kill, on hit, on shoot

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        RSWPlayer rswPlayer = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());
        if (rswPlayer != null && rswPlayer.getState() != RSWPlayer.PlayerState.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            RSWPlayer damaged = rs.getPlayerManagerAPI().getPlayer(p);
            if (damaged == null) {
                return;
            }

            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (damaged.isInMatch()) {
                    e.setDamage(0);
                    if (checkSpectate(damaged)) return;

                    if (damaged.getMatch().getState() == RSWMap.MapState.PLAYING) {
                        damaged.addStatistic(RSWPlayer.Statistic.DEATH, 1, damaged.getMatch().isRanked());

                        Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> {
                            damaged.getPlayer().spigot().respawn();
                            damaged.getMatch().spectate(damaged, RSWMap.SpectateType.INSIDE_GAME, damaged.getMatch().getSpectatorLocation());
                        }, 1);
                    } else {
                        damaged.teleport(damaged.getMatch().getSpectatorLocation());
                    }
                } else {
                    if (!RSWConfig.file().getBoolean("Config.Disable-Lobby-Void-Teleport", false)) {
                        if (rs.getLobbyManagerAPI().isInLobby(damaged.getLocation().getWorld())) {
                            e.setCancelled(true);
                            damaged.heal();
                            rs.getLobbyManagerAPI().tpToLobby(damaged);
                        }
                    }
                }
            } else {
                if (damaged.isInvencible() || rs.getLobbyManagerAPI().isInLobby(damaged.getLocation().getWorld())) {
                    e.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Player pkilled = e.getEntity();
        Player pkiller = e.getEntity().getKiller();

        Location deathLoc = null;
        String msg = e.getDeathMessage();
        e.setDeathMessage(null);

        if (pkiller != null) {
            RSWPlayer killer = rs.getPlayerManagerAPI().getPlayer(pkiller);
            if (killer.isInMatch()) {
                killer.addStatistic(RSWPlayer.Statistic.KILL, 1, killer.getMatch().isRanked());
            }
            deathLoc = pkiller.getLocation();
        }

        RSWPlayer killed = rs.getPlayerManagerAPI().getPlayer(pkilled);
        if (killed == null && killed.isInMatch()) {
            killed.getMatch().getPlayers().forEach(rswPlayer -> rswPlayer.sendMessage(msg));
        }

        if (checkSpectate(killed)) return;

        if (killed.isInMatch() && killed.getMatch().getState().equals(RSWMap.MapState.PLAYING)) {
            killed.addStatistic(RSWPlayer.Statistic.DEATH, 1, killed.getMatch().isRanked());

            Location finalDeathLoc = deathLoc;
            Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> {
                if (killed.getPlayer() == null) {
                    return;
                }
                killed.getPlayer().spigot().respawn();

                killed.getMatch().spectate(killed, RSWMap.SpectateType.INSIDE_GAME,
                        finalDeathLoc == null ? killed.getMatch().getSpectatorLocation() : finalDeathLoc);
            }, 1);
        }
    }

    private boolean checkSpectate(RSWPlayer killed) {
        if (killed.getState() == RSWPlayer.PlayerState.SPECTATOR || killed.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> {
                killed.getPlayer().spigot().respawn();
                killed.teleport(killed.getMatch().getSpectatorLocation());
            }, 1);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            RSWPlayer hitter = rs.getPlayerManagerAPI().getPlayer((Player) e.getDamager());

            if (e.getEntity() instanceof Player) {
                Player whoWasHit = (Player) e.getEntity();
                RSWPlayer hurt = rs.getPlayerManagerAPI().getPlayer(whoWasHit);
                if (hitter.getTeam() != null && hitter.getTeam().getMembers().contains(hurt)) {
                    TranslatableLine.TEAMMATE_DAMAGE_CANCEL.send(hitter, true);
                    e.setCancelled(true);
                }

            }

            if (hitter.getState() == RSWPlayer.PlayerState.SPECTATOR || hitter.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            if (RSWConfig.file().getBoolean("Config.Right-Click-Player-Info")) {
                RSWPlayer click = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());
                RSWPlayer clicked = rs.getPlayerManagerAPI().getPlayer((Player) event.getRightClicked());
                if (click != null && clicked != null && !clicked.isInMatch()) {
                    PlayerGUI playg = new PlayerGUI(click, click.getUUID(), clicked);
                    playg.openInventory(click);
                }
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent e) {
        // auto join random match
        if (RSWConfig.file().getBoolean("Config.Bungeecord.Enabled")) {
            if (rs.getMapManagerAPI().getMaps(MapManagerAPI.MapGamemodes.ALL).isEmpty()) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_NO_AVAILABLE_MAPS.getSingle());
                return;
            }

            Optional<RSWMap> map = rs.getMapManagerAPI().getMaps(MapManagerAPI.MapGamemodes.ALL).stream().findFirst();
            if (map.isPresent()) {
                RSWMap game = map.get();
                if (game.getState() == RSWMap.MapState.RESETTING) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_RESETTING_MESSAGE.getSingle());
                    return;
                }

                if (game.isFull() && !game.isSpectatorEnabled()) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_FULL.getSingle());
                } else {
                    rs.getPlayerManagerAPI().getFastJoin().put(e.getUniqueId(), game);
                    e.allow();
                }
            } else {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_NO_AVAILABLE_MAPS.getSingle());
            }
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        if (RSWConfig.file().getBoolean("Config.Enable-Chat-Per-Map", false)) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(e.getPlayer());
            if (p != null) {
                if (p.isInMatch()) {
                    e.getRecipients().clear();
                    for (RSWPlayer gp : p.getMatch().getAllPlayers()) {
                        e.getRecipients().add(gp.getPlayer());
                    }
                } else {
                    e.getRecipients().clear();
                    for (Player gp : Bukkit.getOnlinePlayers()) {
                        e.getRecipients().add(gp);
                    }
                    for (RSWPlayer player : rs.getPlayerManagerAPI().getPlayers()) {
                        if (player.isInMatch()) {
                            e.getRecipients().remove(player.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && rs.hasNewUpdate()) {
            Text.send(e.getPlayer(), "&6&LWARNING! &r&fThere is a new update available for Real&bSkywars&f! https://www.spigotmc.org/resources/105115/");
        }

        rs.getPlayerManagerAPI().loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(e.getPlayer());
        if (p != null) {
            p.leave();
        }
    }

    @EventHandler
    public void onPlayerShootArrow(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() != null && e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof Arrow) {
            Player p = (Player) e.getEntity().getShooter();
            RSWPlayer gp = rs.getPlayerManagerAPI().getPlayer(p);
            assert gp != null;
            if (gp.getBowParticle() != null && gp.isInMatch()) {
                gp.addTrail(new RSWBowTrail(gp.getBowParticle(), e.getEntity(), gp));
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            RSWPlayer gp = rs.getPlayerManagerAPI().getPlayer(p);
            if (gp != null && gp.isInMatch() && gp.getState() == RSWPlayer.PlayerState.CAGE) {
                e.setFoodLevel(20);
                e.setCancelled(true);
            }
        }
    }
}