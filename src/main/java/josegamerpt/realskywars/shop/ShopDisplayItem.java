package josegamerpt.realskywars.shop;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShopDisplayItem {

    private final Map<String, Object> info = new HashMap<>();
    private String name, displayName;
    private Material m;
    private Double price;
    private Boolean bought = false;
    private String permission;
    private Boolean interactive;
    private ShopManager.Categories it;

    public ShopDisplayItem(String name, String displayName, Material ma, Double per, Boolean b, String perm, ShopManager.Categories t) {
        this.name = name;
        this.displayName = displayName;
        this.price = per;
        this.bought = b;
        this.permission = perm;
        this.interactive = true;
        this.it = t;
        this.m = ma;
    }

    public ShopDisplayItem() {
        this.interactive = false;
    }

    public void addInfo(String a, Object b) {
        info.put(a, b);
    }


    private String formatName(String name) {
        String ret;
        try {
            Material m = Material.getMaterial(Text.strip(name));
            ret = "&b" + WordUtils.capitalizeFully(m.name().replace("_", " "));
        } catch (Exception e) {
            ret = name;
        }
        ret = "&r&f" + ret;
        return ret;
    }

    public Object getInfo(String s) {
        return info.get(s);
    }

    public boolean containsInfo(String randomBlock) {
        return info.containsKey(randomBlock);
    }

    public boolean isBought() {
        return this.bought;
    }

    public void setBought(boolean b) {
        this.bought = b;
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    public void setInteractive(boolean b) {
        this.interactive = b;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setName(String s) {
        this.name = s;
    }

    public Double getPrice() {
        return this.price;
    }

    public ItemStack getItemStack() {
        if (!this.interactive) {
            return Itens.createItem(Material.BUCKET, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SEARCH_NOTFOUND_NAME));
        }

        if (this.it == ShopManager.Categories.KITS) {
            Kit k = RealSkywars.getPlugin().getKitManager().getKit(name);

            return this.bought ? Itens.createItemLoreEnchanted(m, 1, "&r&f" + k.getDisplayName(), k.getDescription(false)) : Itens.createItemLore(m, 1, "&r&f" + k.getDisplayName(), k.getDescription(true));
        } else {
            return this.bought ? Itens.createItemLoreEnchanted(m, 1, formatName(this.getDisplayName()), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SHOP_BOUGHT))) : Itens.createItemLore(m, 1, formatName(this.getDisplayName()), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SHOP_BUY).replace("%price%", this.getPrice().toString())));
        }
    }

    public Material getMaterial() {
        return this.m;
    }
}
