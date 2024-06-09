package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;

import java.util.List;

public abstract class ShopManagerAPI {
    public abstract List<RSWShopDisplayItem> getCategoryContents(RSWPlayer p, ShopCategory t);

    public enum ShopCategory {
        CAGE_BLOCKS, BOW_PARTICLES, KITS, WIN_BLOCKS, SPEC_SHOP;

        public String getCategoryConfigName() {
            switch (this) {
                case CAGE_BLOCKS:
                    return "Cage-Blocks";
                case WIN_BLOCKS:
                    return "Win-Blocks";
                case BOW_PARTICLES:
                    return "Bow-Particles";
                default:
                    return "err?";
            }
        }

        public String getCategoryTitle(RSWPlayer p) {
            switch (this) {
                case KITS:
                    return TranslatableLine.KITS.get(p);
                case BOW_PARTICLES:
                    return TranslatableLine.BOWPARTICLE.get(p);
                case WIN_BLOCKS:
                    return TranslatableLine.WINBLOCK.get(p);
                case CAGE_BLOCKS:
                    return TranslatableLine.CAGEBLOCK.get(p);
                case SPEC_SHOP:
                    return TranslatableLine.MENU_SPECTATOR_SHOP_TITLE.get(p);
                default:
                    return "? not found";
            }
        }
    }
}
