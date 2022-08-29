package josegamerpt.realskywars.holograms;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.holograms.classes.DHHologram;
import josegamerpt.realskywars.holograms.classes.HDHologram;
import josegamerpt.realskywars.holograms.classes.NoHologram;
import org.bukkit.Bukkit;

public class HologramManager {
    private HologramType selected = HologramType.NONE;

    public HologramManager() {
        //select scoreboard plugin
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.selected = HologramType.HOLOGRAPHIC_DISPLAYS;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            this.selected = HologramType.DECENT_HOLOGRAMS;
        }

        switch (this.selected) {
            case DECENT_HOLOGRAMS:
                RealSkywars.log("Hooked on Decent Holograms!");
                break;
            case HOLOGRAPHIC_DISPLAYS:
                RealSkywars.log("Hooked on Holographic Displays!");
                break;
        }
    }

    public SWHologram getHologramInstance() {
        switch (this.selected) {
            case DECENT_HOLOGRAMS:
                return new DHHologram();
            case HOLOGRAPHIC_DISPLAYS:
                return new HDHologram();
            default:
                return new NoHologram();
        }
    }
}
