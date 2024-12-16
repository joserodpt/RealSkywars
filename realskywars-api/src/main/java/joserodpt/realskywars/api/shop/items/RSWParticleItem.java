package joserodpt.realskywars.api.shop.items;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

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
