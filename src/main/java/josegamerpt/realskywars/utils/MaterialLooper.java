package josegamerpt.realskywars.utils;

import org.bukkit.Material;

import java.util.List;

public class MaterialLooper {

    private final List<Material> list;
    private Material get;
    private int i = 0;

    public MaterialLooper(List<Material> s) {
        this.list = s;
    }

    public void next() {
        try {
            if (this.i >= this.list.size()) {
                this.i = 0;
            }
            this.get = list.get(i);
            this.i++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Material get() {
        return this.get;
    }

}
