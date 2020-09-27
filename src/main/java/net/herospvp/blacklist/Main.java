package net.herospvp.blacklist;

import lombok.Getter;
import me.gud.utils.Core;
import net.herospvp.blacklist.commands.BlacklistCommand;
import net.herospvp.blacklist.database.Hikari;
import net.herospvp.blacklist.monitor.Events;
import net.herospvp.heroscore.utils.builders.Message;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static Message.Builder builder;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Core.setPluginInstance(this);

        Message.addPlugin("BLACKLIST", Arrays.asList(
                "&c/bl add <player>&7\u2502 &fAggiungi <player> alla lista.",
                "&c/bl remove <player>&7\u2502 &fRimuovi <player> dalla lista.",
                "&c/bl check <player>&7\u2502 &fControlla se <player> e' nella lista.",
                "&c/bl list&7\u2502 &fLista dei players nella lista."));

        builder = Message.like("BLACKLIST");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Hikari.initializeDatabase();
            Hikari.storeAllData();
        });

        getServer().getPluginCommand("blacklist").setExecutor(new BlacklistCommand());
        getServer().getPluginManager().registerEvents(new Events(), this);

    }

    @Override
    public void onDisable() {
        saveConfig();
        Message.removePlugin("BLACKLIST");
    }

}
