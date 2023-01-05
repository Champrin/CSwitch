package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

public class OnOneLine extends Game implements Listener {
    public OnOneLine(Room room) {
        super(room);
    }

    Block lastBlock = null;

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 房间游戏条件限制
        if (this.room.gameType != CSwitchGameType.MAKE_A_LINE) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (block.getId() != BlockID.STAINED_TERRACOTTA) return;
        if (!this.room.isInArena(block)) return;

        Level level = block.level;
        if (lastBlock == null) {
            this.lastBlock = block;
        } else {
            // setBlock(Vector3 v3, Block block)
            level.setBlock(lastBlock, block);
            level.setBlock(block, lastBlock);
            updateBlock(lastBlock);
            lastBlock = null;
        }

        //TODO 判断能否进行游戏
//        int item = player.getInventory().getItemInHand().getId();
//        if (item == Item.DOOR_BLOCK) {
//            this.room.isFinished = true;
//            this.count = 0;
//        }
    }

    // 0.
    // 1.
    // 2.
    // 3.
    // 4.

    public void updateBlock(Block block) {
        int mate = block.getDamage();
        this.EliminateBlock(block, mate);
        this.count = 0;
        this.MoveBlock(block);
        this.checkFinish();
    }

    //TODO 考虑用DFS还是BFS
    //TODO 随机数据保证能有一种解

    private void searchSameBlock(){

    }

    public void EliminateBlock(Block block, int mate) {

        Level level = block.level;
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        checkBlock(level, block, mate);
        checkBlock(level, new Vector3(x, y + 1, z), mate);
        checkBlock(level, new Vector3(x, y - 1, z), mate);
        if (room.direction == Room.Direction.X_PLUS || room.direction == Room.Direction.X_MINUS) {
            checkBlock(level, new Vector3(x + 1, y, z), mate);
            checkBlock(level, new Vector3(x - 1, y, z), mate);
        } else if (room.direction == Room.Direction.Z_PLUS || room.direction == Room.Direction.Z_MINUS) {
            checkBlock(level, new Vector3(x, y, z + 1), mate);
            checkBlock(level, new Vector3(x, y, z - 1), mate);
        }
    }

    //从最下开始

    public void checkBlock(Level level, Vector3 v3, int mate) {
        Block block = level.getBlock(v3);
        if (block.getDamage() == mate) {
            this.count = count + 1;
            level.setBlock(block, Block.get(BlockID.AIR,0));
            this.check = check + 1;
            this.room.point = this.room.point + count;
            this.EliminateBlock(block, mate);
        }
    }

    public void FallingBlock(Block block) {
        Level level = block.level;
        Block underBlock = level.getBlock(new Vector3(block.getFloorX(), block.getFloorY() - 1, block.getFloorZ()));
        if (underBlock.getId() == 0) {
            level.setBlock(block, Block.get(BlockID.AIR,0));
            level.setBlock(underBlock, block);
            this.FallingBlock(level.getBlock(new Vector3(underBlock.getFloorX(), underBlock.getFloorY(), underBlock.getFloorZ())));
        }
    }

    public void MoveBlock(Block block) {
        Level level = block.level;

        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        if (level.getBlock(new Vector3(x, y, room.zMin)).getId() != 0) {
                            FallingBlock(level.getBlock(new Vector3(x, y, room.zMin)));
                        }
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        if (level.getBlock(new Vector3(room.xMin, y, z)).getId() != 0) {
                            FallingBlock(level.getBlock(new Vector3(room.xMin, y, z)));
                        }

                    }
                }
                break;
        }
    }

    public void checkFinish() {

    }
   /*TODO 自动靠拢
     public void clearAir(String direction, int xi, int xa, int yi, int ya, int zi, int za, Level level) {
        switch (direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = xi; x <= xa; x++) {
                    int a = 0;
                    for (int y = yi; y <= ya; y++) {
                        if (level.getBlock(new Vector3(x, y, zi)).getId() == 0) {
                            a = a + 1;
                            if (y == ya && a == (int) this.room.data.get("width")) {
                                for (int i = yi; i <= ya; i++) {
                                    Block b = level.getBlock(new Vector3(x + 1, i, zi));
                                    level.setBlock(new Vector3(x, i, zi), b);
                                    level.setBlock(b, Block.get(0, 0));

                                }
                            }
                        }
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = zi; z <= za; z++) {
                    int a = 0;
                    for (int y = yi; y <= ya; y++) {
                        if (level.getBlock(new Vector3(xi, y, z)).getId() == 0) {
                            a = a + 1;
                            if (y == ya && a == (int) this.room.data.get("width")) {
                                for (int i = yi; i <= ya; i++) {
                                    System.out.println(xi+""+i+""+z);
                                    Block b = level.getBlock(new Vector3(xi, i, z + 1));
                                    level.setBlock(new Vector3(xi, i, z), Block.get(0, 0));
                                    level.setBlock(b, Block.get(0, 0));
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    public void checkFinish(String direction, int xi, int xa, int yi, int ya, int zi, int za, Level level) {
        int all = 0;
        switch (direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = xi; x <= xa; x++) {
                    for (int y = yi; y <= ya; y++) {
                        if (level.getBlock(new Vector3(x, zi, y)).getId() == 0) {
                            all = all + 1;
                        } else {
                            break;
                        }
                        if (y == ya) {
                            if (all >= area) {
                                this.room.finish = true;
                                this.count = 0;
                            }
                        }
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = zi; z <= za; z++) {
                    for (int y = yi; y <= ya; y++) {
                        if (level.getBlock(new Vector3(xi, z, y)).getId() != 0) {
                            all = all + 1;
                            System.out.println("all"+all);
                        } else {
                            break;
                        }
                        if (y == ya) {
                            if (all >= area) {
                                this.room.finish = true;
                                this.count = 0;
                            }
                        }
                    }
                }
                break;
        }
    }*/

    @Override
    public void buildArena() {
        this.lastBlock = null;
        buildOperation(true);
    }
}