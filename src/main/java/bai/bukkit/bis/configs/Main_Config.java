package bai.bukkit.bis.configs;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Main_Config {
    private final Set<Integer> Stol = new HashSet<>();
    private final Map<Integer, List<String>> lore_stol_map = new HashMap<>();
    private final Map<Player, Player_Unique> player_unique_map = new HashMap<>();

    public Map<Integer, String> Stols = new HashMap<>();
    public Map<String, Lore_Data> Lores = new HashMap<>();
    public Map<String, String> Mess = new HashMap<>();

    YamlConfiguration config;

    private Main_Config() {
    }

    public static Main_Config reset(YamlConfiguration config) {
        Main_Config mc = new Main_Config();
        mc.config = config;

        // Stols
        ConfigurationSection Stols_config = config.getConfigurationSection("Stols");
        for (String key : Stols_config.getKeys(false)) {
            mc.Stols.put(Integer.parseInt(key), Stols_config.getString(key));
        }

        // Lores
        ConfigurationSection Lores_config = config.getConfigurationSection("Lores");
        for (String key : Lores_config.getKeys(false)) {
            Boolean unique = Lores_config.getBoolean(key + ".unique");
            List<Integer> stols = Lores_config.getIntegerList(key + ".stols");
            Lore_Data data = new Lore_Data(key, unique, stols);
            mc.Lores.put(key, data);
        }

        // Mess
        ConfigurationSection Mess_config = config.getConfigurationSection("Mess");
        for (String key : Mess_config.getKeys(false)) {
            mc.Mess.put(key, Mess_config.getString(key));
        }

        return mc;
    }

    public Set<Integer> getStolsInt() {
        if (Stol.isEmpty()) {
            for (Map.Entry<String, Lore_Data> v : Lores.entrySet()) {
                Stol.addAll(v.getValue().stols);
            }
        }
        return Stol;
    }

    private void reset_lore_stol_map() {
        for (Map.Entry<String, Lore_Data> v : Lores.entrySet()) {
            for (Integer i : v.getValue().stols) {
                if (lore_stol_map.containsKey(i)) {
                    List<String> list = new ArrayList<>(lore_stol_map.get(i));
                    list.add(v.getValue().name);
                    lore_stol_map.put(i,list);
                } else {
                    lore_stol_map.put(i, Arrays.asList(v.getValue().name));
                }
            }

        }
    }

    public String getMess(String key) {
        return Mess.containsKey(key) ? Mess.get(key) : "";
    }

    public List<String> getLore(int stol) {
        if (lore_stol_map.isEmpty()) {
            reset_lore_stol_map();
        }
        return lore_stol_map.containsKey(stol) ? lore_stol_map.get(stol) : Arrays.asList();
    }

    public String getItemLore(ItemStack stack) {
        Set<String> keys = Lores.keySet();
        if (stack.getItemMeta().hasLore())
            return "null";
        for (String key : keys) {
            for (String str : stack.getItemMeta().getLore()) {
                if (str.contains(key)) {
                    return key;
                }
            }
        }
        return "null";
    }

    public Boolean is(ItemStack stack, int stol) {
        List<String> keys = getLore(stol);
        if (stack.getItemMeta().hasLore())
            return false;
        if (getItemLore(stack) != "null") {
            return keys.contains(getItemLore(stack));
        }
        return false;
    }

    public ItemStack getItem(int stol) {
        return BConfigManager.items_config.get(Stols.get(stol));
    }

    public boolean is_unique(Player player, String lore) {
        if (player_unique_map.containsKey(player)) {
            return player_unique_map.get(player).map.containsKey(lore);
        }
        return false;
    }

    public void add_unique(Player player, String lore, int stol) {
        if (!player_unique_map.containsKey(player)) {
            player_unique_map.put(player, new Player_Unique(player.getName()));
        }
        player_unique_map.get(player).add(lore, stol);
    }

    public void dle_unique(Player player, String lore) {
        if (player_unique_map.containsKey(player)) {
            player_unique_map.get(player).del(lore);
        }
    }

    public static class Player_Unique {
        public final String player_name;
        private final Map<String, Integer> map = new HashMap<>();

        public Player_Unique(String player_name) {
            this.player_name = player_name;
        }

        public void add(String lore, int stol) {
            map.put(lore, stol);
        }

        public void del(String lore) {
            map.remove(lore);
        }

        public boolean is(String lore) {
            return map.containsKey(lore);
        }

        public int get_stol(String lore) {
            return is(lore) ? map.get(lore) : -1;
        }
    }

    public static class Lore_Data {
        public final String name;
        public final Boolean unique;
        public final List<Integer> stols;

        public Lore_Data(String name, Boolean unique, List<Integer> stols) {
            this.name = name;
            this.unique = unique;
            this.stols = stols;
        }
    }
}
