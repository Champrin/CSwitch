package cn.createlight.cswitch.room;

import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.ArrayList;
import java.util.Map;

public class RoomGlobalListeners implements Listener {

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        //TODO 换一种方式
        Player player = event.getPlayer();
        Room room = RoomManager.getPlayerRoom(player);
        if (room == null) return;
        if (event.getMessage().contains("@hub")) {
            event.setCancelled(true);
            player.sendMessage(">  你已退出游戏！");
            room.stopGame();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onTouch(PlayerInteractEvent event) {
        Block block = event.getBlock();
        if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
            BlockEntity sign = event.getBlock().level.getBlockEntity(block);
            if (sign instanceof BlockEntitySign) {
                Player player = event.getPlayer();
                //TODO 重新写逻辑
                if (((BlockEntitySign) sign).getText()[2].equals("§a点击加入游戏")) {
                    event.setCancelled(true);
                    int x = (int) Math.round(Math.floor(block.x));
                    int y = (int) Math.round(Math.floor(block.y));
                    int z = (int) Math.round(Math.floor(block.z));

                    for (Map.Entry<String, ArrayList<BlockEntitySign>> map : RoomManager.getBlockSignsEntrySet()) {
                        if (map.getValue().get(0) == sign) {
                            Room room = RoomManager.getRoom(map.getKey());
                            if (room != null) {
                                room.joinToRoom(player);
                            }
                            break;
                        }
                    }
                } else if (((BlockEntitySign) sign).getText()[2].equals("§a点击查看游戏介绍")) {
                    event.setCancelled(true);
                    FormWindowSimple window = new FormWindowSimple(
                            ConfigManager.getConfig(ConfigManager.ConfigName.GAME_RULE).getString(LanguageConfigKey.RULE_FORM_WINDOW_TITLE.toConfigKey()),
                            ConfigManager.getConfig(ConfigManager.ConfigName.GAME_RULE).getString(LanguageConfigKey.RULE_FORM_WINDOW_CONTENT.toConfigKey())
                    );
                    player.showFormWindow(window);
                }
            }
        }
    }
}
