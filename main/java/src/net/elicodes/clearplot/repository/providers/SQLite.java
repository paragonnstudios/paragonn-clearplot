package net.elicodes.clearplot.repository.providers;

import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.repository.Database;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;

public class SQLite implements Database {

    private Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void open() {
        File file = new File(Main.getPlugin().getDataFolder(), "database.db");
        String url = "jdbc:sqlite:" + file;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            createTable();
            Bukkit.getConsoleSender().sendMessage("§2[HexagonClearPlot] §aConexão com SQLite estabelecida com sucesso.");
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("§4[HexagonClearPlot] §cHouve um erro ao conectar-se com o SQLite!");
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTable() {
        try {
            PreparedStatement stm = this.connection.prepareStatement(
                    "create table if not exists clearplot(`player` TEXT, " +
                            "`plotLoc` TEXT, " +
                            "`actionbar` BOOLEAN, " +
                            "`world` TEXT, " +
                            "`remove` BOOLEAN)"
            );
            stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}