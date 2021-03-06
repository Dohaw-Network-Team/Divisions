package net.dohaw.play.divisions.events.custom;

import lombok.Getter;
import net.dohaw.play.divisions.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HeatingUpCriticalStrikeEvent extends Event implements Cancellable {

    private boolean isCancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Getter
    private PlayerData playerData;

    public HeatingUpCriticalStrikeEvent(PlayerData pd){
        this.playerData = pd;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
