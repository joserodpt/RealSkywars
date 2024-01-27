package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;

import java.util.List;

public abstract class ShopManagerAPI {
    public abstract List<RSWShopDisplayItem> getCategoryContents(RSWPlayer p, Categories t);

    public enum Categories {
        CAGE_BLOCKS, BOW_PARTICLES, KITS, WIN_BLOCKS, SPEC_SHOP
    }
}
