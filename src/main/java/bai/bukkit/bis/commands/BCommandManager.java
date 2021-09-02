package bai.bukkit.bis.commands;


import bai.bukkit.bis.configs.BConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BCommandManager implements TabExecutor {

    public static BCommandManager getNewInstance() {
        return new BCommandManager();
    }

    public static void register() {
        BCommandManager bcm = getNewInstance();
        PluginCommand pm = Bukkit.getPluginCommand("bis");
        pm.setExecutor(bcm);
        pm.setTabCompleter(bcm);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 1)
                return false;
            String str_1 = args[0];
            if (str_1.equalsIgnoreCase("setItem") && args.length >= 2) {
                String str_2 = args[1];
                ItemStack stack = player.getInventory().getItemInMainHand();
                if (stack.getType() == Material.AIR)
                    player.sendMessage("请不要空手用这个命令~");
                BConfigManager.items_config.add(str_2, stack);
                BConfigManager.items_config.save();
                return true;
            } else if (str_1.equalsIgnoreCase("getItem") && args.length >= 2) {
                String str_2 = args[1];
                player.getInventory().addItem(BConfigManager.items_config.get(str_2));
            }
        }
        if (args.length < 1)
            return false;
        String str_1 = args[0];
        if (str_1.equalsIgnoreCase("reload")) {
            BConfigManager.load_config();
            return true;
        } else if (str_1.equalsIgnoreCase("debug")) {
            BConfigManager.debug = !BConfigManager.debug;
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return Arrays.asList("setItem", "reload", "debug", "getItem");
        if (args.length == 2 && args[0].equalsIgnoreCase("getItem"))
            return new ArrayList<>(BConfigManager.items_config.getIteamNames());
        return null;
    }

}
