package josegamerpt.realskywars.chests;

import josegamerpt.realskywars.configuration.chests.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChestManager {

    private int defaultChance = 50;
    private String header = "Itens.";
    private String itens = "Max-Itens-Per-Chest";

    public void set2Chest(ChestManager.TierType t, Boolean middle, List<ItemStack> itens) {
        switch (t) {
            case BASIC:
                if (middle) {
                    int i = 0;
                    BasicChestMiddle.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        BasicChestMiddle.file().set(header + i + ".ItemStack", item);
                        BasicChestMiddle.file().set(header + i + ".Chance", defaultChance);
                        i++;
                    }
                    BasicChestMiddle.save();
                } else {
                    int i = 0;
                    BasicChest.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        BasicChest.file().set(header + i + ".ItemStack", item);
                        BasicChest.file().set(header + i + ".Chance", defaultChance);
                        i++;
                    }
                    BasicChest.save();
                }
                break;
            case NORMAL:
                if (middle) {
                    int i = 0;
                    NormalChestMiddle.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        NormalChestMiddle.file().set(header + i + ".ItemStack", item);
                        NormalChestMiddle.file().set(header + i + ".Chance", defaultChance);
                        i++;
                    }
                    NormalChestMiddle.save();
                } else {
                    int i = 0;
                    NormalChest.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        NormalChest.file().set(header + i + ".ItemStack", item);
                        NormalChest.file().set(header + i + ".Chance", defaultChance);
                        i++;
                    }
                    NormalChest.save();
                }
                break;
            case OP:
                if (middle) {
                    int i = 0;
                    OPChestMiddle.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        OPChestMiddle.file().set(header + i + ".ItemStack", item);
                        OPChestMiddle.file().set(header + i + ".Chance", defaultChance);
                        i++;
                    }
                    OPChestMiddle.save();
                } else {
                    int i = 0;
                    OPChest.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        OPChest.file().set(header + i + ".ItemStack", item);
                        OPChest.file().set(header + i + ".Chance", defaultChance);
                        i++;
                    }
                    OPChest.save();
                }
                break;
            case CAOS:
                if (middle) {
                    int i = 0;
                    CAOSchestMiddle.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        CAOSchestMiddle.file().set(header + i + ".ItemStack", item);
                        CAOSchestMiddle.file().set(header + i + ".Chance", 50);
                        i++;
                    }
                    CAOSchestMiddle.save();
                } else {
                    int i = 0;
                    CAOSchest.file().set("Itens", null);
                    for (ItemStack item : itens) {
                        CAOSchest.file().set(header + i + ".ItemStack", item);
                        CAOSchest.file().set(header + i + ".Chance", 50);
                        i++;
                    }
                    CAOSchest.save();
                }
                break;
        }
    }

    public List<SWChestItem> getChest(ChestManager.TierType t, Boolean middle) {
        ArrayList<SWChestItem> retrn = new ArrayList<>();
        switch (t) {
            case BASIC:
                if (middle) {
                    for (String string : BasicChestMiddle.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = BasicChestMiddle.file().getItemStack(header + string + ".ItemStack");
                        int f = BasicChestMiddle.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                } else {
                    for (String string : BasicChest.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = BasicChest.file().getItemStack(header + string + ".ItemStack");
                        int f = BasicChest.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                }
                break;
            case NORMAL:
                if (middle) {
                    for (String string : NormalChestMiddle.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = NormalChestMiddle.file().getItemStack(header + string + ".ItemStack");
                        int f = NormalChestMiddle.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                } else {
                    for (String string : NormalChest.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = NormalChest.file().getItemStack(header + string + ".ItemStack");
                        int f = NormalChest.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                }
                break;
            case OP:
                if (middle) {
                    for (String string : OPChestMiddle.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = OPChestMiddle.file().getItemStack(header + string + ".ItemStack");
                        int f = OPChestMiddle.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                } else {
                    for (String string : OPChest.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = OPChest.file().getItemStack(header + string + ".ItemStack");
                        int f = OPChest.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                }
                break;
            case CAOS:
                if (middle) {
                    for (String string : CAOSchestMiddle.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = CAOSchestMiddle.file().getItemStack(header + string + ".ItemStack");
                        int f = CAOSchestMiddle.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                } else {
                    for (String string : CAOSchest.file().getConfigurationSection(header).getKeys(false)) {
                        ItemStack item = CAOSchest.file().getItemStack(header + string + ".ItemStack");
                        int f = CAOSchest.file().getInt(header + string + ".Chance");

                        retrn.add(new SWChestItem(item, f));
                    }
                }
                break;
        }
        return retrn;
    }

    public void add2Chest(ChestManager.TierType t, Boolean middle, List<ItemStack> tmp) {
        List<SWChestItem> itens = new ArrayList<>(getChest(t, middle));
        for (ItemStack itemStack : tmp) {
            itens.add(new SWChestItem(itemStack, 50));
        }

        switch (t) {
            case BASIC:
                if (middle) {
                    int i = 0;
                    BasicChestMiddle.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        BasicChestMiddle.file().set(header + i + ".ItemStack", item.getItemStack());
                        BasicChestMiddle.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    BasicChestMiddle.save();
                } else {
                    int i = 0;
                    BasicChest.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        BasicChest.file().set(header + i + ".ItemStack", item.getItemStack());
                        BasicChest.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    BasicChest.save();
                }
                break;
            case NORMAL:
                if (middle) {
                    int i = 0;
                    NormalChestMiddle.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        NormalChestMiddle.file().set(header + i + ".ItemStack", item.getItemStack());
                        NormalChestMiddle.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    NormalChestMiddle.save();
                } else {
                    int i = 0;
                    NormalChest.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        NormalChest.file().set(header + i + ".ItemStack", item.getItemStack());
                        NormalChest.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    NormalChest.save();
                }
                break;
            case OP:
                if (middle) {
                    int i = 0;
                    OPChestMiddle.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        OPChestMiddle.file().set(header + i + ".ItemStack", item.getItemStack());
                        OPChestMiddle.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    OPChestMiddle.save();
                } else {
                    int i = 0;
                    OPChest.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        OPChest.file().set(header + i + ".ItemStack", item.getItemStack());
                        OPChest.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    OPChest.save();
                }
                break;
            case CAOS:
                if (middle) {
                    int i = 0;
                    CAOSchestMiddle.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        CAOSchestMiddle.file().set(header + i + ".ItemStack", item.getItemStack());
                        CAOSchestMiddle.file().set(header + i + ".Chance", item.getItemStack());
                        i++;
                    }
                    CAOSchestMiddle.save();
                } else {
                    int i = 0;
                    CAOSchest.file().set("Itens", null);
                    for (SWChestItem item : itens) {
                        CAOSchest.file().set(header + i + ".ItemStack", item.getItemStack());
                        CAOSchest.file().set(header + i + ".Chance", item.getChance());
                        i++;
                    }
                    CAOSchest.save();
                }
                break;
        }
    }

    public int getMaxItems(ChestManager.TierType tierType, Boolean middle) {
        switch (tierType) {
            case BASIC:
                if (middle) {
                    return BasicChestMiddle.file().getInt(itens);
                } else {
                    return BasicChest.file().getInt(itens);
                }
            case NORMAL:
                if (middle) {
                    return NormalChestMiddle.file().getInt(itens);
                } else {
                    return NormalChest.file().getInt(itens);
                }
            case OP:
                if (middle) {
                    return OPChestMiddle.file().getInt(itens);
                } else {
                    return OPChest.file().getInt(itens);
                }
            case CAOS:
                if (middle) {
                    return CAOSchestMiddle.file().getInt(itens);
                } else {
                    return CAOSchest.file().getInt(itens);
                }
        }
        return 0;
    }

    public enum TierType {
        BASIC, NORMAL, OP, CAOS
    }
}
