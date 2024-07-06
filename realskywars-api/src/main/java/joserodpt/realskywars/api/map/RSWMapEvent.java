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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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
            case BORDERSHRINK:
                this.room.getBossBar().setDeathMatch();

                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle("", TranslatableLine.TITLE_DEATHMATCH.get(rswPlayer), 10, 20, 5));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 50, 50));

                this.room.getBorder().setSize((double) this.room.getBorderSize() / 2, 30L);
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

    public enum EventType {
        REFILL(Material.CHEST), TNTRAIN(Material.TNT), BORDERSHRINK(Material.SPAWNER);

        final Material icon;

        EventType(Material icon) {
            this.icon = icon;
        }

        public Material getIcon() {
            return this.icon;
        }

        public String getName() {
            return Text.color(RSWLanguagesConfig.file().getString("Strings.Events." + this.name()));
        }
    }
}
