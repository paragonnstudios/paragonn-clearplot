package net.elicodes.clearplot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class LocationSerializer {

    public static String getSerializedLocation(Location loc) { //Converts location -> String
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getName();
    }

    public static Location getDeserializedLocation(String s) {//Converts String -> Location
        String [] parts = s.split(";"); //If you changed the semicolon you must change it here too
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        World w = Bukkit.getServer().getWorld(parts[3]);
        return new Location(w, x, y, z, 0, 0); //can return null if the world no longer exists
    }
}
