package bai.bukkit.bis;

import bai.bukkit.bis.commands.BCommandManager;
import bai.bukkit.bis.configs.BConfigManager;
import bai.bukkit.bis.listeners.BListenerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BaiInvStols extends JavaPlugin {

    private static BaiInvStols instance;

    public static BaiInvStols getInstance() {
        return instance;
    }

    public static void setInstance(BaiInvStols instance) {
        BaiInvStols.instance = instance;
    }

    public static void info(Object obj) {
        instance.getLogger().info(obj.toString());
    }

    public static void debug(Object obj) {
        if (BConfigManager.debug)
            info(obj);
    }

    @Override
    public void onLoad() {
        setInstance(this);
    }

    @Override
    public void onEnable() {
        BConfigManager.load_config();
        BListenerManager.register();
        BCommandManager.register();

    }

    @Override
    public void onDisable() {

    }
}
