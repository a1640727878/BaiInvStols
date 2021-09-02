package bai.bukkit.bis.configs;

import bai.bukkit.bis.BaiInvStols;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class BConfigManager {
    public static final Path Default_Paths = BaiInvStols.getInstance().getDataFolder().toPath();

    public static final Path Config_Path = Default_Paths.resolve("config.yml");
    public static final Path Items_Path = Default_Paths.resolve("items.yml");

    public static final YamlConfiguration Config = new YamlConfiguration();
    public static final YamlConfiguration Items_Config = new YamlConfiguration();

    public static Items_Config items_config;
    public static Main_Config main_config;

    public static Boolean debug = false;

    private static void copy_file(String name, Path path) {
        InputStream is = BaiInvStols.class.getClassLoader().getResourceAsStream(name);
        try {
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            BaiInvStols.info("配置文件不存在...");
        }
    }

    public static void load_config() {
        Default_Paths.toFile().mkdirs();
        set_config();
        set_items_config();

        items_config = bai.bukkit.bis.configs.Items_Config.reset(Items_Config);
        main_config = Main_Config.reset(Config);
    }

    private static void set_config() {
        try {
            if (!Config_Path.toFile().exists()) {
                Config_Path.toFile().createNewFile();
                copy_file("config.yml", Config_Path);
            }
            Config.load(Config_Path.toFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void set_items_config() {
        try {
            if (!Items_Path.toFile().exists()) {
                Items_Path.toFile().createNewFile();
                copy_file("items.yml", Items_Path);
            }
            Items_Config.load(Items_Path.toFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
