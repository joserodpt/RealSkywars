package josegamerpt.realskywars.chests;

import josegamerpt.realskywars.configuration.chests.BasicChest;
import josegamerpt.realskywars.configuration.chests.EPICChest;
import josegamerpt.realskywars.configuration.chests.NormalChest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChestManager {

    public void set2Chest(SWChest.Tier t, Boolean mid, List<ItemStack> itens) {
        String header = "Itens.";

        int i = 0;
        int defaultChance = 50;
        if (mid) {
            header = "Mid." + header;
        }

        switch (t) {
            case BASIC:
                BasicChest.file().set(header, null);
                for (ItemStack item : itens) {
                    BasicChest.file().set(header + i + ".ItemStack", item);
                    BasicChest.file().set(header + i + ".Chance", defaultChance);
                    ++i;
                }
                BasicChest.save();
                break;
            case NORMAL:
                NormalChest.file().set(header, null);
                for (ItemStack item : itens) {
                    NormalChest.file().set(header + i + ".ItemStack", item);
                    NormalChest.file().set(header + i + ".Chance", defaultChance);
                    ++i;
                }
                NormalChest.save();
                break;
            case EPIC:
                EPICChest.file().set(header, null);
                for (ItemStack item : itens) {
                    EPICChest.file().set(header + i + ".ItemStack", item);
                    EPICChest.file().set(header + i + ".Chance", defaultChance);
                    ++i;
                }
                EPICChest.save();
                break;
        }
    }

    public List<SWChestItem> getChest(SWChest.Tier t, Boolean mid) {
        ArrayList<SWChestItem> ret = new ArrayList<>();
        String header = "Itens.";
        if (mid) {
            header = "Mid." + header;
        }
        switch (t) {
            case BASIC:
                for (String string : BasicChest.file().getConfigurationSection(header).getKeys(false)) {
                    ItemStack item = BasicChest.file().getItemStack(header + string + ".ItemStack");
                    int f = BasicChest.file().getInt(header + string + ".Chance");

                    ret.add(new SWChestItem(item, f, string));
                }
                break;
            case NORMAL:
                for (String string : NormalChest.file().getConfigurationSection(header).getKeys(false)) {
                    ItemStack item = NormalChest.file().getItemStack(header + string + ".ItemStack");
                    int f = NormalChest.file().getInt(header + string + ".Chance");

                    ret.add(new SWChestItem(item, f, string));
                }
                break;
            case EPIC:
                for (String string : EPICChest.file().getConfigurationSection(header).getKeys(false)) {
                    ItemStack item = EPICChest.file().getItemStack(header + string + ".ItemStack");
                    int f = EPICChest.file().getInt(header + string + ".Chance");

                    ret.add(new SWChestItem(item, f, string));
                }
                break;
        }
        return ret;
    }

    public int getMaxItems(SWChest.Tier chestTier) {
        String itens = "Max-Itens-Per-Chest";
        switch (chestTier) {
            case BASIC:
                return BasicChest.file().getInt(itens);
            case NORMAL:
                return NormalChest.file().getInt(itens);
            case EPIC:
                return EPICChest.file().getInt(itens);
        }
        return 0;
    }

    public void setChance(SWChest.Tier ct, SWChest.Type cte, String key, int val) {
        String header = "Itens.";
        if (cte == SWChest.Type.MID) {
            header = "Mid." + header;
        }
        switch (ct) {
            case BASIC:
                BasicChest.file().set(header + key + ".Chance", val);
                BasicChest.save();
                break;
            case NORMAL:
                NormalChest.file().set(header + key + ".Chance", val);
                NormalChest.save();
                break;
            case EPIC:
                EPICChest.file().set(header + key + ".Chance", val);
                EPICChest.save();
                break;
        }
    }
}
