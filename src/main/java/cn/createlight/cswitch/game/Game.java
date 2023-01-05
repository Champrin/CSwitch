package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.createlight.cswitch.room.RoomConfigKey;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.room.RoomManager;

public abstract class Game implements Listener {
    public Room room;

    public int count = 0;
    public int check = 0;

    protected static final Block whiteWoolBlock = Block.get(BlockID.WOOL, 0);
    protected static final Block greenWoolBlock = Block.get(BlockID.WOOL, 5);
    protected static final Block blackWoolBlock = Block.get(BlockID.WOOL, 15);
    protected static final Block airBlock = Block.get(BlockID.AIR, 0);
    protected static final Block glassBlock = Block.get(BlockID.GLASS, 0);

    protected int[][] abstractArray;
    protected int row, col; // 真实世界的坐标点获取在抽象数组中的行号和列号

    protected static final int[][] abstractArrayMoveDirections = {{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    protected final int[][] blockMoveDirections;
    protected final int[][] blockMoveDirectionsWithoutSelf;

    //TODO 根据玩家朝向判断是否与direction一致 x+ z+ ..
    public Game(Room room) {
        this.room = room;

        room.plugin.getServer().getPluginManager().registerEvents(this, room.plugin);

        //TODO
        blockMoveDirections = new int[][]{{0, 0, 0}, {0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}};
        blockMoveDirectionsWithoutSelf = new int[][]{{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}};
        //blockMoveDirections = new int[][]{{0, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}}; //z+/z-
    }

    public abstract void onTouch(PlayerInteractEvent event);

    //public void updateBlock(Block block){}

    //TODO 用事件来搞定完成游戏时需要的操作

    /**
     * 检查是否完成游戏
     */
    public abstract void checkFinish();

    //TODO 类的所有需要初始化的变量都在这里进行
    //TODO 加入setArenaFrame
    //TODO 异步执行
    /**
     * 搭建游戏区域
     */
    public abstract void buildArena();

    /**
     * 有关搭建游戏区域的操作
     *
     * @param value true 表示完成游戏区域搭建，即游戏区域可用
     *              false 表示未完成游戏区域搭建，即游戏区域不可用
     */
    public void buildOperation(boolean value) {
        Config config = RoomManager.getRoomConfig(room.roomID);
        config.set(RoomConfigKey.BUILD_FINISH.toConfigKey(), true);
        config.save();
    }
/***
 this.preLoginEventTask = new AsyncTask() {
 private PlayerAsyncPreLoginEvent event;

 public void onRun() {
 this.event = new PlayerAsyncPreLoginEvent(Player.this.username, Player.this.uuid, Player.this.loginChainData, Player.this.getSkin(), Player.this.getAddress(), Player.this.getPort());
 Player.this.server.getPluginManager().callEvent(this.event);
 }

 public void onCompletion(Server server) {
 if (!Player.this.closed) {
 if (this.event.getLoginResult() == LoginResult.KICK) {
 Player.this.close(this.event.getKickMessage(), this.event.getKickMessage());
 } else if (Player.this.shouldLogin) {
 Player.this.setSkin(this.event.getSkin());
 Player.this.completeLoginSequence();
 Iterator var2 = this.event.getScheduledActions().iterator();

 while(var2.hasNext()) {
 Consumer<Server> action = (Consumer)var2.next();
 action.accept(server);
 }
 }

 }
 }
 };
 this.server.getScheduler().scheduleAsyncTask(this.preLoginEventTask);
 if (this.preLoginEventTask.isFinished()) {
 this.preLoginEventTask.onCompletion(this.server);
 }
 * */


    public void setArenaFrame() {
        int id = 35, mate = 5;
        if (room.gameType == CSwitchGameType.JIGSAW || room.gameType == CSwitchGameType.N_PUZZLE || room.gameType == CSwitchGameType.THE_2048) {
            id = 20;
            mate = 0;
        } else if (room.gameType == CSwitchGameType.QUICK_REACTION || room.gameType == CSwitchGameType.CARD_MEMORY) {
            mate = 0;
        }
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(id, mate));
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        room.level.setBlock(new Vector3(room.xMin, y, z), Block.get(id, mate));
                    }
                }
                break;
        }
    }

    /**
     * 根据在真实世界的坐标点获取在抽象数组中的行号和列号
     *
     * @param x 真实世界的坐标点x
     * @param y 真实世界的坐标点y
     * @param z 真实世界的坐标点z
     */
    protected void getRolCowByBlockPosition(int x, int y, int z) {
        row = room.yMax - y;
        switch (room.direction) {
            case X_PLUS:
                col = x - room.xMin;
                break;
            case X_MINUS:
                col = room.xMax - x;
                break;
            case Z_PLUS:
                col = z - room.zMin;
                break;
            case Z_MINUS:
                col = room.zMax - z;
                break;
        }
    }
}
