package net.elicodes.clearplot.repository.providers;

import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.repository.Database;
import org.bukkit.Bukkit;

import java.sql.*;

public class MySQL implements Database {

    private Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void open() {
        String host = Main.getPlugin().config.getString("MySQL.host");
        String user = Main.getPlugin().config.getString("MySQL.usuario");
        String password = Main.getPlugin().config.getString("MySQL.senha");
        String database = Main.getPlugin().config.getString("MySQL.database");
        String url = "jdbc:mysql://" + host + "/" + database + "?autoReconnect=true";

        try {
            connection = DriverManager.getConnection(url, user, password);
            createTable();
            Bukkit.getConsoleSender().sendMessage("§2[HexagonClearPlot] §aConexão com MySQL estabelecida com sucesso.");
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage("§4[HexagonClearPlot] §cHouve um erro ao conectar-se com o MySQL!");
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void createTable() {
        try {
            PreparedStatement stm = this.connection.prepareStatement(
                    "create table if not exists clearplot(`player` TEXT NOT NULL, " +
                            "`plotLoc` TEXT NOT NULL, " +
                            "`actionbar` BOOLEAN NOT NULL, " +
                            "`world` TEXT NOT NULL, " +
                            "`remove` BOOLEAN NOT NULL)");
            stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}