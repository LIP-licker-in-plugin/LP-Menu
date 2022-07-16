package com.darksoldier1404.dpm.commands;

import com.darksoldier1404.dpm.Menu;
import com.darksoldier1404.dpm.functions.DPMFunction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class DPMCommand implements CommandExecutor, TabCompleter {
    private final Menu plugin = Menu.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.data.getPrefix() + "플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            if (p.hasPermission("dpm.use")) {
                p.sendMessage(plugin.data.getPrefix() + "/dpm open <name> - 메뉴를 오픈합니다.");
            }
            if (p.hasPermission("dpm.admin")) {
                p.sendMessage(plugin.data.getPrefix() + "/dpm create <name> <row> - 메뉴를 생성합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm delete <name> - 메뉴를 삭제합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm title <name> <title> - 메뉴의 타이틀을 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm row <name> <row> - 메뉴의 행을 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm items <name> - 메뉴의 아이템을 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm cmds <name> - 메뉴의 클릭시 커맨드를 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm op <name> - 메뉴의 커맨드를 OP권한/일반 권한으로 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm cwc <name> - 메뉴의 아이템 클릭시 메뉴를 닫히게 할지 설정합니다. (CloseWhenClick)");
                p.sendMessage(plugin.data.getPrefix() + "/dpm sound <name> - 메뉴의 클릭시 사운드를 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm price <name> - 메뉴의 클릭시 이용 요금을 설정합니다. (에센셜 필요)");
                p.sendMessage(plugin.data.getPrefix() + "/dpm aliases <name> <cmd> - 메뉴의 단축 명령어를 설정합니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm list - 모든 메뉴를 보여줍니다.");
                p.sendMessage(plugin.data.getPrefix() + "/dpm reload - 메뉴를 리로드합니다.");
            }
            return false;
        }
        if (!p.hasPermission("dpm.use")) {
            p.sendMessage(plugin.data.getPrefix() + "권한이 없습니다.");
            return false;
        }
        if (args[0].equalsIgnoreCase("open")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "오픈할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.openMenu(p, args[1]);
            return false;
        }
        if (!p.hasPermission("dpm.admin")) {
            p.sendMessage(plugin.data.getPrefix() + "권한이 없습니다.");
            return false;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "생성할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(plugin.data.getPrefix() + "생성할 메뉴의 행을 입력해주세요.");
                return false;
            }
            DPMFunction.createMenu(p, args[1], args[2]);
            return false;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "삭제할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.deleteMenu(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("title")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 타이틀을 입력해주세요.");
                return false;
            }
            DPMFunction.setTitle(p, args[1], args);
            return false;
        }
        if (args[0].equalsIgnoreCase("row")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 행을 입력해주세요.");
                return false;
            }
            DPMFunction.setRow(p, args[1], args[2]);
            return false;
        }
        if (args[0].equalsIgnoreCase("items")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.openItemSettingGUI(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("cmds")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.openCommandSettingGUI(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("op")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.openOPSettingGUI(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("sound")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.openSoundSettingGUI(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("cwc")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            DPMFunction.openCWCSettingGUI(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("price")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 가격을 입력해주세요.");
                return false;
            }
            DPMFunction.openPriceSettingGUI(p, args[1]);
            return false;
        }
        if (args[0].equalsIgnoreCase("aliases")) {
            if (args.length == 1) {
                p.sendMessage(plugin.data.getPrefix() + "설정할 메뉴의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(plugin.data.getPrefix() + "메뉴의 단축 명령어를 입력해주세요.");
                return false;
            }
            DPMFunction.setAliases(p, args[1], args[2]);
            return false;
        }
        if (args[0].equalsIgnoreCase("list")) {
            p.sendMessage(plugin.data.getPrefix() + "<<< 메뉴 목록 >>>");
            plugin.menus.keySet().forEach(key -> {
                p.sendMessage(plugin.data.getPrefix() + " - " + key);
            });
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.data.reload();
            p.sendMessage(plugin.data.getPrefix() + "설정을 다시 불러왔습니다.");
            return false;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("dpm.admin")) {
                return Arrays.asList("open", "create", "delete", "title", "row", "items", "cmds", "op", "sound", "cwc", "price", "reload", "aliases");
            }
            return Arrays.asList("open");
        }
        return null;
    }
}
