package net.createlight.champrin.cswitch.Game;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.utils.Config;
import net.createlight.champrin.cswitch.CSwitch;
import net.createlight.champrin.cswitch.Room;

public abstract class Game implements Listener {

    public CSwitch mainPlugin = CSwitch.getInstance();
    /**
     * 游戏类型名
     */
    public String gameTypeName;
    /**
     * 游戏区域面积
     */
    public int area;

    public int count = 0;
    public int check = 0;

    public Room room;

    protected static final Block whiteWoolBlock = Block.get(BlockID.WOOL, 0);
    protected static final Block greenWoolBlock = Block.get(BlockID.WOOL, 5);
    protected static final Block blackWoolBlock = Block.get(BlockID.WOOL, 15);
    protected static final Block airBlock = Block.get(BlockID.AIR, 0);


    protected final int width, length;

    protected int[][] abstractArray;

    protected int row, col; // 真实世界的坐标点获取在抽象数组中的行号和列号

    protected static final int[][] abstractArrayMoveDirections = {{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    protected final int[][] blockMoveDirections;
//TODO 根据玩家朝向判断是否与direction一致 x+ z+ ..
    public Game(Room room) {
        this.room = room;
        this.mainPlugin = room.plugin;
        mainPlugin.getServer().getPluginManager().registerEvents(this, mainPlugin);
        this.gameTypeName = room.gameTypeName;
        this.area = (int) this.room.data.get("area");
        this.width = (int) this.room.data.get("width");
        this.length = (int) this.room.data.get("length");

        blockMoveDirections = new int[][]{{0, 0, 0}, {0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}};
        //blockMoveDirections = new int[][]{{0, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}}; //z+/z-
    }

    public abstract void onTouch(PlayerInteractEvent event);

    //public void updateBlock(Block block){}

    //TODO 用事件来搞定完成游戏时需要的操作
    /**
     * 检查是否完成游戏
     */
    public abstract void checkFinish();

    /**
     * 搭建游戏区域
     */
    public abstract void buildArena();

    /**
     * 有关搭建游戏区域的操作
     * @param value true 表示完成游戏区域搭建，即游戏区域可用
     *              false 表示未完成游戏区域搭建，即游戏区域不可用
     */
    public void buildOperation(boolean value) {
        Config config = mainPlugin.getRoomData(room.id);
        config.set("arena", true);
        config.save();
        room.data.put("arena", true);
    }
    //TODO true finishBuild false useBuild

    /**
     * 根据在真实世界的坐标点获取在抽象数组中的行号和列号
     *
     * @param x 真实世界的坐标点x
     * @param y 真实世界的坐标点y
     * @param z 真实世界的坐标点z
     */
    protected void getRolCowByBlockPosition(int x, int y, int z) {
        row = room.yMax - y;
        col = switch (room.direction) {
            case "x+" -> x - room.xMin;
            case "x-" -> room.xMax - x;
            case "z+" -> z - room.zMin;
            case "z-" -> room.zMax - z;
        };
    }
}
