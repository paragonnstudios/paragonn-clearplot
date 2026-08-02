package net.elicodes.clearplot.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EC_Config {

    public EC_Config(JavaPlugin plugin, String nome) {
        this.plugin = plugin;
        setName(nome);
    }

    private JavaPlugin plugin;
    private String name;
    private File file;

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    private YamlConfiguration config;

    public void saveConfig() {
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        getConfig().options().copyDefaults(true);
    }

    public void saveDefaultConfig() {
        getPlugin().saveResource(getName(), false);
    }

    public void reloadConfig() {
        file = new File(getPlugin().getDataFolder(), getName());
        config = YamlConfiguration.loadConfiguration(getFile());
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(plugin.getResource(getName()), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            if (!file.exists()) {
                config.setDefaults(defConfig);
                config.options().copyDefaults(true);
                try {
                    config.save(getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteConfig() {
        getFile().delete();
    }

    public boolean existeConfig() {
        return getFile().exists();
    }

    public String getString(String path) {
        return getConfig().getString(path);
    }

    public short getShort(String path) {
        return (short) getConfig().getInt(path);
    }

    public int getInt(String path) {
        return getConfig().getInt(path);
    }

    public boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public double getDouble(String path) {
        return getConfig().getDouble(path);
    }

    public List<?> getList(String path) {
        return getConfig().getList(path);
    }

    public boolean contains(String path) {
        return getConfig().contains(path);
    }

    public void set(String path, Object value) {
        getConfig().set(path, value);
    }

    public List<String> getStringList(String path) {
        return getConfig().getStringList(path);
    }

    public List<Integer> getIntegerList(String path) {
        return getConfig().getIntegerList(path);
    }

    public List<Double> getDoubleList(String path) {
        return getConfig().getDoubleList(path);
    }

    public List<Boolean> getBooleanList(String path) {
        return getConfig().getBooleanList(path);
    }

    public List<Byte> getByteList(String path) {
        return getConfig().getByteList(path);
    }

    public List<Character> getCharacterList(String path) {
        return getConfig().getCharacterList(path);
    }

    public List<Long> getLongList(String path) {
        return getConfig().getLongList(path);
    }

    public List<Short> getShortList(String path) {
        return getConfig().getShortList(path);
    }

    public List<Map<?, ?>> getMapList(String path) {
        return getConfig().getMapList(path);
    }

    public List<?> getList(String path, List<?> def) {
        return getConfig().getList(path, def);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return getConfig().getConfigurationSection(path);
    }
}
