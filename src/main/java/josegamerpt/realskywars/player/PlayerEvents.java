package josegamerpt.realskywars.player;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.SoloCage;
import josegamerpt.realskywars.cages.TeamCage;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.effects.BowTrail;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.GUIManager;
import josegamerpt.realskywars.gui.guis.MapsViewer;
import josegamerpt.realskywars.gui.guis.PlayerGUI;
import josegamerpt.realskywars.gui.guis.ProfileContent;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;

public class PlayerEvents implements Listener {

    //block commands
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        RSWPlayer rsw = RealSkywars.getPlayerManager().getPlayer(player);
        if (rsw != null && rsw.getPlayer() != null && rsw.isInMatch() && !player.isOp()) {
            String command = event.getMessage();
            boolean block = true;
            for (String s : Config.file().getStringList("Config.Allowed-Commands")) {
                if (command.startsWith("/" + s)) {
                    block = false;
                    break;
                }
            }
            if (block) {
                rsw.sendMessage(RealSkywars.getLanguageManager().getString(rsw, LanguageManager.TS.BLOCKED_COMMAND, true));
                Debugger.print(PlayerEvents.class, "blocked " + command + " for " + event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    //special items handling
    @EventHandler
    public void pegar(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            RSWPlayer gp = RealSkywars.getPlayerManager().getPlayer((Player) e.getEntity());
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
                if (RealSkywars.getGameManager().getLobbyLocation() != null && e.getPlayer().getWorld().equals(RealSkywars.getGameManager().getLobbyLocation().getWorld())) {
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
                RSWPlayer gp = RealSkywars.getPlayerManager().getPlayer(e.getPlayer());
                if (RealSkywars.getGameManager().isInLobby(gp.getLocation().getWorld())) {
                    if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {

                        switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                            case BOOK:
                                GUIManager.openPlayerMenu(gp);
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                e.setCancelled(true);
                                break;
                            case NETHER_STAR:
                                MapsViewer v = new MapsViewer(gp, gp.getMapViewerPref(), RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAPS_NAME, false));
                                v.openInventory(gp);
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                e.setCancelled(true);
                                break;
                            case EMERALD:
                                GUIManager.openShopMenu(gp);
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                e.setCancelled(true);
                                break;
                        }
                    }
                }

                if (gp.isInMatch()) {
                    switch (gp.getState()) {
                        case PLAYING:
                            //fill chests
                            if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Chest) {
                                SWChest chest = gp.getMatch().getChest(e.getClickedBlock().getLocation());
                                if (chest != null) {
                                    chest.populate();
                                    if (chest.isOpened()) {
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> chest.startTasks(gp.getMatch()), 1);
                                    }
                                }
                            }
                            if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                                e.setCancelled(true);
                                RealSkywars.getPlayerManager().trackPlayer(gp);
                            }
                            break;
                        case CAGE:
                            switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                                case BOW:
                                    e.setCancelled(true);
                                    ProfileContent v = new ProfileContent(gp, ShopManager.Categories.KITS);
                                    v.openInventory(gp);
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                    break;
                                case MINECART:
                                    e.setCancelled(true);
                                    gp.getMatch().removePlayer(gp);
                                    break;
                                case HOPPER:
                                    e.setCancelled(true);

                                    if (gp.getMatch().getStartRoomTimer() != null) {
                                        if (gp.getMatch().getStartRoomTimer().getSecondsLeft() > Config.file().getInt("Config.Vote-Before-Seconds")) {
                                            GUIManager.openVote(gp);
                                        } else {
                                            gp.sendMessage(RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.CANT_VOTE, true));
                                        }
                                    } else {
                                        GUIManager.openVote(gp);
                                    }

                                    break;
                            }
                            break;
                        case SPECTATOR:
                        case EXTERNAL_SPECTATOR:
                            switch (e.getPlayer().getInventory().getItemInMainHand().getType()) {
                                case TOTEM_OF_UNDYING:
                                    e.setCancelled(true);
                                    e.getPlayer().performCommand("rsw play " + gp.getMatch().getGameMode().name());
                                    break;
                                case MAP:
                                    e.setCancelled(true);
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                    GUIManager.openSpectate(gp);
                                    break;
                                case MINECART:
                                    e.setCancelled(true);
                                    gp.getMatch().removePlayer(gp);
                                    break;
                                default:
                                    e.setCancelled(true);
                                    break;
                            }
                            break;
                    }
                }
                break;
        }
    }

    //player block interactions

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        RSWPlayer p = RealSkywars.getPlayerManager().getPlayer(event.getPlayer());
        if (p != null) {
            switch (p.getState()) {
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                case CAGE:
                    event.setCancelled(true);
                    break;
            }
        }
        if (RealSkywars.getGameManager().isInLobby(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            RSWPlayer pg = RealSkywars.getPlayerManager().getPlayer(event.getPlayer());
            if (pg.getSetup() != null) {
                if (event.getBlock().getType().equals(Material.BEACON)) {
                    switch (pg.getSetup().getGameType()) {
                        case SOLO:
                            if ((pg.getSetup().getCages().size() + 1) < pg.getSetup().getMaxPlayers()) {
                                log(event, pg);
                            } else {
                                log(event, pg);
                                pg.getSetup().confirmCages(true);
                                pg.sendMessage(RealSkywars.getLanguageManager().getString(pg, LanguageManager.TS.CAGES_SET, false));
                            }
                            break;
                        case TEAMS:
                            if ((pg.getSetup().getCages().size() + 1) < pg.getSetup().getTeamCount()) {
                                log(event, pg);
                            } else {
                                log(event, pg);
                                pg.getSetup().confirmCages(true);
                                pg.sendMessage(RealSkywars.getLanguageManager().getString(pg, LanguageManager.TS.CAGES_SET, false));
                            }
                            break;
                    }
                }
                if (event.getBlock().getType() == Material.CHEST && pg.getSetup() != null) {
                    String name = event.getItemInHand().getItemMeta().getDisplayName();
                    Block b = event.getBlock();
                    BlockData blockData = b.getBlockData();
                    BlockFace f = ((Directional) blockData).getFacing();
                    switch (ChatColor.stripColor(name).toLowerCase()) {
                        case "common chest":
                            pg.getSetup().addChest(new SWChest(SWChest.ChestTYPE.NORMAL, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ(), f, false));
                            pg.sendMessage("Added Normal Chest.");
                            break;
                        case "mid chest":
                            pg.getSetup().addChest(new SWChest(SWChest.ChestTYPE.MID, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ(), f, false));
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
        int i = p.getSetup().getCages().size() + 1;
        switch (p.getSetup().getGameType()) {
            case SOLO:
                SoloCage c = new SoloCage(i, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), 0, 64, 0);
                p.getSetup().addCage(c);
                break;
            case TEAMS:
                TeamCage tc = new TeamCage(i, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), p.getSetup().getPlayersPerTeam());
                p.getSetup().addCage(tc);
                break;
        }
        e.getPlayer().sendMessage(ChatColor.GREEN + "You placed cage number " + i);
    }

    //player join, leave, drop item, player damage, on kill, on hit, on shoot

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        RSWPlayer rswPlayer = RealSkywars.getPlayerManager().getPlayer(event.getPlayer());
        if (rswPlayer.getState() != RSWPlayer.PlayerState.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            RSWPlayer damaged = RealSkywars.getPlayerManager().getPlayer(p);
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (damaged.isInMatch()) {
                    e.setDamage(0);
                    if (damaged.getState().name().contains("SPEC")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                            damaged.getPlayer().spigot().respawn();
                            damaged.teleport(damaged.getMatch().getSpectatorLocation());
                        }, 1);
                        return;
                    }

                    if (damaged.getMatch().getState() == SWGameMode.GameState.PLAYING) {
                        damaged.addStatistic(RSWPlayer.Statistic.DEATH, 1, damaged.getMatch().isRanked());

                        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                            damaged.getPlayer().spigot().respawn();
                            damaged.getMatch().spectate(damaged, SWGameMode.SpectateType.GAME, damaged.getMatch().getSpectatorLocation());
                        }, 1);
                    } else {
                        damaged.teleport(damaged.getMatch().getSpectatorLocation());
                    }

                } else {
                    e.setCancelled(true);
                    damaged.heal();
                    RealSkywars.getGameManager().tpToLobby(damaged);
                }
            } else {
                if (damaged.isInvencible() || RealSkywars.getGameManager().isInLobby(damaged.getLocation().getWorld())) {
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
            RSWPlayer killer = RealSkywars.getPlayerManager().getPlayer(pkiller);
            if (killer.isInMatch()) {
                killer.addStatistic(RSWPlayer.Statistic.KILL, 1, killer.getMatch().isRanked());
            }
            deathLoc = pkiller.getLocation();
        }

        RSWPlayer killed = RealSkywars.getPlayerManager().getPlayer(pkilled);

        if (killed.getState().name().contains("SPEC")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                killed.getPlayer().spigot().respawn();
                killed.teleport(killed.getMatch().getSpectatorLocation());
            }, 1);
            return;
        }

        if (killed.isInMatch() && killed.getMatch().getState().equals(SWGameMode.GameState.PLAYING)) {
            killed.addStatistic(RSWPlayer.Statistic.DEATH, 1, killed.getMatch().isRanked());

            Location finalDeathLoc = deathLoc;
            Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                if (killed.getPlayer() != null) {
                    killed.getPlayer().spigot().respawn();
                }
                if (finalDeathLoc == null) {
                    if (killed.getPlayer() != null)
                        killed.getMatch().spectate(killed, SWGameMode.SpectateType.GAME, killed.getMatch().getSpectatorLocation());
                } else {
                    if (killed.getPlayer() != null)
                        killed.getMatch().spectate(killed, SWGameMode.SpectateType.GAME, finalDeathLoc);
                }
            }, 1);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player whoWasHit = (Player) e.getEntity();
            Player whoHit = (Player) e.getDamager();
            RSWPlayer hitter = RealSkywars.getPlayerManager().getPlayer(whoHit);
            RSWPlayer hurt = RealSkywars.getPlayerManager().getPlayer(whoWasHit);
            if (hitter.getTeam() != null && hitter.getTeam().getMembers().contains(hurt)) {
                whoHit.sendMessage(RealSkywars.getLanguageManager().getString(hitter, LanguageManager.TS.TEAMMATE_DAMAGE_CANCEL, true));
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
            RSWPlayer click = RealSkywars.getPlayerManager().getPlayer(event.getPlayer());
            RSWPlayer clicked = RealSkywars.getPlayerManager().getPlayer((Player) event.getRightClicked());
            if (click != null && clicked != null && !clicked.isInMatch()) {
                PlayerGUI playg = new PlayerGUI(click, click.getUUID(), clicked);
                playg.openInventory(click);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        RealSkywars.getPlayerManager().loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        RSWPlayer p = RealSkywars.getPlayerManager().getPlayer(e.getPlayer());
        if (p != null) {
            p.leave();
        }
    }

    @EventHandler
    public void onPlayerShootArrow(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() != null && e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof Arrow) {
            Player p = (Player) e.getEntity().getShooter();
            RSWPlayer gp = RealSkywars.getPlayerManager().getPlayer(p);
            assert gp != null;
            if (gp.getProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES) != null && gp.isInMatch()) {
                gp.addTrail(new BowTrail((Particle) gp.getProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES), e.getEntity(), gp));
            }
        }
    }

    //make chest open
    @EventHandler
    public void onInv(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Chest) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) event.getPlayer());
            if (p != null && p.isInMatch()) {
                Chest c = (Chest) event.getInventory().getHolder();
                SWChest swc = p.getMatch().getChest(c.getLocation());
                if (swc != null && swc.isOpened()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> RealSkywars.getNMS().playChestAnimation(swc.getChestBlock(), true), 2);
                }
            }
        }
    }
}