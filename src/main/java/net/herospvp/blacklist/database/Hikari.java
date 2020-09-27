package net.herospvp.blacklist.database;

import me.gud.utils.Core;
import me.gud.utils.SQL;
import net.herospvp.blacklist.Conf;
import net.herospvp.blacklist.Main;
import net.herospvp.heroscore.utils.builders.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Hikari {

    private static final Message.Builder builder = Main.getBuilder();

    public static void initializeDatabase() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {

            connection = SQL.connection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + Conf.databaseTable +
                    " (username CHAR(16) NOT NULL, executor CHAR(16) NOT NULL, reason CHAR(64), PRIMARY KEY(username));");
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            SQL.close(connection, preparedStatement);
        }
    }

    public static void storeAllData() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {

            connection = SQL.connection();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + Conf.databaseTable);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String playerName = resultSet.getString(1), reason = resultSet.getString(3);
                Store.add(playerName, reason);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            SQL.close(connection, preparedStatement, resultSet);
        }
    }

    public static void add(CommandSender commandSender, String playerName, String reason) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {

            connection = SQL.connection();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + Conf.databaseTable + " WHERE username = "
                    + SQL.format(playerName));
            resultSet = preparedStatement.executeQuery();

            String executor = commandSender.getName();
            
            if (!resultSet.next()) {

                if (reason == null) reason = "Non specificato";

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equals(playerName)) {
                        Bukkit.getScheduler().runTask(Main.getInstance(),
                                () -> player.kickPlayer(Core.translatedString(player.getName(), "blacklist-kick")));
                    }
                    builder.sendMessage(player, Core.translatedString(executor, "addato")
                            .replace("%player%", playerName).replace("%exec%", executor)
                            .replace("%reason%", reason), Message.Builder.Result.OK);
                }

                if (!(commandSender instanceof Player)) {
                    builder.sendMessage(commandSender, Core.getItalianString("addato")
                            .replace("%player%", playerName).replace("%exec%", executor)
                            .replace("%reason%", reason), Message.Builder.Result.OK);
                }

                preparedStatement = connection.prepareStatement("INSERT INTO " + Conf.databaseTable + " VALUES ("
                        + SQL.format(playerName) + ", " + SQL.format(executor) + ", " + SQL.format(reason) + ")");
                preparedStatement.executeUpdate();

                Store.add(playerName, reason);

            } else {
                if (commandSender instanceof Player) {
                    builder.sendMessage(commandSender, Core.translatedString(executor, "addato-errore")
                            .replace("%player%", playerName), Message.Builder.Result.ERR);
                } else {
                    builder.sendMessage(commandSender, Core.getItalianString("addato-errore")
                            .replace("%player%", playerName), Message.Builder.Result.OK);
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            SQL.close(connection, preparedStatement, resultSet);
        }
    }

    public static void remove(CommandSender commandSender, String playerName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {

            String executor = commandSender.getName();

            connection = SQL.connection();
            preparedStatement = connection.prepareStatement("DELETE FROM " + Conf.databaseTable + " WHERE username = "
                    + SQL.format(playerName));
            preparedStatement.executeUpdate();

            Store.remove(playerName);

            if (commandSender instanceof Player) {
                builder.sendMessage(commandSender, Core.translatedString(executor, "rimosso")
                        .replace("%player%", playerName), Message.Builder.Result.OK);
            } else {
                builder.sendMessage(commandSender, Core.getItalianString("rimosso")
                        .replace("%player%", playerName), Message.Builder.Result.OK);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            SQL.close(connection, preparedStatement);
        }
    }

}
