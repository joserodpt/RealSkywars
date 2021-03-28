package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoomTAB {

    private final RSWPlayer player;
    private ArrayList<Player> show = new ArrayList<>();

    public RoomTAB(RSWPlayer player) {
        this.player = player;
        clear();
    }

    public void add(Player p) {
        if (p.getUniqueId() != this.player.getUniqueId() && !show.contains(p)) {
            show.add(p);
        }
    }

    public void add(List<Player> p) {
        show.addAll(p);
    }


    public void remove(Player p) {
        show.remove(p);
    }

    public void reset() {
        this.show.addAll(Bukkit.getOnlinePlayers());
    }

    public void clear() {
        this.show.clear();
    }

    public void setHeader(String s) {
        if (!this.player.isBot()) {
            this.player.getPlayer().setPlayerListHeader(s);
        }
    }

    public void setFooter(String s) {
        if (!this.player.isBot()) {
            this.player.getPlayer().setPlayerListFooter(s);
        }
    }

    public void updateRoomTAB() {
        if (!this.player.isBot()) {
            Bukkit.getOnlinePlayers().forEach(pl -> this.player.hidePlayer(RealSkywars.getPlugin(), pl));
            this.show.forEach(rswPlayer -> this.player.showPlayer(RealSkywars.getPlugin(), rswPlayer));
            Debugger.print(RoomTAB.class, player.getName() + " - " + getNames());
        }
    }

    private String getNames() {
        ArrayList<String> n = new ArrayList<>();
        for (Player player1 : this.show) {
            if (player1 != null)
            {
                n.add(player1.getName());
            }
        }
        return Strings.join(n, ',');
    }

}
