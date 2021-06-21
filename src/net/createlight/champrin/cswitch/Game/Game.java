package net.createlight.champrin.cswitch.Game;

import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.utils.Config;
import net.createlight.champrin.cswitch.CSwitch;
import net.createlight.champrin.cswitch.Room;

public abstract class Game implements Listener {

    public CSwitch mainPlugin = CSwitch.getInstance();
    public String game_type;
    public int area;
    public int count = 0;
    public int check = 0;
    public Room room;

    public Game(Room room) {
        this.room = room;
        this.mainPlugin = room.plugin;
        mainPlugin.getServer().getPluginManager().registerEvents(this, mainPlugin);
        this.game_type = room.game_type;
        this.area = (int) this.room.data.get("area");
    }

    public abstract void onTouch(PlayerInteractEvent event);

    //public void updateBlock(Block block){}

    public abstract void checkFinish();

    public abstract void madeArena();

    public void finishBuild() {
        Config config = mainPlugin.getRoomData(room.id);
        config.set("arena", true);
        config.save();
        room.data.put("arena", true);
    }

    public void useBuild() {
        Config config = mainPlugin.getRoomData(room.id);
        config.set("arena", false);
        config.save();
        room.data.put("arena", false);
    }
}
