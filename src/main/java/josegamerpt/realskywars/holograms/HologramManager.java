package josegamerpt.realskywars.holograms;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.holograms.support.DHHologram;
import josegamerpt.realskywars.holograms.support.HDHologram;
import josegamerpt.realskywars.holograms.support.NoHologram;
import org.bukkit.Bukkit;

public class HologramManager {
    private SWHologram.HType selected = SWHologram.HType.NONE;

    public HologramManager() {
        //select scoreboard plugin
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.selected = SWHologram.HType.HOLOGRAPHIC_DISPLAYS;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            this.selected = SWHologram.HType.DECENT_HOLOGRAMS;
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
