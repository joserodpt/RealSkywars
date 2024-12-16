package joserodpt.realskywars.api.map.modes;

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

import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.cages.RSWSoloCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.teams.RSWTeam;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerItems;
import joserodpt.realskywars.api.player.tab.RSWPlayerTabInterface;
import joserodpt.realskywars.api.utils.CountdownTimer;
import joserodpt.realskywars.api.utils.FireworkUtils;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SoloMode extends RSWMap {

    private final Map<Location, RSWCage> cages;

    //para dar setup
    public SoloMode(String nome, String displayName, World w, String schematicName, RSWWorld.WorldType wt, int maxPlayers) {
        super(nome.replace(".schematic", "").replace(".schem", ""), displayName.replace(".schematic", "").replace(".schem", ""), w, schematicName, wt, MapState.RESETTING, maxPlayers, null, true, false, true, null, null, new HashMap<>(), false, true);
        this.cages = new HashMap<>();
    }

    public SoloMode(String nome, String displayName, World w, String schematicName, RSWWorld.WorldType wt, MapState estado, Map<Location, RSWCage> cages, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Boolean border, Location pos1, Location pos2, Map<Location, RSWChest> chests, Boolean rankd, Boolean unregistered) {
        super(nome, displayName, w, schematicName, wt, estado, maxPlayers, spectatorLocation, specEnabled, instantEnding, border, pos1, pos2, chests, rankd, unregistered);
        this.cages = cages;
        this.cages.forEach((location, rswCage) -> rswCage.setMap(this));
    }

    @Override
    public void forceStartMap() {
        if (canStartMap()) {
            super.cancelMapStart();
        } else {
            this.setState(MapState.PLAYING);
            super.setStartingPlayers(super.getPlayerCount());

            super.getStartMapTimer().killTask();

            super.calculateVotes();

            for (RSWPlayer p : this.getPlayers()) {
                if (p.getPlayer() != null) {
                    p.setBarNumber(0);
                    p.getInventory().clear();

                    super.getBossBar().addPlayer(p.getPlayer());

                    //start msg
                    TranslatableList.MAP_START.get(p).forEach(s -> p.sendCenterMessage(s.replace("%chests%", super.getChestTier().getDisplayName(p)).replace("%kit%", p.getPlayerKit().getDisplayName()).replace("%project%", super.getProjectileTier().getDisplayName(p)).replace("%time%", super.getTimeType().getDisplayName(p))));

                    p.getPlayerKit().give(p);
                    p.setState(RSWPlayer.PlayerState.PLAYING);
                    p.getPlayerCage().open();
                }
            }

            super.startTimers();
        }
    }

    @Override
    public boolean canStartMap() {
        return super.getPlayerCount() < RSWConfig.file().getInt("Config.Min-Players-ToStart");
    }

    @Override
    public void removePlayer(RSWPlayer p) {
        if (p.hasCage()) {
            p.getPlayerCage().removePlayer(p);
        }

        super.commonRemovePlayer(p);
    }

    @Override
    public void addPlayer(RSWPlayer p) {
        if (this.isUnregistered()) {
            TranslatableLine.MAP_IS_UNREGISTERED.send(p, true);
            return;
        }

        if (super.getRealSkywarsAPI().getPartiesManagerAPI().checkForParties(p, this)) {
            switch (this.getState()) {
                case RESETTING:
                    TranslatableLine.CANT_JOIN.send(p, true);
                    return;
                case FINISHING:
                case PLAYING:
                    if (this.isSpectatorEnabled()) {
                        spectate(p, SpectateType.EXTERNAL, null);
                    } else {
                        TranslatableLine.SPECTATING_DISABLED.send(p, true);
                        return;
                    }
                    break;
                default:
                    if (this.getPlayerCount() == this.getMaxPlayers()) {
                        if (RSWConfig.file().getBoolean("Config.Bungeecord.Enabled")) {
                            spectate(p, SpectateType.EXTERNAL, null);
                            return;
                        } else {
                            TranslatableLine.ROOM_FULL.send(p, true);
                            return;
                        }
                    }

                    //cage
                    for (RSWCage c : this.cages.values()) {
                        if (c.isEmpty() && p.getPlayer() != null) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    p.setPlayerMap(this);
                    p.setState(RSWPlayer.PlayerState.CAGE);

                    super.getAllPlayers().add(p);

                    if (p.getPlayer() != null) {
                        super.getBossBar().addPlayer(p.getPlayer());
                        p.heal();
                        List<String> up = TranslatableList.TITLE_ROOMJOIN.get(p);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
                    }

                    for (RSWPlayer ws : this.getAllPlayers()) {
                        ws.sendMessage(TranslatableLine.PLAYER_JOIN_ARENA.get(p, true).replace("%player%", p.getDisplayName()).replace("%players%", getPlayerCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
                    }

                    RSWPlayerItems.CAGE.giveSet(p);

                    //update tab
                    if (!p.isBot()) {
                        for (RSWPlayer player : this.getPlayers()) {
                            if (!player.isBot()) {
                                RSWPlayerTabInterface rt = player.getTab();
                                List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                                rt.clear();
                                rt.addPlayers(players);
                                rt.updateRoomTAB();
                            }
                        }
                    }

                    if (getPlayerCount() == RSWConfig.file().getInt("Config.Min-Players-ToStart")) {
                        startRoom();
                    }
                    break;
            }

            //call api
            super.getRealSkywarsAPI().getEventsAPI().callRoomStateChange(this);

            //signal that is ranked
            if (super.isRanked()) p.sendActionbar("&b&lRANKED");
        }
    }

    @Override
    public void resetArena(OperationReason rr) {
        super.commonResetArena(rr);
    }

    @Override
    public void checkWin() {
        if (this.getPlayerCount() == 1 && super.getState() != MapState.FINISHING) {
            this.setState(MapState.FINISHING);

            RSWPlayer p = getPlayers().get(0);
            p.setInvincible(true);

            super.getMapTimer().killTask();
            super.getTimeCounterTask().cancel();

            super.getRealSkywarsAPI().getPlayerManagerAPI().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(TranslatableLine.WINNER_BROADCAST.get(gamePlayer, true).replace("%winner%", p.getDisplayName()).replace("%map%", super.getName()).replace("%displayname%", super.getDisplayName())));

            if (this.isInstantEndEnabled()) {
                this.sendLog(p, true);
                this.kickPlayers(null);
                this.resetArena(OperationReason.RESET);
            } else {
                super.setFinishingTimer(new CountdownTimer(super.getRealSkywarsAPI().getPlugin(), this.getTimeEndGame(), () -> {
                    super.getBossBar().tick();
                    if (p.getPlayer() != null) {
                        p.setInvincible(true);
                        p.addStatistic(RSWPlayer.Statistic.SOLO_WIN, 1, this.isRanked());
                        p.executeWinBlock(this.getTimeEndGame() - 2);
                    }

                    for (RSWPlayer g : super.getAllPlayers()) {
                        g.delCage();
                        g.sendMessage(TranslatableLine.MATCH_END.get(g, true).replace("%time%", Text.formatSeconds(this.getTimeEndGame())));
                    }
                }, () -> {
                    super.getBossBar().tick();
                    this.sendLog(p, true);
                    this.kickPlayers(null);
                    this.resetArena(OperationReason.RESET);
                }, (t) -> {
                    super.getAllPlayers().forEach(rswPlayer -> rswPlayer.setBarNumber(t.getSecondsLeft(), this.getTimeEndGame()));
                    super.getBossBar().tick();
                    if (p.getPlayer() != null) {
                        FireworkUtils.spawnRandomFirework(p.getLocation());
                    }
                }));

                super.getFinishingTimer().scheduleTimer();
            }

            super.getChests().forEach(RSWChest::cancelTasks);
            super.getChests().forEach(RSWChest::clearHologram);
        }
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SOLO;
    }

    @Override
    public Collection<RSWCage> getCages() {
        return this.cages.values();
    }

    @Override
    public Collection<RSWTeam> getTeams() {
        return Collections.emptyList();
    }

    @Override
    public int getMaxTeamsNumber() {
        return 0;
    }

    @Override
    public int getMaxTeamsMembers() {
        return 0;
    }

    @Override
    public int minimumPlayersToStartMap() {
        return RSWConfig.file().getInt("Config.Min-Players-ToStart");
    }

    @Override
    public void removeCage(Location loc) {
        for (Location location : this.cages.keySet()) {
            if (location.getBlockX() == loc.getX() && location.getBlockY() == loc.getY() && location.getBlockZ() == loc.getZ()) {
                this.cages.remove(location);
                this.save(Data.CAGES, true);
                break;
            }
        }
    }

    @Override
    public void addCage(Location location) {
        RSWSoloCage cage = new RSWSoloCage(this.cages.size() + 1, location);
        cage.setMap(this);
        this.cages.put(location, cage);
        this.save(Data.CAGES, true);
    }
}
