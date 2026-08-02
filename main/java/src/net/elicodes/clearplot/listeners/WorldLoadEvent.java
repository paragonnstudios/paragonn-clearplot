package net.elicodes.clearplot.listeners;

import com.intellectualcrafters.plot.object.Plot;
import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.runnables.ClearTask;
import net.elicodes.clearplot.utils.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorldLoadEvent implements Listener {

    @EventHandler
    void evento(org.bukkit.event.world.WorldLoadEvent e) {
        try {
            PreparedStatement stm = Main.getPlugin().database.getConnection().prepareStatement(
                    "select * from clearplot where world = ?"
            );
            stm.setString(1, e.getWorld().getName());

            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                String playerName = rs.getString("player");
                Location loc = LocationSerializer.getDeserializedLocation(rs.getString("plotLoc"));
                Plot plot = Main.getPlugin().manager.getPlotAPI().getPlot(loc);
                boolean actionbar = rs.getBoolean("actionbar");
                boolean remove = rs.getBoolean("remove");

                ClearTask clearTask = new ClearTask(plot, playerName, loc.getWorld());
                clearTask.setActionbar(actionbar);
                clearTask.setRemoveItem(remove);
                clearTask.init();
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
