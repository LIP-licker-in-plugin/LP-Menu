package com.darksoldier1404.dpm.functions;

import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dpm.Menu;
import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.enums.PluginName;
import com.darksoldier1404.dppc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

@SuppressWarnings("all")
public class DPMFunction {
    private static final Menu plugin = Menu.getInstance();
    public static final Map<UUID, Quadruple<String, ItemStack, String, Integer>> currentEditItem = new HashMap<>();
    public static final Map<UUID, DInventory> currentInv = new HashMap<>();

    public static void openMenu(Player p, String name) {
        if (!isValid(name)) return;
        DInventory inv = getMenuInventory(name);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) continue;
            int finalI = i;
            Bukkit.getScheduler().runTask(plugin, () -> {
                inv.setItem(finalI, initPlaceHolder(inv.getItem(finalI), p));
            });
        }
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void createMenu(Player p, String name, String srow) {
        if (isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "이미 존재하는 메뉴입니다.");
            return;
        }
        int row;
        try {
            row = Integer.parseInt(srow);
        } catch (NumberFormatException e) {
            p.sendMessage(plugin.data.getPrefix() + "올바른 숫자를 입력해주세요.");
            return;
        }
        if (row < 1 || row > 6) {
            p.sendMessage(plugin.data.getPrefix() + "메뉴의 행은 1~6 사이여야 합니다.");
            return;
        }
        YamlConfiguration data = new YamlConfiguration();
        data.set("Menu.NAME", name);
        data.set("Menu.ROWS", row);
        plugin.menus.put(name, data);
        saveMenu(name);
        p.sendMessage(plugin.data.getPrefix() + name + " 메뉴가 생성되었습니다.");
    }

    public static void deleteMenu(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        new File(plugin.getDataFolder() + "/menus/" + name + ".yml").delete();
        plugin.menus.remove(name);
        p.sendMessage(plugin.data.getPrefix() + name + " 메뉴가 삭제되었습니다.");
    }

    public static void setTitle(Player p, String name, String... args) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        String title = ColorUtils.applyColor(getText(args, 2));
        plugin.menus.get(name).set("Menu.TITLE", title);
        p.sendMessage(plugin.data.getPrefix() + name + "타이틀이 설정되었습니다. : " + title);
        saveMenu(name);
    }

    public static void setAliases(Player p, String name, String aliases) {
        if (!plugin.menus.containsKey(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        plugin.menus.get(name).set("Menu.ALIASES", aliases);
        p.sendMessage(plugin.data.getPrefix() + name + "메뉴의 단축 명령어가 설정되었습니다. : " + aliases);
        saveMenu(name);
    }

    public static void setRow(Player p, String name, String srow) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        int row;
        try {
            row = Integer.parseInt(srow);
        } catch (NumberFormatException e) {
            p.sendMessage(plugin.data.getPrefix() + "올바른 숫자를 입력해주세요.");
            return;
        }
        if (row < 1 || row > 6) {
            p.sendMessage(plugin.data.getPrefix() + "메뉴의 행은 1~6 사이여야 합니다.");
            return;
        }
        plugin.menus.get(name).set("Menu.ROWS", row);
        p.sendMessage(plugin.data.getPrefix() + name + "메뉴의 행이 설정되었습니다. : " + row);
        saveMenu(name);
    }

    public static void openItemSettingGUI(Player p, String name) { // 1
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setObj(Tuple.of(name, "ITEMS"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void saveItemSetting(Player p, String name, DInventory inv) {
        if (!isValid(name)) return;
        if (currentEditItem.containsKey(p.getUniqueId())) return;
        YamlConfiguration data = plugin.menus.get(name);
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                data.set("Menu.ITEMS." + i, null);
            } else {
                data.set("Menu.ITEMS." + i, item);
            }
        }
        saveMenu(name);
        p.sendMessage(plugin.data.getPrefix() + name + " 메뉴의 아이템 설정이 저장되었습니다.");
    }

    public static DInventory getMenuInventory(String name) {
        YamlConfiguration data = plugin.menus.get(name);
        String rows = data.getString("Menu.ROWS");
        String title = data.getString("Menu.TITLE") == null ? "타이틀이 설정되지 않았습니다." : data.getString("Menu.TITLE");
        title = ColorUtils.applyColor(title);
        DInventory inv = new DInventory(null, title, Integer.parseInt(rows) * 9, plugin);
        if (data.get("Menu.ITEMS") != null) {
            data.getConfigurationSection("Menu.ITEMS").getKeys(false).forEach(key -> {
                inv.setItem(Integer.parseInt(key), data.getItemStack("Menu.ITEMS." + key));
            });
        }
        return inv;
    }

    public static void openCommandSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setObj(Tuple.of(name, "COMMANDS"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void openCommandSettingGUI(Player p, String name, ItemStack item, int slot) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setItem(slot, item);
        inv.setObj(Tuple.of(name, "COMMANDS"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void openSoundSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setObj(Tuple.of(name, "SOUND"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void openSoundSettingGUI(Player p, String name, ItemStack item, int slot) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setItem(slot, item);
        inv.setObj(Tuple.of(name, "SOUND"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void openPriceSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setObj(Tuple.of(name, "PRICE"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void openPriceSettingGUI(Player p, String name, ItemStack item, int slot) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setItem(slot, item);
        inv.setObj(Tuple.of(name, "PRICE"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static void openOPSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setObj(Tuple.of(name, "OP"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static ItemStack setCommand(ItemStack item, String command) {
        return NBT.setStringTag(item, "dpm.command", command);
    }

    public static ItemStack setSound(ItemStack item, String sound) {
        String name = sound.split(" ")[0];
        String volume = sound.split(" ")[1];
        String pitch = sound.split(" ")[2];
        item = NBT.setStringTag(item, "dpm.sound", name);
        item = NBT.setStringTag(item, "dpm.sound.volume", volume);
        return NBT.setStringTag(item, "dpm.sound.pitch", pitch);
    }

    public static ItemStack setPrice(ItemStack item, String price) {
        return NBT.setStringTag(item, "dpm.price", price);
    }

    public static boolean isValid(String name) {
        return plugin.menus.containsKey(name);
    }

    public static void loadAllMenus() {
        List<YamlConfiguration> menus = ConfigUtils.loadCustomDataList(plugin, "menus");
        menus.forEach(menu -> {
            plugin.menus.put(menu.getString("Menu.NAME"), menu);
        });
    }

    public static void saveMenu(String name) {
        ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
    }

    public static String getText(String[] args, int line) {
        StringBuilder s = new StringBuilder();
        args = Arrays.copyOfRange(args, line, args.length);
        Iterator<String> i = Arrays.stream(args).iterator();
        while (i.hasNext()) {
            s.append(i.next()).append(" ");
        }
        // delete last space
        if (s.charAt(s.length() - 1) == ' ') {
            s.deleteCharAt(s.length() - 1);
        }
        return s.toString();
    }

    public static void openCWCSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(plugin.data.getPrefix() + "존재하지 않는 메뉴입니다.");
            return;
        }
        DInventory inv = getMenuInventory(name);
        inv.setObj(Tuple.of(name, "CWC"));
        p.openInventory(inv);
        currentInv.put(p.getUniqueId(), inv);
    }

    public static ItemStack initPlaceHolder(ItemStack item, Player p) {
        ItemMeta im = item.getItemMeta();
        if(im.hasDisplayName()) {
            im.setDisplayName(initReplacer(item, im.getDisplayName(), p));
        }
        if(im.hasLore()) {
            List<String> lore = new ArrayList<>();
            for (String s : im.getLore()) {
                lore.add(initReplacer(item, s, p));
            }
            im.setLore(lore);
        }
        item.setItemMeta(im);
        return item;
    }

    public static String initReplacer(ItemStack item, String text, Player p) {
        text = text.replace("<price>", NBT.getStringTag(item, "dpm.price"));
        text = text.replace("<command>", NBT.getStringTag(item, "dpm.command"));
        text = text.replace("<sound>", NBT.getStringTag(item, "dpm.sound"));
        text = text.replace("<sound.volume>", NBT.getStringTag(item, "dpm.sound.volume"));
        text = text.replace("<sound.pitch>", NBT.getStringTag(item, "dpm.sound.pitch"));
        text = text.replace("<op>", NBT.getStringTag(item, "dpm.op"));
        text = text.replace("<cwc>", NBT.getStringTag(item, "dpm.cwc"));
        text = text.replace("<p_name>", p.getName());
        text = text.replace("<p_displayname>", p.getDisplayName());
        text = text.replace("<p_money>", MoneyAPI.getMoney(p).toString());
        text = text.replace("<p_level>", String.valueOf(p.getLevel()));
        text = text.replace("<p_exp>", String.valueOf(p.getExp()));
        text = text.replace("<p_health>", String.valueOf(p.getHealth()));
        text = text.replace("<p_maxhealth>", String.valueOf(p.getMaxHealth()));
        text = text.replace("<p_food>", String.valueOf(p.getFoodLevel()));
        text = text.replace("<p_gamemode>", String.valueOf(p.getGameMode()));
        text = text.replace("<p_world>", p.getWorld().getName());
        text = text.replace("<p_x>", String.valueOf(p.getLocation().getBlockX()));
        text = text.replace("<p_y>", String.valueOf(p.getLocation().getBlockY()));
        text = text.replace("<p_z>", String.valueOf(p.getLocation().getBlockZ()));
        if(DPPCore.getInstance().enabledPlugins.containsKey(PluginName.LegendaryCash)) {
            text = text.replace("<dlc_cash>", String.valueOf(CashFunction.getCash(p)));
            text = text.replace("<dlc_mileage>", String.valueOf(CashFunction.getMileage(p)));
        }
        return text;
    }
}
