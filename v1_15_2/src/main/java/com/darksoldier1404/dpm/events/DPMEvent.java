package com.darksoldier1404.dpm.events;

import com.darksoldier1404.dpm.Menu;
import com.darksoldier1404.dpm.functions.DPMFunction;
import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Quadruple;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("all")
public class DPMEvent implements Listener {
    private final Menu plugin = Menu.getInstance();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().split(" ")[0].substring(1);
        plugin.menus.values().forEach(m -> {
            if (m.getString("Menu.ALIASES") != null && m.getString("Menu.ALIASES").equalsIgnoreCase(cmd)) {
                e.setCancelled(true);
                plugin.getServer().dispatchCommand(e.getPlayer(), "dpm open " + m.getString("Menu.NAME"));
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory();
            if (inv.getObj() == null) {
                return;
            }
            DPMFunction.saveItemSetting((Player) e.getPlayer(), ((Tuple<String, String>) inv.getObj()).getA(), inv);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory();
            if (e.getCurrentItem() == null) return;
            Player p = (Player) e.getWhoClicked();
            if (inv.getObj() == null) {
                e.setCancelled(true);
                if (NBT.hasTagKey(e.getCurrentItem(), "dpm.command")) {
                    String command = NBT.getStringTag(e.getCurrentItem(), "dpm.command");
                    if (NBT.hasTagKey(e.getCurrentItem(), "op_cmd")) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if(p.isOp()){
                                p.performCommand(command.replace("<player>", p.getName()));
                            }else{
                                p.setOp(true);
                                p.performCommand(command.replace("<player>", p.getName()));
                                p.setOp(false);
                            }
                        }, 2L);
                    } else {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            p.performCommand(command.replace("<player>", p.getName()));
                        }, 2L);
                    }
                }
                if (NBT.hasTagKey(e.getCurrentItem(), "dpm.sound")) {
                    try {
                        String sound = NBT.getStringTag(e.getCurrentItem(), "dpm.sound");
                        float volume = Float.parseFloat(NBT.getStringTag(e.getCurrentItem(), "dpm.sound.volume"));
                        float pitch = Float.parseFloat(NBT.getStringTag(e.getCurrentItem(), "dpm.sound.pitch"));
                        p.playSound(p.getLocation(), sound, volume, pitch);
                    } catch (Exception ex) {
                        p.sendMessage(plugin.data.getPrefix() + "사운드 설정이 잘못되었습니다.");
                        p.sendMessage(plugin.data.getPrefix() + "소리 : " + NBT.getStringTag(e.getCurrentItem(), "dpm.sound"));
                        p.sendMessage(plugin.data.getPrefix() + "볼륨 : " + NBT.getStringTag(e.getCurrentItem(), "dpm.sound_volume"));
                        p.sendMessage(plugin.data.getPrefix() + "피치 : " + NBT.getStringTag(e.getCurrentItem(), "dpm.sound_pitch"));
                    }
                }
                if(NBT.hasTagKey(e.getCurrentItem(), "dpm.cwc")) {
                    p.closeInventory();
                }
                if(NBT.hasTagKey(e.getCurrentItem(), "dpm.price")) {
                    String sprice = NBT.getStringTag(e.getCurrentItem(), "dpm.price");
                    try{
                        double price = Double.parseDouble(sprice);
                        MoneyAPI.takeMoney(p, price);
                    }catch(Exception ex) {
                        p.sendMessage(plugin.data.getPrefix() + "가격 설정이 잘못되었습니다.");
                        p.sendMessage(plugin.data.getPrefix() + "가격 : " + NBT.getStringTag(e.getCurrentItem(), "dpm.price"));
                    }
                }
                return;
            }
            Tuple<String, String> t = (Tuple<String, String>) inv.getObj();
            String b = t.getB();
            if (!b.equalsIgnoreCase("ITEMS")) {
                e.setCancelled(true);
                if (b.equalsIgnoreCase("commands")) {
                    DPMFunction.currentEditItem.put(p.getUniqueId(), Quadruple.of(t.getA(), e.getCurrentItem(), "commands", e.getSlot()));
                    p.closeInventory();
                    p.sendMessage(plugin.data.getPrefix() + "설정하실 커맨드를 입력하세요.");
                }
                if (b.equalsIgnoreCase("sound")) {
                    DPMFunction.currentEditItem.put(p.getUniqueId(), Quadruple.of(t.getA(), e.getCurrentItem(), "sound", e.getSlot()));
                    p.closeInventory();
                    p.sendMessage(plugin.data.getPrefix() + "설정하실 사운드를 입력하세요. ( EX) ui.button.click 1 1 )");
                }
                if(b.equalsIgnoreCase("price")) {
                    DPMFunction.currentEditItem.put(p.getUniqueId(), Quadruple.of(t.getA(), e.getCurrentItem(), "price", e.getSlot()));
                    p.closeInventory();
                    p.sendMessage(plugin.data.getPrefix() + "설정하실 가격을 입력하세요.");
                }
                if (b.equalsIgnoreCase("op")) {
                    if (NBT.hasTagKey(e.getCurrentItem(), "op_cmd")) {
                        e.setCurrentItem(NBT.removeTag(e.getCurrentItem(), "op_cmd"));
                        p.sendMessage(plugin.data.getPrefix() + "해당 슬롯을 유저의 권한으로 실행되게 설정하였습니다.");
                    } else {
                        e.setCurrentItem(NBT.setStringTag(e.getCurrentItem(), "op_cmd", "true"));
                        p.sendMessage(plugin.data.getPrefix() + "해당 슬롯을 관리자의 권한으로 실행되게 설정하였습니다.");
                    }
                }
                if (b.equalsIgnoreCase("cwc")) {
                    if (NBT.hasTagKey(e.getCurrentItem(), "dpm.cwc")) {
                        e.setCurrentItem(NBT.removeTag(e.getCurrentItem(), "dpm.cwc"));
                        p.sendMessage(plugin.data.getPrefix() + "해당 슬롯을 클릭시 메뉴를 닫히지 않게 설정하였습니다.");
                    } else {
                        e.setCurrentItem(NBT.setStringTag(e.getCurrentItem(), "dpm.cwc", "true"));
                        p.sendMessage(plugin.data.getPrefix() + "해당 슬롯을 클릭시 메뉴를 닫히게 설정하였습니다.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (DPMFunction.currentEditItem.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            Quadruple<String, ItemStack, String, Integer> t = DPMFunction.currentEditItem.get(e.getPlayer().getUniqueId());
            if (t.getC().equalsIgnoreCase("commands")) {
                t.setB(DPMFunction.setCommand(t.getB(), e.getMessage()));
                Bukkit.getScheduler().runTask(plugin, () -> {
                    DPMFunction.openCommandSettingGUI(e.getPlayer(), t.getA(), t.getB(), t.getD());
                    DPMFunction.currentEditItem.remove(e.getPlayer().getUniqueId());
                });
            }
            if (t.getC().equalsIgnoreCase("sound")) {
                t.setB(DPMFunction.setSound(t.getB(), e.getMessage()));
                Bukkit.getScheduler().runTask(plugin, () -> {
                    DPMFunction.openSoundSettingGUI(e.getPlayer(), t.getA(), t.getB(), t.getD());
                    DPMFunction.currentEditItem.remove(e.getPlayer().getUniqueId());
                });
            }
            if(t.getC().equalsIgnoreCase("price")) {
                t.setB(DPMFunction.setPrice(t.getB(), e.getMessage()));
                Bukkit.getScheduler().runTask(plugin, () -> {
                    DPMFunction.openPriceSettingGUI(e.getPlayer(), t.getA(), t.getB(), t.getD());
                    DPMFunction.currentEditItem.remove(e.getPlayer().getUniqueId());
                });
            }
        }
    }
}
