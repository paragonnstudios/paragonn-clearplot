package net.elicodes.clearplot.runnables;

import com.boydti.fawe.util.*;
import com.boydti.fawe.bukkit.wrapper.*;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.manager.CPManager;
import net.elicodes.clearplot.utils.ActionBar;
import net.elicodes.clearplot.utils.ConfigHelper;
import net.elicodes.clearplot.utils.NumberFormatter;
import net.elicodes.clearplot.utils.ProgressUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.scheduler.CraftTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClearTask {

    private OfflinePlayer player;

    private Plot plot;
    private Location initLoc;
    private org.bukkit.Location loc;

    private boolean actionbar;

    private double zMax;
    private double xMax;

    private double zMin;
    private double xMin;

    private double currentX;
    private double currentY;
    private double currentZ;

    private double blocks;
    private World w;

    private double initialY = 1;
    private boolean disponivel = false;
    private boolean removeItem = true;

    private CPManager manager;
    private int taskID;

    public ClearTask(Plot plot, String playerName, World w) {
        this.manager = Main.getPlugin().manager;

        this.plot = plot;
        this.initLoc = new Location(w.getName(), plot.getTop().getX(), PS.get().getPlotManager(plot).getSignLoc(plot.getArea(), plot).clone().getY(), plot.getTop().getZ());
        this.loc = new org.bukkit.Location(w, initLoc.getX(), initLoc.getY(), initLoc.getZ(), initLoc.getYaw(), initLoc.getPitch());
        this.player = Bukkit.getOfflinePlayer(playerName);

        this.w = w;
        this.actionbar = true;

        Location bottom = plot.getExtendedBottomAbs();
        Location top = plot.getExtendedTopAbs();

        xMax = top.getX();
        xMin = bottom.getX();

        zMax = top.getZ();
        zMin = bottom.getZ();

        currentX = xMin;
        currentY = Main.getPlugin().manager.getTopLocation(plot).getY();
        currentZ = zMin;

        this.blocks = (Main.getPlugin().config.getDouble("Region.Base") * Main.getPlugin().config.getDouble("Region.Perimetro")) * currentY;
    }

    public void init() {
        TaskManager.IMP.later(() -> {
            for (double cx = 0; cx < blocks; cx++) {
                Block b = w.getBlockAt((int) currentX, (int) currentY, (int) currentZ);
                if (b.getType() != Material.AIR && !manager.getBlocksBlackList().contains(b.getType())) {
                    initialY = currentY;
                    disponivel = true;
                    break;
                }
                ++currentX;
                if (currentX > xMax) {
                    currentX = xMin;
                    ++currentZ;
                    if (currentZ > zMax) {
                        currentZ = zMin;
                        --currentY;
                        if (currentY == 0) {
                            disponivel = false;
                            break;
                        }
                    }
                }
                cx++;
            }

            if (disponivel) {
                if (removeItem) {
                    if (player.isOnline()) {
                        Player onlinePlayer = player.getPlayer();
                        if (onlinePlayer.getItemInHand().getAmount() == 0) return;
                        if (onlinePlayer.getItemInHand().getAmount() > 1) {
                            onlinePlayer.getItemInHand().setAmount(onlinePlayer.getItemInHand().getAmount() - 1);
                        } else onlinePlayer.setItemInHand(null);
                        setRemoveItem(false);
                    }
                }
                Main.getPlugin().manager.getCleaning().put(player.getName().toLowerCase(), this);
                for(String line : ConfigHelper.msgList("loading_process")) sendMessage(line);
                currentY = initialY;
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
                    final List<Block> blockList = new ArrayList<>();
                    double cx = 0;

                    public void run() {
                        if (cx <= blocks) {
                            Block b = w.getBlockAt((int) currentX, (int) currentY, (int) currentZ);
                            if (b != null && b.getType() != Material.AIR) {
                                if (!manager.getBlocksBlackList().contains(b.getType())) {
                                    blockList.add(b);
                                    b.setType(Material.AIR);
                                }
                                String progressText = ProgressUtil.getProgressPorcentage(blockList.size(), blocks);
                                int restantes = (int) (blocks - blockList.size());
                                if (player.isOnline()) {
                                    if (manager.getPlotAPI().isPlotWorld(player.getPlayer().getWorld()) && isActionbar()) {
                                        ActionBar.send(player.getPlayer(), ConfigHelper.msg("actionbar.cleaning").replace("@blocos", NumberFormatter.formatNumber(restantes)).replace("@progress", progressText));
                                    }
                                }
                            }
                            ++currentX;
                            if (currentX > xMax) {
                                currentX = xMin;
                                ++currentZ;
                                if (currentZ > zMax) {
                                    currentZ = zMin;
                                    --currentY;
                                    if (currentY == 0) {
                                        if (player.isOnline()) {
                                            ActionBar.send(player.getPlayer(), ConfigHelper.msg("actionbar.sucess_clean"), 2);
                                            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                                        }
                                        for(String line : ConfigHelper.msgList("sucess_clean")) sendMessage(line);
                                        ClearTask.this.stop();
                                    }
                                }
                            }
                            cx++;
                        } else Bukkit.getScheduler().cancelTask(taskID);
                    }
                }, 0L, 1L);
            } else {
                for(String line : ConfigHelper.msgList("no_have_blocks")) sendMessage(line);
                if (player.isOnline()) {
                    ActionBar.send(player.getPlayer(), ConfigHelper.msg("actionbar.no_have_blocks"), 2);
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.VILLAGER_NO, 1, 1);
                }
            }
        }, 1);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        this.manager.getCleaning().remove(player.getName().toLowerCase());
        if (Main.getPlugin().manager.contains(this.player.getName().toLowerCase())) {
            try {
                PreparedStatement stm = Main.getPlugin().database.getConnection().prepareStatement(
                        "delete from clearplot where player = ?"
                );

                stm.setString(1, player.getName().toLowerCase());
                stm.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendMessage(String message) {
        if (player.isOnline()) {
            player.getPlayer().sendMessage(message);
        }
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public boolean isRemoveItem() {
        return removeItem;
    }

    public void setRemoveItem(boolean removeItem) {
        this.removeItem = removeItem;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
    }

    public Plot getPlot() {
        return plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public org.bukkit.Location getLoc() {
        return loc;
    }

    public void setLoc(org.bukkit.Location loc) {
        this.loc = loc;
    }

    public Location getInitLoc() {
        return initLoc;
    }

    public void setInitLoc(Location initLoc) {
        this.initLoc = initLoc;
    }

    public boolean isActionbar() {
        return actionbar;
    }

    public void setActionbar(boolean actionbar) {
        this.actionbar = actionbar;
    }

    public World getW() {
        return w;
    }

    public void setW(World w) {
        this.w = w;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
}

