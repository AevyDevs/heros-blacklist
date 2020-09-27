package net.herospvp.blacklist.commands;

import me.gud.utils.Core;
import net.herospvp.blacklist.Main;
import net.herospvp.blacklist.database.Hikari;
import net.herospvp.blacklist.database.Store;
import net.herospvp.heroscore.utils.builders.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class BlacklistCommand implements CommandExecutor {

    private static final Message.Builder builder = Main.getBuilder();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (builder.checkPermission(commandSender, "blacklist")) {
            switch (strings.length) {
                case 1: {
                    String category = strings[0].toLowerCase();
                    if (category.equals("list")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        AtomicInteger x = new AtomicInteger();
                        Store.getStoredPlayers().forEach((s1, s2) -> {
                            x.getAndIncrement();
                            s1 = "&c" + s1;
                            s2 = "&c" + s2;
                            stringBuilder.append(s1).append(" &7» ").append(s2).append("\n");
                        });

                        if (commandSender instanceof Player) {
                            builder.sendMessage(commandSender,
                                    Core.translatedString(commandSender.getName(), "lista")
                                            .replace("%tot%", String.valueOf(x)) + "\n\n" + stringBuilder);
                        } else {
                            builder.sendMessage(commandSender,
                                    Core.getItalianString("lista")
                                            .replace("%tot%", String.valueOf(x)) + "\n\n" + stringBuilder);
                        }
                    } else {
                        builder.printHelpMessage(commandSender);
                    }
                    break;
                }
                case 2: {
                    String category = strings[0].toLowerCase();
                    switch (category) {
                        case "remove": {
                            String playerName = strings[1];
                            if (Store.containsKey(playerName)) {
                                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
                                        () -> Hikari.remove(commandSender, playerName));
                            } else {
                                if (commandSender instanceof Player) {
                                    builder.sendMessage(commandSender, Core.translatedString(commandSender.getName(), "check-non-presente")
                                            .replace("%player%", playerName), Message.Builder.Result.ERR);
                                } else {
                                    builder.sendMessage(commandSender, Core.getItalianString("check-non-presente")
                                            .replace("%player%", playerName), Message.Builder.Result.ERR);
                                }
                            }
                            break;
                        }
                        case "check": {
                            String playerName = strings[1];
                            String string = Store.containsKey(playerName) ? "check-presente" : "check-non-presente";
                            if (commandSender instanceof Player) {
                                builder.sendMessage(commandSender, Core.translatedString(commandSender.getName(), string)
                                        .replace("%player%", playerName), Message.Builder.Result.ERR);
                            } else {
                                builder.sendMessage(commandSender, Core.getItalianString(string)
                                        .replace("%player%", playerName), Message.Builder.Result.ERR);
                            }
                            break;
                        }
                        case "add" : {
                            String playerName = strings[1];
                            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
                                    () -> Hikari.add(commandSender, playerName, null));
                            break;
                        }
                        default:
                            builder.printHelpMessage(commandSender);
                            break;
                    }
                    break;
                }
                case 3: {
                    String category = strings[0].toLowerCase();
                    if (category.equals("add")) {
                        String playerName = strings[1], reason = null;
                        StringBuilder stringBuilder = new StringBuilder();
                        try {
                            int i = 0;
                            for (String string : strings) {
                                i++;
                                if (i < 2) continue;
                                stringBuilder.append(" ").append(string);
                            }
                            reason = stringBuilder.toString();
                        } catch (IndexOutOfBoundsException ignored) {}

                        String finalReason = reason;
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
                                () -> Hikari.add(commandSender, playerName, finalReason));
                    } else {
                        builder.printHelpMessage(commandSender);
                    }
                    break;
                }
                default:
                    builder.printHelpMessage(commandSender);
                    break;
            }
        }
        return false;
    }

}
