package net.elicodes.clearplot.manager;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.runnables.ClearTask;
import net.elicodes.clearplot.utils.ConfigHelper;
import net.elicodes.clearplot.utils.EC_Config;
import net.elicodes.clearplot.utils.ItemHelper;
import net.elicodes.clearplot.utils.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CPManager {

    private ArrayList<Material> blocksBlackList;
    private HashMap<String, ClearTask> playersCleaning;

    private EC_Config config;
    private PlotAPI plotAPI;
    private ItemStack item;

    public CPManager(Main main) {
        this.plotAPI = new PlotAPI();
        this.config = main.config;
        this.playersCleaning = new HashMap<>();
        this.blocksBlackList = new ArrayList<>();

        this.item = ItemHelper.create(
                config.getString("CP-Item.skull-url"),
                config.getString("CP-Item.material"),
                config.getShort("CP-Item.data"),
                config.getString("CP-Item.name"),
                config.getStringList("CP-Item.lore"),
                config.getBoolean("CP-Item.glow"));
    }

    public void loadBlackList() {
        List<String> blocksList = config.getStringList("Blocks-BlackList");
        blocksList.forEach(line -> blocksBlackList.add(Material.valueOf(line)));
    }

    public void clearPlot(Player player) {
        World w = player.getWorld();
        Location loc = player.getLocation();
        if (w == null) return;
        if (!plotAPI.isPlotWorld(w)) {
            for(String line : ConfigHelper.msgList("not_in_plotworld")) player.sendMessage(line);
            return;
        }
        Plot plot = plotAPI.getPlot(loc);
        if (plot == null) {
            for(String line : ConfigHelper.msgList("no_terrain")) player.sendMessage(line);
            return;
        } else if (!plot.hasOwner()) {
            for(String line : ConfigHelper.msgList("no_owner")) player.sendMessage(line);
            return;
        } else if (!plot.isOwner(player.getUniqueId())) {
            for(String line : ConfigHelper.msgList("you_not_owner")) player.sendMessage(line);
            return;
        } else if (playersCleaning.containsKey(player.getName().toLowerCase())) {
            for(String line : ConfigHelper.msgList("clean_on")) player.sendMessage(line);
            return;
        }
        ClearTask task = new ClearTask(plot, player.getName(), w);
        task.setRemoveItem(true);
        task.init();
    }

    public boolean contains(String playerName) {
        try {
            PreparedStatement stm = Main.getPlugin().database.getConnection().prepareStatement(
                    "select * from clearplot where player = ?"
            );
            stm.setString(1, playerName);

            ResultSet rs = stm.executeQuery();
            return rs.next();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertOrUpdate(ClearTask clearTask) {
        try {
            if(contains(clearTask.getPlayer().getName())) {
                PreparedStatement stm = Main.getPlugin().database.getConnection().prepareStatement(
                        "update clearplot set plotLoc = ?, actionbar = ?, world = ?, remove = ? where player = ?"
                );
                Location loc = clearTask.getLoc();
                loc.setWorld(clearTask.getW());
                stm.setString(1, LocationSerializer.getSerializedLocation(loc));
                stm.setBoolean(2, clearTask.isActionbar());
                stm.setString(3, clearTask.getLoc().getWorld().getName());
                stm.setBoolean(4, clearTask.isRemoveItem());
                stm.setString(5, clearTask.getPlayer().getName());

                stm.executeUpdate();
                return;
            }
            PreparedStatement stm = Main.getPlugin().database.getConnection().prepareStatement(
                    "insert into clearplot(player, plotLoc, actionbar, world, remove) VALUES(?,?,?,?,?)"
            );

            stm.setString(1, clearTask.getPlayer().getName());
            stm.setString(2, LocationSerializer.getSerializedLocation(clearTask.getLoc()));
            stm.setBoolean(3, clearTask.isActionbar());
            stm.setString(4, clearTask.getW().getName());
            stm.setBoolean(5, clearTask.isRemoveItem());

            stm.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTasks() {
        for(ClearTask clearTask : playersCleaning.values()) {
            insertOrUpdate(clearTask);
        }
    }
    @Deprecated
    public com.intellectualcrafters.plot.object.Location getTopLocation(Plot plot) {
        return PS.get().getPlotManager(plot).getSignLoc(plot.getArea(), plot);
    }

    public HashMap<String, ClearTask> getCleaning() {
        return playersCleaning;
    }

    public ArrayList<Material> getBlocksBlackList() {
        return blocksBlackList;
    }

    public PlotAPI getPlotAPI() {
        return plotAPI;
    }

    public ItemStack getItem() {
        return item;
    }
}
