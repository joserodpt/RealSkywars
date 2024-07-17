package joserodpt.realskywars.api.shop.items;

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RSWSpectatorShopItem extends RSWBuyableItem {

    public RSWSpectatorShopItem(String configKey, String displayName, Material material, Double price, String permission) {
        super(configKey, displayName, material, price, permission, ItemCategory.SPEC_SHOP);
    }

    @Override
    public ItemStack getIcon(RSWPlayer p) {
        return Itens.createItem(this.getMaterial(), this.getAmount(), "&f" + this.getAmount() + "x " + this.getDisplayName(), Arrays.asList(TranslatableLine.SHOP_CLICK_2_BUY.get(p).replace("%price%", this.getPrice().toString()), "", "&a&nF (Swap hand)&r&f to increase the item amount.", "&c&nQ (Drop)&r&f to decrease the item amount."));
    }

}
