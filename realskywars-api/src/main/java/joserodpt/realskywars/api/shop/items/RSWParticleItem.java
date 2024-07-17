package joserodpt.realskywars.api.shop.items;

import joserodpt.realskywars.api.shop.RSWBuyableItem;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Map;

public class RSWParticleItem extends RSWBuyableItem {

    public RSWParticleItem(String configKey, String displayName, Material material, Double price, String permission, String particleName) {
        super(configKey, displayName, material, price, permission, ItemCategory.BOW_PARTICLE, Map.of("Particle", particleName));
    }

    public Particle getParticle() {
        return Particle.valueOf((String) this.getExtrasMap().get("Particle"));
    }
}
