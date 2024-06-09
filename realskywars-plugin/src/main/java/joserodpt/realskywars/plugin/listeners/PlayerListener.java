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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.cages.RSWSoloCage;
import joserodpt.realskywars.api.cages.RSWTeamCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.effects.RSWBowTrail;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.plugin.gui.GUIManager;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerProfileContentsGUI;
import joserodpt.realskywars.plugin.gui.guis.ShopGUI;
import joserodpt.realskywars.plugin.gui.guis.VoteGUI;
import joserodpt.realskywars.plugin.managers.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
            switch (gp.getState()) {
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void items(PlayerInteractEvent e) {
        switch (e.getAction()) {
            case PHYSICAL:
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
                RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(e.getPlayer());
                if (p != null && p.isInMatch()) {
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
                                    PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManager.Categories.KITS);
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
                                    ShopGUI ss = new ShopGUI(p, ShopManager.Categories.SPEC_SHOP);
                                    ss.openInventory(p);

                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                    break;
                                default:
                                    e.setCancelled(true);
                                    break;
                            }
                            break;
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
                                TranslatableLine.NO_MAP_FOUND.send(p, true);
                            }
                        }
                    }
                }


                if (rs.getLobbyManagerAPI().isInLobby(p.getLocation().getWorld())) {
                    if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
                        switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                            case BOOK:
                                e.setCancelled(true);
                                GUIManager.openPlayerMenu(p);
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
                                GUIManager.openShopMenu(p);

                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
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
        if (p != null) {
            switch (p.getState()) {
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                case CAGE:
                    event.setCancelled(true);
                    break;
            }
        }
        if (event.getBlock().getType() == Material.BEACON && p.getSetupRoom() != null) {
            Location loc = event.getBlock().getLocation().add(0.5, 0, 0.5);
            p.getSetupRoom().removeCage(loc);
            p.getPlayer().sendMessage(ChatColor.RED + "You removed this cage.");
        }
        if (event.getBlock().getType() == Material.CHEST && p.getSetupRoom() != null) {
            p.getSetupRoom().removeChest(event.getBlock().getLocation());
            p.getPlayer().sendMessage(ChatColor.RED + "You removed this chest.");
        }
        if (rs.getLobbyManagerAPI().isInLobby(event.getBlock().getWorld()) && !p.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            RSWPlayer pg = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());
            if (pg.getSetupRoom() != null) {
                if (event.getBlock().getType().equals(Material.BEACON)) {
                    switch (pg.getSetupRoom().getGameType()) {
                        case SOLO:
                            if ((pg.getSetupRoom().getCages().size() + 1) < pg.getSetupRoom().getMaxPlayers()) {
                                log(event, pg);
                            } else {
                                log(event, pg);
                                pg.getSetupRoom().confirmCages(true);
                                TranslatableLine.CAGES_SET.send(pg, true);
                            }
                            break;
                        case TEAMS:
                            if ((pg.getSetupRoom().getCages().size() + 1) < pg.getSetupRoom().getTeamCount()) {
                                log(event, pg);
                            } else {
                                log(event, pg);
                                pg.getSetupRoom().confirmCages(true);
                                TranslatableLine.CAGES_SET.send(pg, true);
                            }
                            break;
                    }
                }
                if (event.getBlock().getType() == Material.CHEST && pg.getSetupRoom() != null) {
                    String name = event.getItemInHand().getItemMeta().getDisplayName();
                    Block b = event.getBlock();
                    BlockData blockData = b.getBlockData();
                    BlockFace f = ((Directional) blockData).getFacing();
                    switch (Text.strip(name).toLowerCase()) {
                        case "common chest":
                            pg.getSetupRoom().addChest(new RSWChest(RSWChest.Type.NORMAL, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ(), f));
                            pg.sendMessage("Added Normal Chest.");
                            break;
                        case "mid chest":
                            pg.getSetupRoom().addChest(new RSWChest(RSWChest.Type.MID, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ(), f));
                            pg.sendMessage("Added Mid Chest.");
                            break;
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
    }

    public void log(BlockPlaceEvent e, RSWPlayer p) {
        Location loc = e.getBlock().getLocation().add(0.5, 0, 0.5);
        int i = p.getSetupRoom().getCages().size() + 1;
        switch (p.getSetupRoom().getGameType()) {
            case SOLO:
                RSWSoloCage c = new RSWSoloCage(i, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), 0, 64, 0);
                p.getSetupRoom().addCage(loc, c);
                break;
            case TEAMS:
                RSWTeamCage tc = new RSWTeamCage(i, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), p.getSetupRoom().getPlayersPerTeam());
                p.getSetupRoom().addCage(loc, tc);
                break;
        }
        e.getPlayer().sendMessage(ChatColor.GREEN + "You placed cage number " + i);
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
                    if (damaged.getState().name().contains("SPEC")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> {
                            damaged.getPlayer().spigot().respawn();
                            damaged.teleport(damaged.getMatch().getSpectatorLocation());
                        }, 1);
                        return;
                    }

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
                    e.setCancelled(true);
                    damaged.heal();
                    rs.getLobbyManagerAPI().tpToLobby(damaged);
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
        e.setDeathMessage(null);

        if (pkiller != null) {
            RSWPlayer killer = rs.getPlayerManagerAPI().getPlayer(pkiller);
            if (killer.isInMatch()) {
                killer.addStatistic(RSWPlayer.Statistic.KILL, 1, killer.getMatch().isRanked());
            }
            deathLoc = pkiller.getLocation();
        }

        RSWPlayer killed = rs.getPlayerManagerAPI().getPlayer(pkilled);

        if (killed.getState().name().contains("SPEC")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> {
                killed.getPlayer().spigot().respawn();
                killed.teleport(killed.getMatch().getSpectatorLocation());
            }, 1);
            return;
        }

        if (killed.isInMatch() && killed.getMatch().getState().equals(RSWMap.MapState.PLAYING)) {
            killed.addStatistic(RSWPlayer.Statistic.DEATH, 1, killed.getMatch().isRanked());

            Location finalDeathLoc = deathLoc;
            Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> {
                if (killed.getPlayer() != null) {
                    killed.getPlayer().spigot().respawn();
                }
                if (finalDeathLoc == null) {
                    if (killed.getPlayer() != null)
                        killed.getMatch().spectate(killed, RSWMap.SpectateType.INSIDE_GAME, killed.getMatch().getSpectatorLocation());
                } else {
                    if (killed.getPlayer() != null)
                        killed.getMatch().spectate(killed, RSWMap.SpectateType.INSIDE_GAME, finalDeathLoc);
                }
            }, 1);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player whoWasHit = (Player) e.getEntity();
            Player whoHit = (Player) e.getDamager();
            RSWPlayer hitter = rs.getPlayerManagerAPI().getPlayer(whoHit);
            RSWPlayer hurt = rs.getPlayerManagerAPI().getPlayer(whoWasHit);
            if (hitter.getTeam() != null && hitter.getTeam().getMembers().contains(hurt)) {
                TranslatableLine.TEAMMATE_DAMAGE_CANCEL.send(hitter, true);
                e.setCancelled(true);
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

    Map<UUID, RSWMap> fastJoin = new HashMap<>();

    @EventHandler
    public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent e) {
        // auto join random match
        if (RSWConfig.file().getBoolean("Config.Bungeecord.Enabled")) {
            Optional<RSWMap> suitableGame = rs.getMapManagerAPI().findSuitableGame(null);
            if (suitableGame.isPresent()) {
                RSWMap game = suitableGame.get();
                if (game.getState() == RSWMap.MapState.RESETTING) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_RESETTING_MESSAGE.getSingle());
                    return;
                }

                if (suitableGame.get().isFull() && !game.isSpectatorEnabled()) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_FULL.getSingle());
                } else {
                    fastJoin.put(e.getUniqueId(), suitableGame.get());
                    e.allow();
                }
            } else {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslatableLine.BUNGEECORD_NO_AVAILABLE_MAPS.getSingle());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && rs.hasNewUpdate()) {
            Text.send(e.getPlayer(), "&6&LWARNING! &r&fThere is a new update available for Real&bSkywars&f! https://www.spigotmc.org/resources/105115/");
        }

        RSWPlayer p = rs.getPlayerManagerAPI().loadPlayer(e.getPlayer());
        if (fastJoin.containsKey(e.getPlayer().getUniqueId())) {
            fastJoin.get(e.getPlayer().getUniqueId()).addPlayer(p);
            fastJoin.remove(e.getPlayer().getUniqueId());
        }
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
            if (gp.getProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES) != null && gp.isInMatch()) {
                gp.addTrail(new RSWBowTrail((Particle) gp.getProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES), e.getEntity(), gp));
            }
        }
    }
}