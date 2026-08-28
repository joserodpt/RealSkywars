package joserodpt.realskywars.api.map;

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
 * @author José Rodrigues © 2019-2026
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RSWMapEvent {

    private final EventType eventType;
    private final RSWMap room;
    private int time;

    public RSWMapEvent(RSWMap room, EventType eventType, int time) {
        this.room = room;
        this.eventType = eventType;
        this.time = time;
    }

    public RSWMapEvent(RSWMap map, EventType eventType) {
        this(map, eventType, 30);
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String getName() {
        return Text.color(this.eventType.getName() + " " + Text.formatSeconds(this.getTimeLeft()));
    }

    public int getTimeLeft() {
        return (this.room.getMaxGameTime() - (this.room.getMaxGameTime() - this.getTime())) - this.room.getTimePassed();
    }

    public void tick() {
        if (this.getTimeLeft() == 0) {
            execute();
            this.room.getEvents().remove(this);
        }
    }

    public int getTime() {
        return this.time;
    }

    public void execute() {
        switch (this.eventType) {
            case REFILL:
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle(TranslatableList.REFILL_EVENT_TITLE.get(rswPlayer).get(0), TranslatableList.REFILL_EVENT_TITLE.get(rswPlayer).get(1), 4, 10, 4));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.BLOCK_CHEST_LOCKED, 50, 50));
                break;
            case TNTRAIN:
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle(TranslatableList.TNTRAIN_EVENT_TITLE.get(rswPlayer).get(0), TranslatableList.TNTRAIN_EVENT_TITLE.get(rswPlayer).get(1), 4, 10, 4));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_TNT_PRIMED, 50, 50));
                this.room.getPlayers().forEach(player -> player.spawnAbovePlayer(TNTPrimed.class));
                break;
            case LUCKYBLOCK_SPAWN:
                executeLuckyBlockSpawn();
                break;
            case LUCKYBLOCK_RAIN:
                executeLuckyBlockRain();
                break;
            case LUCKYBLOCK_TREASURE:
                executeLuckyBlockTreasure();
                break;
            case BORDERSHRINK:
                this.room.getBossBar().setDeathMatch();

                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle("", TranslatableLine.TITLE_DEATHMATCH.get(rswPlayer), 10, 20, 5));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 50, 50));

                int factor = Math.max(1, RSWConfig.file().getInt("Config.Death-Match-Shrink-Factor", 2));

                this.room.getBorder().setSize((double) this.room.getBorderSize() / factor, 30L);
                this.room.getBorder().setCenter(this.room.getMapCuboid().getCenter());
                break;
        }
    }

    public String serialize() {
        return this.eventType.name() + "@" + this.time;
    }

    public ItemStack getItem() {
        return Itens.createItem(this.getEventType().getIcon(), 1, this.getEventType().getName() + " &r&f@ &b" + Text.formatSeconds(this.getTimeLeft()), Text.color(Arrays.asList("&a&nLeft-Click&r&f to edit", "&c&nQ (Drop)&r&f to remove")));
    }

    public void setTime(int seconds) {
        this.time = seconds;
    }

    /** The first configured lucky block, as a material. */
    private Material luckyBlockMaterial() {
        for (String configured : RSWConfig.file().getStringList("Config.LuckyBlock.Blocks")) {
            Material parsed = Material.matchMaterial(configured.trim().toUpperCase());
            if (parsed != null && parsed.isBlock()) {
                return parsed;
            }
        }
        return Material.SPONGE;
    }

    private void announce(TranslatableList title, Sound sound) {
        this.room.getAllPlayers().forEach(rswPlayer -> {
            List<String> lines = title.get(rswPlayer);
            if (lines.size() >= 2) {
                rswPlayer.sendTitle(lines.get(0), lines.get(1), 4, 10, 4);
            }
            rswPlayer.playSound(sound, 50, 50);
        });
    }

    /** Places lucky blocks on the surface around every alive player. */
    private void executeLuckyBlockSpawn() {
        announce(TranslatableList.LUCKYBLOCK_SPAWN_EVENT_TITLE, Sound.BLOCK_NOTE_BLOCK_PLING);

        Material lucky = luckyBlockMaterial();
        int amount = RSWConfig.file().getInt("Config.LuckyBlock.Events.Spawn-Amount", 5);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (RSWPlayer p : this.room.getPlayers()) {
            if (p.getPlayer() == null) {
                continue;
            }

            for (int i = 0; i < amount; i++) {
                Location target = p.getPlayer().getLocation().clone().add(random.nextInt(-8, 9), 0, random.nextInt(-8, 9));
                target.setY(this.room.getRSWWorld().getWorld().getHighestBlockYAt(target) + 1);

                if (target.getBlock().getType().isAir()) {
                    target.getBlock().setType(lucky);
                }
            }
        }
    }

    /** Drops lucky blocks from above every alive player. */
    private void executeLuckyBlockRain() {
        announce(TranslatableList.LUCKYBLOCK_RAIN_EVENT_TITLE, Sound.ENTITY_ENDER_DRAGON_FLAP);

        Material lucky = luckyBlockMaterial();
        int amount = RSWConfig.file().getInt("Config.LuckyBlock.Events.Rain-Amount", 15);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (RSWPlayer p : this.room.getPlayers()) {
            if (p.getPlayer() == null) {
                continue;
            }

            for (int i = 0; i < amount; i++) {
                Location target = p.getPlayer().getLocation().clone()
                        .add(random.nextInt(-6, 7), random.nextInt(8, 20), random.nextInt(-6, 7));

                Block below = target.clone().subtract(0, 1, 0).getBlock();
                if (target.getBlock().getType().isAir() && !below.getType().isAir()) {
                    target.getBlock().setType(lucky);
                    this.room.getRSWWorld().getWorld().spawnParticle(Particle.CLOUD, target.clone().add(.5, .5, .5), 5, .2, .2, .2, .01);
                }
            }
        }
    }

    /** Hands every alive player a lucky block to place where they want. */
    private void executeLuckyBlockTreasure() {
        announce(TranslatableList.LUCKYBLOCK_TREASURE_EVENT_TITLE, Sound.ENTITY_PLAYER_LEVELUP);

        List<String> configured = RSWConfig.file().getStringList("Config.LuckyBlock.Blocks");
        String id = configured.isEmpty() ? "SPONGE" : configured.get(0).trim();

        Map<String, Object> data = new HashMap<>();
        data.put(ItemStackSpringer.ItemCategories.MATERIAL.name(), id.toUpperCase().startsWith("ITEMSADDER:") ? id : id.toUpperCase());
        data.put(ItemStackSpringer.ItemCategories.AMOUNT.name(), 1);
        data.put(ItemStackSpringer.ItemCategories.NAME.name(), RSWConfig.file().getString("Config.LuckyBlock.Item-Name", "&e&lLucky Block"));

        ItemStack item = ItemStackSpringer.getItemDeSerialized(data);
        if (item == null) {
            return;
        }

        for (RSWPlayer p : this.room.getPlayers()) {
            if (p.getPlayer() == null) {
                continue;
            }
            p.getPlayer().getInventory().addItem(item.clone());
            p.getPlayer().getWorld().spawnParticle(Particle.TOTEM, p.getPlayer().getLocation().add(0, 1, 0), 30, .5, .5, .5, .1);
        }
    }

    public enum EventType {
        REFILL(Material.CHEST), TNTRAIN(Material.TNT), BORDERSHRINK(Material.SPAWNER),
        LUCKYBLOCK_SPAWN(Material.SPONGE), LUCKYBLOCK_RAIN(Material.SLIME_BLOCK), LUCKYBLOCK_TREASURE(Material.ENDER_CHEST);

        final Material icon;

        EventType(Material icon) {
            this.icon = icon;
        }

        public Material getIcon() {
            return this.icon;
        }

        public String getName() {
            String configured = RSWConfig.file().getString("Config.Languages.Strings.Events." + this.name());
            return Text.color(configured == null ? Text.beautifyEnumName(this.name()) : configured);
        }
    }
}
