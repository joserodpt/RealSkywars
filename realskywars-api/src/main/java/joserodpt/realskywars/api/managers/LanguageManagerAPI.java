package joserodpt.realskywars.api.managers;

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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.config.RSWLanguage;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class LanguageManagerAPI {
    protected final Map<String, RSWLanguage> langList = new HashMap<>();

    public abstract void loadLanguages();

    public abstract String getDefaultLanguage();

    public abstract boolean areLanguagesEmpty();

    public abstract Collection<RSWLanguage> getLanguages();

    public abstract Map<String, RSWLanguage> getLanguagesMap();

    public abstract String getPrefix();

    public abstract String getMaterialName(RSWPlayer p, Material mat);

    public abstract String getMaterialName(Material mat);

    public abstract String getEnchantmentName(RSWPlayer p, Enchantment ench);

    public abstract String getEntityName(RSWPlayer p, EntityType type);

    public abstract RSWLanguage getLanguage(String language);
}
