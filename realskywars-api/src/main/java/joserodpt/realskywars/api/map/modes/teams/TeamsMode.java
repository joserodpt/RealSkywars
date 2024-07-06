package joserodpt.realskywars.api.map.modes.teams;

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


import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerItems;
import joserodpt.realskywars.api.player.RSWPlayerTab;
import joserodpt.realskywars.api.utils.CountdownTimer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamsMode extends RSWMap {

    private int maxMembersTeam = 0;
    private final Map<Location, RSWTeam> teams;

    public TeamsMode(String nome, String displayName, World w, String schematicName, RSWWorld.WorldType wt, int teams, int playersPerTeam) {
        super(nome, displayName, w, schematicName, wt, MapState.RESETTING, teams * playersPerTeam, null, true, false, true, null, null, new HashMap<>(), false, true);

        this.teams = new HashMap<>();
        this.maxMembersTeam = playersPerTeam;
    }

    public TeamsMode(String nome, String displayName, World w, String schematicName, RSWWorld.WorldType wt, MapState estado, Map<Location, RSWTeam> teams, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Boolean border, Location pos1, Location pos2, Map<Location, RSWChest> chests, Boolean rankd, Boolean unregistered) {
        super(nome, displayName, w, schematicName, wt, estado, maxPlayers, spectatorLocation, specEnabled, instantEnding, border, pos1, pos2, chests, rankd, unregistered);

        this.teams = teams;

        for (Location location : this.teams.keySet()) { // extract first team to get max members
            this.maxMembersTeam = teams.get(location).getMaxMembers();
            break;
        }

        this.teams.forEach((loc, team) -> team.getTeamCage().setMap(this));
    }

    @Override
    public boolean isPlaceHolder() {
        return false;
    }

    @Override
    public void forceStartMap() {
        if (super.getPlayerCount() < this.maxMembersTeam + 1) {
            super.cancelMapStart();
        } else {
            this.setState(MapState.PLAYING);

            super.getStartMapTimer().killTask();

            super.calculateVotes();

            for (RSWTeam t : this.getTeams()) {
                for (RSWPlayer p : t.getMembers()) {
                    if (p.getPlayer() != null) {
                        p.setBarNumber(0);
                        p.getInventory().clear();

                        super.getBossBar().addPlayer(p.getPlayer());

                        //start msg
                        TranslatableList.MAP_START.get(p).forEach(s -> p.sendCenterMessage(s.replace("%chests%", super.getChestTier().getDisplayName(p)).replace("%kit%", p.getPlayerKit().getDisplayName()).replace("%project%", super.getProjectileTier().getDisplayName(p)).replace("%time%", super.getTimeType().getDisplayName(p))));

                        p.getPlayerKit().give(p);
                        p.setState(RSWPlayer.PlayerState.PLAYING);
                    }
                }
                t.openCage();
            }

            super.startTimers();
        }
    }

    @Override
    public boolean canStartMap() {
        return super.getPlayerCount() < (this.getMaxTeamMembers() + 1);
    }

    @Override
    public void removePlayer(RSWPlayer p) {
        if (p.hasTeam()) {
            p.getTeam().removeMember(p);
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
                    for (RSWTeam c : this.getTeams()) {
                        if (!c.isTeamFull()) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    p.setPlayerMap(this);
                    p.setState(RSWPlayer.PlayerState.CAGE);

                    for (RSWPlayer ws : super.getAllPlayers()) {
                        if (p.getPlayer() != null) {
                            ws.sendMessage(TranslatableLine.PLAYER_JOIN_ARENA.get(ws, true).replace("%player%", p.getDisplayName()).replace("%players%", this.getPlayerCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
                        }
                    }

                    super.getAllPlayers().add(p);
                    p.heal();

                    if (p.getPlayer() != null) {
                        super.getBossBar().addPlayer(p.getPlayer());
                        List<String> up = TranslatableList.TITLE_ROOMJOIN.get(p);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
                    }

                    RSWPlayerItems.CAGE.giveSet(p);

                    //update tab
                    if (!p.isBot()) {
                        for (RSWPlayer player : this.getPlayers()) {
                            if (!player.isBot()) {
                                RSWPlayerTab rt = player.getTab();
                                List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                                rt.clear();
                                rt.add(players);
                                rt.updateRoomTAB();
                            }
                        }
                    }

                    if (this.getPlayerCount() == this.maxMembersTeam + 1) {
                        super.startRoom();
                    }
                    break;
            }

            //call api
            super.getRealSkywarsAPI().getEventsAPI().callRoomStateChange(this);

            //signal that is ranked
            if (this.isRanked()) p.sendActionbar("&b&lRANKED");
        }
    }

    @Override
    public void resetArena(OperationReason rr) {
        this.getTeams().forEach(RSWTeam::reset);
        super.commonResetArena(rr);
    }

    private int getAliveTeams() {
        return (int) this.getTeams().stream()
                .filter(t -> !t.isEliminated() && t.getMemberCount() > 0)
                .count();
    }

    @Override
    public void checkWin() {
        if (this.getAliveTeams() == 1 && this.getState() != MapState.FINISHING) {
            this.setState(MapState.FINISHING);

            RSWTeam winRSWTeam = getPlayers().get(0).getTeam();

            super.getMapTimer().killTask();
            super.getTimeCounterTask().cancel();

            super.getRealSkywarsAPI().getPlayerManagerAPI().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(TranslatableLine.WINNER_BROADCAST.get(gamePlayer, true).replace("%winner%", winRSWTeam.getNames()).replace("%map%", super.getName()).replace("%displayname%", super.getDisplayName())));

            if (this.isInstantEndEnabled()) {
                winRSWTeam.getMembers().forEach(rswPlayer -> this.sendLog(rswPlayer, true));
                this.kickPlayers(null);
                this.resetArena(OperationReason.RESET);
            } else {
                super.setFinishingTimer(new CountdownTimer(super.getRealSkywarsAPI().getPlugin(), this.getTimeEndGame(), () -> {
                    for (RSWPlayer p : winRSWTeam.getMembers()) {
                        if (p.getPlayer() != null) {
                            p.setInvincible(true);
                            p.addStatistic(RSWPlayer.Statistic.TEAM_WIN, 1, this.isRanked());
                            p.executeWinBlock(this.getTimeEndGame() - 2);
                        }
                        this.sendLog(p, true);
                    }

                    for (RSWPlayer g : super.getAllPlayers()) {
                        if (g.getPlayer() != null) {
                            g.sendMessage(TranslatableLine.MATCH_END.get(g, true).replace("%time%", Text.formatSeconds(this.getTimeEndGame())));
                            g.getPlayer().sendTitle("", Text.color(TranslatableLine.TITLE_WIN.get(g).replace("%player%", winRSWTeam.getNames())), 10, 40, 10);
                        }
                    }
                }, () -> {
                    winRSWTeam.getMembers().forEach(rswPlayer -> this.sendLog(rswPlayer, true));
                    this.kickPlayers(null);
                    this.resetArena(OperationReason.RESET);
                }, (t) -> {
                    // if (Players.get(0).p != null) {
                    //     firework(Players.get(0));
                    // }

                    super.getAllPlayers().forEach(rswPlayer -> rswPlayer.setBarNumber(t.getSecondsLeft(), this.getTimeEndGame()));
                }));

                super.getFinishingTimer().scheduleTimer();
            }

            super.getChests().forEach(RSWChest::cancelTasks);
            super.getChests().forEach(RSWChest::clearHologram);
        }
    }

    @Override
    public Mode getGameMode() {
        return Mode.TEAMS;
    }

    @Override
    public Collection<RSWCage> getCages() {
        return null;
    }

    @Override
    public Collection<RSWTeam> getTeams() {
        return this.teams.values();
    }

    @Override
    public int getMaxTeamMembers() {
        return this.maxMembersTeam;
    }

    @Override
    public int minimumPlayersToStartMap() {
        return getMaxTeamMembers() + 1;
    }

    @Override
    public void removeCage(Location loc) {
        for (Location location : this.teams.keySet()) {
            if (location.getBlockX() == loc.getX() && location.getBlockY() == loc.getY() && location.getBlockZ() == loc.getZ()) {
                this.teams.remove(location);
                this.save(Data.CAGES, true);
                break;
            }
        }
    }

    @Override
    public void addCage(Location location) {
        RSWTeam t = new RSWTeam(this.getTeams().size() + 1, this.getMaxTeamMembers(), location);
        t.getTeamCage().setMap(this);
        this.teams.put(location, t);
        this.save(Data.CAGES, true);
    }
}
