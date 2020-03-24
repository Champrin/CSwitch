package xyz.caibin.cswitch.Game;

import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.CSwitch;
import xyz.caibin.cswitch.Room;

public abstract class Game implements Listener {

    public CSwitch mainPlugin = CSwitch.getInstance();
    public String game_type;
    public int area;
    public int count = 0;
    public int check = 0;
    public Room plugin;

    public Game(){
        mainPlugin.getServer().getPluginManager().registerEvents(this, mainPlugin);
    }

    public abstract void onTouch(PlayerInteractEvent event);

    public void updateBlock(Block block){};

    public abstract void checkFinish();
}
