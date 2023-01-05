package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

import java.util.Random;

public class RemoveAll extends Game implements Listener {
    public RemoveAll(Room room) {
        super(room);
    }


    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (this.room.gameType != CSwitchGameType.REMOVE_ALL) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (!this.room.isInArena(block)) return;

        this.updateBlock(block);
    }


    public void updateBlock(Block block) {
        if (block.getId() != 159) return;
        int mate = block.getDamage();
        this.EliminateBlock(block, mate);
        this.count = 0;
        this.MoveBlock(block);
        this.checkFinish();
    }

    public void EliminateBlock(Block block, int mate) {
        if (block.getId() != 159) return;

        Level level = block.getLevel();
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
            level.setBlock(block, Block.get(0, 0));
            this.check = check + 1;
            this.room.point = this.room.point + count;
            this.EliminateBlock(block, mate);
        }
    }

    public void FallingBlock(Block block) {
        Level level = block.level;
        Block underBlock = level.getBlock(new Vector3(block.getFloorX(), block.getFloorY() - 1, block.getFloorZ()));
        if (underBlock.getId() == 0) {
            level.setBlock(block, Block.get(0, 0));
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
        if (check >= room.area) {
            this.room.isFinished = true;
            this.count = 0;
            this.check = 0;
        }
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
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        int num = new Random().nextInt(5) + 4;
                        Block block = Block.get(159, num);
                        room.level.setBlock(new Vector3(x, y, room.zMin), block);
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        int num = new Random().nextInt(5) + 4;
                        Block block = Block.get(159, num);
                        room.level.setBlock(new Vector3(room.xMin, y, z), block);
                    }
                }
                break;
        }
        buildOperation(true);
    }
}