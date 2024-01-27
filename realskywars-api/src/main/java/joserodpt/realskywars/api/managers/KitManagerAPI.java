package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.kits.RSWKit;

import java.util.List;

public abstract class KitManagerAPI {
    public abstract void loadKits();

    public abstract void registerKit(RSWKit k);

    public abstract void unregisterKit(RSWKit k);

    public abstract List<RSWKit> getKits();

    public abstract RSWKit getKit(String string);
}
