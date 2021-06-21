package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

import java.util.HashMap;
import java.util.Random;

public class C2048 extends Game {

    private int maxNum;
    private HashMap<Integer, Integer> grade = new HashMap<>();
    private HashMap<Integer, Integer> nextGrade = new HashMap<>();

    public C2048(Room room) {
        super(room);
        this.grade.put(0, 1);
        this.grade.put(8, 2);
        this.grade.put(7, 3);
        this.grade.put(6, 4);
        this.grade.put(4, 5);
        this.grade.put(1, 6);
        this.grade.put(3, 7);
        this.grade.put(9, 8);
        this.grade.put(11, 9);
        this.grade.put(14, 10);
        this.grade.put(5, 11);

        this.nextGrade.put(1, 8);
        this.nextGrade.put(2, 7);
        this.nextGrade.put(3, 6);
        this.nextGrade.put(4, 4);
        this.nextGrade.put(5, 1);
        this.nextGrade.put(6, 3);
        this.nextGrade.put(7, 9);
        this.nextGrade.put(8, 11);
        this.nextGrade.put(9, 14);
        this.nextGrade.put(10, 5);
        madeArena();
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.finish) return;
        if (this.game_type.equals("C2048")) {
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Item item = player.getInventory().getItemInHand();
                event.setCancelled();
                if (item.getId() != 35) return;
                updateBlock(item.getDamage());

                System.out.println("check " + this.check);
                checkFinish();
            }
        }
    }

    public void updateBlock(int button) {
        switch (button) {
            case 0:
                switch (room.direction) {
                    case "x+":
                        x1_up();
                        break;
                    case "z+":
                        z1_up();
                        break;
                }
                break;
            case 1:
                switch (room.direction) {
                    case "x+":
                        x1_down();
                        break;
                    case "z+":
                        z1_down();
                        break;
                }
                break;
            case 2:
                switch (room.direction) {
                    case "x+":
                        x1_left();
                        break;
                    case "z+":
                        z1_left();
                        break;
                    case "x-":
                        x2_left();
                        break;
                    case "z-":
                        z2_left();
                        break;
                }
                break;
            case 3:
                switch (room.direction) {
                    case "x+":
                        x1_right();
                        break;
                    case "z+":
                        z1_right();
                        break;
                    case "x-":
                        x2_right();
                        break;
                    case "z-":
                        z2_right();
                        break;
                }
                break;
        }
        randomBlock();

    }

    private void x1_up() {
        for (int y = room.ya - 1; y >= room.yi; y--) {
            for (int x = room.xi; x <= room.xa; x++) {
                Block b = room.level.getBlock(new Vector3(x, y, room.zi));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(x, y - 1, room.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(x, y - 1, room.zi));
                    clearEmptyX(b1, x, y - 2, room.zi);
                }
            }
        }
    }

    private void z1_up() {

        for (int y = room.ya - 1; y >= room.yi; y--) {
            for (int z = room.zi; z <= room.zi; z++) {
                Block b = room.level.getBlock(new Vector3(room.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(room.xi, y - 1, z));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(room.xi, y - 1, z));
                    clearEmptyZ(b1, room.xi, y - 2, z);
                }
            }
        }
    }

    private void x1_down() {
        for (int y = room.yi + 1; y <= room.ya; y++) {
            for (int x = room.xi; x <= room.xa; x++) {
                Block b = room.level.getBlock(new Vector3(x, y, room.zi));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(x, y + 1, room.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(x, y + 1, room.zi));
                    clearEmptyX(b1, x, y + 2, room.zi);
                }
            }
        }
    }

    private void z1_down() {
        for (int y = room.yi + 1; y <= room.ya; y++) {
            for (int z = room.zi; z <= room.zi; z++) {
                Block b = room.level.getBlock(new Vector3(room.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(room.xi, y + 1, z));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(room.xi, y + 1, z));
                    clearEmptyZ(b1, room.xi, y + 2, z);
                }
            }
        }
    }


    private void x1_left() {
        for (int x = room.xi + 1; x <= room.xa; x++) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(x, y, room.zi));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(x - 1, y, room.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(x - 1, y, room.zi));
                    clearEmptyX(b1, x - 2, y, room.zi);
                }
            }
        }
    }

    private void z1_left() {
        for (int z = room.zi + 1; z <= room.zi; z++) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(room.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(room.xi, y, z - 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(room.xi, y, z - 1));
                    clearEmptyZ(b1, room.xi, y, z - 2);
                }
            }
        }
    }

    private void x2_left() {
        for (int x = room.xa - 1; x >= room.xa; x--) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(x, y, room.zi));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(x + 1, y, room.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(x + 1, y, room.zi));
                    clearEmptyX2(b1, x + 2, y, room.zi);
                }
            }
        }
    }

    private void z2_left() {
        for (int z = room.za - 1; z >= room.za; z--) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(room.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(room.xi, y, z + 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(room.xi, y, z + 1));
                    clearEmptyZ2(b1, room.xi, y, z + 2);
                }
            }
        }
    }

    //1-->+ 2-->-
    private void x1_right() {
        for (int x = room.xa - 1; x >= room.xi; x--) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(x, y, room.zi));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(x + 1, y, room.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(x + 1, y, room.zi));
                    clearEmptyX(b1, x + 2, y, room.zi);
                }
            }
        }
    }

    private void z1_right() {
        for (int z = room.za - 1; z >= room.zi; z--) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(room.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(room.xi, y, z + 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        System.out.println(b.getDamage());
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(room.xi, y, z + 1));
                    clearEmptyZ(b1, room.xi, y, z + 2);
                }
            }
        }
    }

    private void x2_right() {
        for (int x = room.xi + 1; x <= room.xa; x++) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(x, y, room.zi));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(x - 1, y, room.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(x - 1, y, room.zi));
                    clearEmptyX2(b1, x - 2, y, room.zi);
                }
            }
        }
    }

    private void z2_right() {
        for (int z = room.zi + 1; z <= room.za; z++) {
            for (int y = room.ya; y >= room.yi; y--) {
                Block b = room.level.getBlock(new Vector3(room.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = room.level.getBlock(new Vector3(room.xi, y, z - 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        room.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        room.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        room.level.setBlock(b1, b);
                        room.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = room.level.getBlock(new Vector3(room.xi, y, z - 1));
                    clearEmptyZ2(b1, room.xi, y, z - 2);
                }
            }
        }
    }

    private void clearEmptyX(Block block, int x, int y, int z) {
        for (int i = x; i <= room.xa; i++) {
            Block b = room.level.getBlock(new Vector3(i, y, z));
            if (b.getId() == 20) {
                room.level.setBlock(b, block);
                block = room.level.getBlock(new Vector3(i, y, z));
                room.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void clearEmptyZ(Block block, int x, int y, int z) {
        for (int i = z; i <= room.za; i++) {
            Block b = room.level.getBlock(new Vector3(x, y, i));
            if (b.getId() == 20) {
                room.level.setBlock(b, block);
                block = room.level.getBlock(new Vector3(x, y, i));
                room.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void clearEmptyX2(Block block, int x, int y, int z) {
        for (int i = x; i >= room.xi; i--) {
            Block b = room.level.getBlock(new Vector3(i, y, z));
            if (b.getId() == 20) {
                room.level.setBlock(b, block);
                block = room.level.getBlock(new Vector3(i, y, z));
                room.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void clearEmptyZ2(Block block, int x, int y, int z) {
        for (int i = z; i >= room.zi; i--) {
            Block b = room.level.getBlock(new Vector3(x, y, i));
            if (b.getId() == 20) {
                room.level.setBlock(b, block);
                block = room.level.getBlock(new Vector3(x, y, i));
                room.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void randomBlock() {
        while (true) {
            Block block = randomV3();
            if (block.getId() == 20) {
                room.level.setBlock(block, Block.get(35, 0));
                this.check = check + 1;
                break;
            }
        }
    }

    private Block randomV3() {
        int x = room.xi;
        int z = room.zi;
        int y = new Random().nextInt(room.ya - room.yi) + room.yi;

        if (room.zi - room.za != 0) {
            z = new Random().nextInt(room.za - room.zi) + room.zi;
        } else if (room.xi - room.xa != 0) {
            x = new Random().nextInt(room.xa - room.xi) + room.xi;
        }

        return room.level.getBlock(new Vector3(x, y, z));
    }

    private void checkMaxNum(int num) {
        if (num > maxNum) {
            this.maxNum = num;
        }
    }

    @Override
    public void checkFinish() {
        if (this.maxNum >= 2048) {
            this.room.finish = true;
        } else if (this.check >= area) {
            this.room.finish = false;
            this.room.gamePlayer.sendMessage(">>  游戏失败");
        }
    }

    @Override
    public void madeArena() {
        int x = room.xi;
        int z = room.zi;
        int y = new Random().nextInt(room.ya - room.yi) + room.yi;

        if (room.zi - room.za != 0) {
            z = new Random().nextInt(room.za - room.zi) + room.zi;
        } else if (room.xi - room.xa != 0) {
            x = new Random().nextInt(room.xa - room.xi) + room.xi;
        }

        room.level.setBlock(new Vector3(x, y, z), Block.get(35, 0));
        check = check + 1;
        finishBuild();
    }
}
