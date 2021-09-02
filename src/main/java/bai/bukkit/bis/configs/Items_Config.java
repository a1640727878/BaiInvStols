package bai.bukkit.bis.configs;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Items_Config {
    private Map<String, ItemStack> items = new HashMap<>();
    YamlConfiguration config;

    private Items_Config() {
    }

    public static Items_Config reset(YamlConfiguration config) {
        Items_Config ic = new Items_Config();
        ic.config = config;
        for (String key : config.getKeys(false)) {
            Object obj = config.get(key);
            if (obj instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) obj;
                ic.items.put(key, itemStack);
            }
        }
        return ic;
    }

    public Set<String> getIteamNames(){
        return items.keySet();
    }

    public ItemStack get(String name){
        if (items.containsKey(name))
            return items.get(name);
        else
            return new ItemStack(Material.AIR);
    }

    public void add(String name,ItemStack itemStack){
        items.put(name,itemStack);
        config.set(name,itemStack);
    }

    public void save(){
        try {
            config.save(BConfigManager.Items_Path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
