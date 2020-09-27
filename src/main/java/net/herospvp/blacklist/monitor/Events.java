package net.herospvp.blacklist.monitor;

import net.herospvp.blacklist.database.Store;
import net.herospvp.heroscore.utils.builders.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class Events implements Listener {

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        if (Store.containsKey(playerName)) {
            event.setKickMessage(Message.Builder
                    .formatColor("&cYou are blacklisted!\n\n&7You can take the unblacklist from our store: &chttps://buy.herospvp.net"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

}
