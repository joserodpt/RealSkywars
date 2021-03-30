package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.SoloCage;
import josegamerpt.realskywars.cages.TeamCage;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.SWChest;
import josegamerpt.realskywars.configuration.Items;
import josegamerpt.realskywars.effects.BowTrail;
import josegamerpt.realskywars.gui.GUIManager;
import josegamerpt.realskywars.gui.MapsViewer;
import josegamerpt.realskywars.gui.PlayerGUI;
import josegamerpt.realskywars.gui.ProfileContent;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.KitManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.modes.SWGameMode;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerEvents implements Listener {

    //special items handling
    @EventHandler
    public void pegar(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            RSWPlayer gp = PlayerManager.getPlayer((Player) e.getEntity());
            switch (gp.getState()) {
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void items(PlayerInteractEvent e) {
        RSWPlayer gp = PlayerManager.getPlayer(e.getPlayer());
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR && e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {

            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.PROFILE)) {
                GUIManager.openPlayerMenu(gp, !gp.isInMatch());
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                e.setCancelled(true);
            }

            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.KIT)) {
                ProfileContent v = new ProfileContent(gp, Enum.Categories.KITS);
                v.openInventory(gp);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                e.setCancelled(true);
            }

            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.SPECTATE)) {
                GUIManager.openSpectate(gp);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                e.setCancelled(true);
            }
            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.MAPS)) {
                MapsViewer v = new MapsViewer(gp, gp.getSelection(Enum.Selection.MAPVIEWER), "Maps");
                v.openInventory(gp);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                e.setCancelled(true);
            }
            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.LEAVE)) {
                gp.getRoom().removePlayer(gp);
                e.setCancelled(true);
            }
            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.CHESTS)) {
                GUIManager.openVote(gp);
                e.setCancelled(true);
            }
            if (e.getPlayer().getInventory().getItemInMainHand()
                    .equals(Items.SHOP)) {
                GUIManager.openShopMenu(gp);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                e.setCancelled(true);
            }
        }
        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Chest) {
            if (gp.isInMatch()) {
                SWChest chest = gp.getRoom().getChest(e.getClickedBlock().getLocation());
                if (chest != null) {
                    chest.fill();
                }
            }
        }
    }

    //player block interactions

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        RSWPlayer p = PlayerManager.getPlayer(event.getPlayer());
        if (p != null) {
            switch (p.getState()) {
                case SPECTATOR:
                case EXTERNAL_SPECTATOR:
                case CAGE:
                    event.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            RSWPlayer pg = PlayerManager.getPlayer(event.getPlayer());
            if (pg.getState() != null) {
                if (event.getBlock().getType() == Items.CAGESET.getType()) {
                    switch (pg.getSetup().getGameType()) {
                        case SOLO:
                            if ((pg.getSetup().getCages().size() + 1) < pg.getSetup().getMaxPlayers()) {
                                log(event, pg);
                            } else {
                                log(event, pg);
                                pg.getSetup().confirmCages(true);
                                pg.sendMessage(LanguageManager.getString(pg, Enum.TS.CAGES_SET, false));
                            }
                            break;
                        case TEAMS:
                            if ((pg.getSetup().getCages().size() + 1) < pg.getSetup().getTeamCount()) {
                                log(event, pg);
                            } else {
                                log(event, pg);
                                pg.getSetup().confirmCages(true);
                                pg.sendMessage(LanguageManager.getString(pg, Enum.TS.CAGES_SET, false));
                            }
                            break;
                    }
                }
                if (event.getBlock().getType() == Items.CHEST1.getType()) {

                    String name = event.getItemInHand().getItemMeta().getDisplayName();
                    switch (ChatColor.stripColor(name).toLowerCase()) {
                        case "common chest":
                            pg.getSetup().addChest(new SWChest(SWChest.ChestTYPE.NORMAL, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()));
                            pg.sendMessage("Added Normal Chest.");
                            break;
                        case "mid chest":
                            pg.getSetup().addChest(new SWChest(SWChest.ChestTYPE.MID, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()));
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
        if (event.getItemDrop().getItemStack().equals(Items.SHOP)
                || event.getItemDrop().getItemStack().equals(Items.CAGESET)
                || event.getItemDrop().getItemStack().equals(Items.CHESTS)
                || event.getItemDrop().getItemStack().equals(Items.PROFILE)
                || event.getItemDrop().getItemStack().equals(Items.MAPS)
                || event.getItemDrop().getItemStack().equals(Items.LEAVE)
                || event.getItemDrop().getItemStack().equals(Items.SPECTATE)
                || event.getItemDrop().getItemStack().getType().equals(Material.PLAYER_HEAD)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            RSWPlayer damaged = PlayerManager.getPlayer(p);
            switch (e.getCause()) {
                case VOID:
                    if (damaged.isInMatch()) {
                        e.setDamage(0);
                        switch (damaged.getRoom().getState()) {
                            case PLAYING:
                                damaged.addStatistic(Enum.Statistic.DEATH, 1);

                                Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                                    damaged.getPlayer().spigot().respawn();
                                    damaged.getRoom().spectate(damaged, SWGameMode.SpectateType.GAME, damaged.getRoom().getSpectatorLocation());
                                }, 1);
                                break;
                            default:
                                damaged.teleport(damaged.getRoom().getSpectatorLocation());
                                break;
                        }
                    }
                    break;
                default:
                    if (damaged.isInvencible() || GameManager.isInLobby(damaged.getLocation())) {
                        e.setCancelled(true);
                    }
                    break;
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
            RSWPlayer killer = PlayerManager.getPlayer(pkiller);
            if (killer.isInMatch()) {
                killer.addStatistic(Enum.Statistic.KILL, 1);
            }
            deathLoc = pkiller.getLocation();
        }

        RSWPlayer killed = PlayerManager.getPlayer(pkilled);
        if (killed.isInMatch() && killed.getRoom().getState().equals(Enum.GameState.PLAYING)) {
            killed.addStatistic(Enum.Statistic.DEATH, 1);

            Location finalDeathLoc = deathLoc;
            Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                if (killed.getPlayer() != null) {
                    killed.getPlayer().spigot().respawn();
                }
                if (finalDeathLoc == null) {
                    if (killed.getPlayer() != null)
                        killed.getRoom().spectate(killed, SWGameMode.SpectateType.GAME, killed.getRoom().getSpectatorLocation());
                } else {
                    if (killed.getPlayer() != null)
                        killed.getRoom().spectate(killed, SWGameMode.SpectateType.GAME, finalDeathLoc);
                }
            }, 1);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player whoWasHit = (Player) e.getEntity();
            Player whoHit = (Player) e.getDamager();
            RSWPlayer hitter = PlayerManager.getPlayer(whoHit);
            RSWPlayer hurt = PlayerManager.getPlayer(whoWasHit);
            if (hitter.getTeam() != null && hitter.getTeam().getMembers().contains(hurt)) {
                whoHit.sendMessage(LanguageManager.getString(hitter, Enum.TS.TEAMMATE_DAMAGE_CANCEL, true));
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
            RSWPlayer click = PlayerManager.getPlayer(event.getPlayer());
            RSWPlayer clicked = PlayerManager.getPlayer((Player) event.getRightClicked());
            if (click != null && clicked != null) {
                PlayerGUI playg = new PlayerGUI(click, click.getUniqueId(), clicked);
                playg.openInventory(click);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerManager.giveItems(e.getPlayer(), PlayerManager.PlayerItems.LOBBY);
        PlayerManager.loadPlayer(e.getPlayer());

        for (RSWPlayer player : PlayerManager.getPlayers()) {
            if (player.isInMatch()) {
                RSWPlayer.RoomTAB rt = player.getTab();
                rt.remove(e.getPlayer());
                rt.updateRoomTAB();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        RSWPlayer p = PlayerManager.getPlayer(e.getPlayer());

        if (p != null) {
            p.leave();
        }
    }

    @EventHandler
    public void onPlayerShootArrow(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() != null &&
                e.getEntity().getShooter() instanceof Player &&
                e.getEntity() instanceof Arrow) {
            Player p = (Player) e.getEntity().getShooter();
            RSWPlayer gp = PlayerManager.getPlayer(p);
            assert gp != null;
            if (gp.getProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES) != null && gp.isInMatch()) {
                gp.addTrail(new BowTrail((Particle) gp.getProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES), e.getEntity(), gp));
            }
        }
    }
}