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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWCountdown;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.teams.Team;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.FireworkUtils;
import joserodpt.realskywars.api.utils.Text;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SoloMode extends RSWMap {

    private final List<RSWCage> cages;

    public SoloMode(String nome, String displayName, World w, String schematicName, RSWWorld.WorldType wt, MapState estado, List<RSWCage> cages, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Boolean border, Location pos1, Location pos2, List<RSWChest> chests, Boolean rankd, RealSkywarsAPI rs) {
        super(nome, displayName, w, schematicName, wt, estado, maxPlayers, spectatorLocation, specEnabled, instantEnding, border, pos1, pos2, chests, rankd, rs);
        this.cages = cages;
    }

    @Override
    public boolean isPlaceHolder() {
        return false;
    }

    @Override
    public void forceStartMap() {
        if (canStartMap()) {
            super.cancelMapStart();
        } else {
            this.setState(MapState.PLAYING);

            super.getStartRoomTimer().killTask();

            super.calculateVotes();

            for (RSWPlayer p : this.getPlayers()) {
                if (p.getPlayer() != null) {
                    p.setBarNumber(0);
                    p.getInventory().clear();

                    super.getBossBar().addPlayer(p.getPlayer());

                    //start msg
                    for (String s : Text.color(super.getRealSkywarsAPI().getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.ARENA_START))) {
                        p.sendCenterMessage(s.replace("%chests%", WordUtils.capitalizeFully(super.getChestTier().name())).replace("%kit%", p.getKit().getDisplayName()).replace("%project%", WordUtils.capitalizeFully(super.getProjectileTier().name().replace("_", " "))).replace("%time%", WordUtils.capitalizeFully(super.getTimeType().name())));
                    }

                    p.getKit().give(p);

                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.PLAYING);
                    p.getCage().open();
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
            p.getCage().removePlayer(p);
        }

        super.commonRemovePlayer(p);
    }

    @Override
    public void addPlayer(RSWPlayer p) {
        if (super.getRealSkywarsAPI().getPartiesManagerAPI().checkForParties(p, this)) {
            switch (this.getState()) {
                case RESETTING:
                    p.sendMessage(super.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CANT_JOIN, true));
                    break;
                case FINISHING:
                case PLAYING:
                    if (this.isSpectatorEnabled()) {
                        spectate(p, SpectateType.EXTERNAL, null);
                    } else {
                        p.sendMessage(super.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SPECTATING_DISABLED, true));
                    }
                    break;
                default:
                    if (this.getPlayerCount() == this.getMaxPlayers()) {
                        p.sendMessage(super.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ROOM_FULL, true));
                        return;
                    }

                    //cage

                    for (RSWCage c : this.cages) {
                        if (c.isEmpty() && p.getPlayer() != null) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    p.setRoom(this);
                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

                    super.getAllPlayers().add(p);

                    if (p.getPlayer() != null) {
                        super.getBossBar().addPlayer(p.getPlayer());
                        p.heal();
                        List<String> up = super.getRealSkywarsAPI().getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.TITLE_ROOMJOIN);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
                    }

                    for (RSWPlayer ws : this.getAllPlayers()) {
                        ws.sendMessage(super.getRealSkywarsAPI().getLanguageManagerAPI().getString(ws, LanguageManagerAPI.TS.PLAYER_JOIN_ARENA, true).replace("%player%", p.getDisplayName()).replace("%players%", getPlayerCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
                    }

                    super.getRealSkywarsAPI().getPlayerManagerAPI().giveItems(p.getPlayer(), PlayerManagerAPI.Items.CAGE);

                    //update tab
                    if (!p.isBot()) {
                        for (RSWPlayer player : this.getPlayers()) {
                            if (!player.isBot()) {
                                RSWPlayer.RoomTAB rt = player.getTab();
                                List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                                rt.clear();
                                rt.add(players);
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

            super.getStartTimer().killTask();
            super.getTimeCounterTask().cancel();

            super.getBossBar().setTitle(TranslatableLine.BOSSBAR_ARENA_END.get());
            super.getBossBar().setProgress(0);
            super.getBossBar().setColor(BarColor.BLUE);

            super.getRealSkywarsAPI().getPlayerManagerAPI().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(super.getRealSkywarsAPI().getLanguageManagerAPI().getString(gamePlayer, LanguageManagerAPI.TS.WINNER_BROADCAST, true).replace("%winner%", p.getDisplayName()).replace("%map%", super.getMapName()).replace("%displayname%", super.getDisplayName())));

            if (this.isInstantEndEnabled()) {
                super.getBossBar().removeAll();
                this.sendLog(p, true);
                this.kickPlayers(null);
                this.resetArena(OperationReason.RESET);
            } else {
                super.setWinTimer(new RSWCountdown(super.getRealSkywarsAPI().getPlugin(), RSWConfig.file().getInt("Config.Time-EndGame"), () -> {
                    if (p.getPlayer() != null) {
                        p.setInvincible(true);
                        p.addStatistic(RSWPlayer.Statistic.SOLO_WIN, 1, this.isRanked());
                        p.executeWinBlock(RSWConfig.file().getInt("Config.Time-EndGame") - 2);
                    }

                    for (RSWPlayer g : super.getAllPlayers()) {
                        g.delCage();
                        g.sendMessage(super.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MATCH_END, true).replace("%time%", Text.formatSeconds(RSWConfig.file().getInt("Config.Time-EndGame"))));
                    }
                }, () -> {
                    super.getBossBar().removeAll();
                    this.sendLog(p, true);
                    this.kickPlayers(null);
                    this.resetArena(OperationReason.RESET);
                }, (t) -> {
                    double div = (double) t.getSecondsLeft() / (double) RSWConfig.file().getInt("Config.Time-EndGame");
                    if (div <= 1 && div >= 0) {
                        super.getBossBar().setProgress(div);
                    }

                    super.getAllPlayers().forEach(rswPlayer -> rswPlayer.setBarNumber(t.getSecondsLeft(), RSWConfig.file().getInt("Config.Time-EndGame")));

                    if (p.getPlayer() != null) {
                        FireworkUtils.spawnRandomFirework(p.getLocation());
                    }
                }));

                super.getWinTimer().scheduleTimer();
            }

            super.getChests().forEach(RSWChest::cancelTasks);
            super.getChests().forEach(RSWChest::clearHologram);
        }
    }

    @Override
    public Mode getGameMode() {
        return Mode.SOLO;
    }

    @Override
    public List<RSWCage> getCages() {
        return this.cages;
    }

    @Override
    public List<Team> getTeams() {
        return null;
    }

    @Override
    public int maxMembersTeam() {
        return 0;
    }

    @Override
    public int getMaxTime() {
        return RSWConfig.file().getInt("Config.Maximum-Game-Time.Solo");
    }

    @Override
    public int minimumPlayersToStartMap() {
        return RSWConfig.file().getInt("Config.Min-Players-ToStart");
    }
}
