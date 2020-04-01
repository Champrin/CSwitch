package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import xyz.caibin.cswitch.Room;

import java.util.HashMap;
import java.util.Random;

public class C2048 extends Game {

    private int maxNum;
    private HashMap<Integer, Integer> grade = new HashMap<>();
    private HashMap<Integer, Integer> nextGrade = new HashMap<>();

    public C2048(Room plugin) {
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

        this.plugin = plugin;
        this.game_type = plugin.game_type;
        this.area = (int) this.plugin.data.get("area");
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("C2048")) {
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
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
                switch (plugin.direction) {
                    case "x+":
                        x1_up();
                        break;
                    case "z+":
                        z1_up();
                        break;
                }
                break;
            case 1:
                switch (plugin.direction) {
                    case "x+":
                        x1_down();
                        break;
                    case "z+":
                        z1_down();
                        break;
                }
                break;
            case 2:
                switch (plugin.direction) {
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
                switch (plugin.direction) {
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
        for (int y = plugin.ya - 1; y >= plugin.yi; y--) {
            for (int x = plugin.xi; x <= plugin.xa; x++) {
                Block b = plugin.level.getBlock(new Vector3(x, y, plugin.zi));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(x, y - 1, plugin.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(x, y - 1, plugin.zi));
                    clearEmptyX(b1, x, y - 2, plugin.zi);
                }
            }
        }
    }

    private void z1_up() {

        for (int y = plugin.ya - 1; y >= plugin.yi; y--) {
            for (int z = plugin.zi; z <= plugin.zi; z++) {
                Block b = plugin.level.getBlock(new Vector3(plugin.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(plugin.xi, y - 1, z));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(plugin.xi, y - 1, z));
                    clearEmptyZ(b1, plugin.xi, y - 2, z);
                }
            }
        }
    }

    private void x1_down() {
        for (int y = plugin.yi + 1; y <= plugin.ya; y++) {
            for (int x = plugin.xi; x <= plugin.xa; x++) {
                Block b = plugin.level.getBlock(new Vector3(x, y, plugin.zi));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(x, y + 1, plugin.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(x, y + 1, plugin.zi));
                    clearEmptyX(b1, x, y + 2, plugin.zi);
                }
            }
        }
    }

    private void z1_down() {
        for (int y = plugin.yi + 1; y <= plugin.ya; y++) {
            for (int z = plugin.zi; z <= plugin.zi; z++) {
                Block b = plugin.level.getBlock(new Vector3(plugin.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(plugin.xi, y + 1, z));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(plugin.xi, y + 1, z));
                    clearEmptyZ(b1, plugin.xi, y + 2, z);
                }
            }
        }
    }


    private void x1_left() {
        for (int x = plugin.xi + 1; x <= plugin.xa; x++) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(x, y, plugin.zi));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(x - 1, y, plugin.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(x - 1, y, plugin.zi));
                    clearEmptyX(b1, x - 2, y, plugin.zi);
                }
            }
        }
    }

    private void z1_left() {
        for (int z = plugin.zi + 1; z <= plugin.zi; z++) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(plugin.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z - 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z - 1));
                    clearEmptyZ(b1, plugin.xi, y, z - 2);
                }
            }
        }
    }

    private void x2_left() {
        for (int x = plugin.xa - 1; x >= plugin.xa; x--) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(x, y, plugin.zi));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(x + 1, y, plugin.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(x + 1, y, plugin.zi));
                    clearEmptyX2(b1, x + 2, y, plugin.zi);
                }
            }
        }
    }

    private void z2_left() {
        for (int z = plugin.za - 1; z >= plugin.za; z--) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(plugin.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z + 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z + 1));
                    clearEmptyZ2(b1, plugin.xi, y, z + 2);
                }
            }
        }
    }

    //1-->+ 2-->-
    private void x1_right() {
        for (int x = plugin.xa - 1; x >= plugin.xi; x--) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(x, y, plugin.zi));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(x + 1, y, plugin.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(x + 1, y, plugin.zi));
                    clearEmptyX(b1, x + 2, y, plugin.zi);
                }
            }
        }
    }

    private void z1_right() {
        for (int z = plugin.za - 1; z >= plugin.zi; z--) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(plugin.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z + 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        System.out.println(b.getDamage());
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z + 1));
                    clearEmptyZ(b1, plugin.xi, y, z + 2);
                }
            }
        }
    }

    private void x2_right() {
        for (int x = plugin.xi + 1; x <= plugin.xa; x++) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(x, y, plugin.zi));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(x - 1, y, plugin.zi));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(x - 1, y, plugin.zi));
                    clearEmptyX2(b1, x - 2, y, plugin.zi);
                }
            }
        }
    }

    private void z2_right() {
        for (int z = plugin.zi + 1; z <= plugin.za; z++) {
            for (int y = plugin.ya; y >= plugin.yi; y--) {
                Block b = plugin.level.getBlock(new Vector3(plugin.xi, y, z));
                if (b.getId() != 20) {
                    Block b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z - 1));
                    int gb = grade.get(b.getDamage());
                    int gb1 = grade.get(b1.getDamage());
                    if (gb == gb1) {
                        this.check = check - 1;
                        int nextGrade = this.nextGrade.get(gb);
                        plugin.level.setBlock(b1, Block.get(35, nextGrade));
                        checkMaxNum(nextGrade);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    } else if (b1.getId() == 20) {
                        plugin.level.setBlock(b1, b);
                        plugin.level.setBlock(b, Block.get(20, 0));
                    }
                    b1 = plugin.level.getBlock(new Vector3(plugin.xi, y, z - 1));
                    clearEmptyZ2(b1, plugin.xi, y, z - 2);
                }
            }
        }
    }

    private void clearEmptyX(Block block, int x, int y, int z) {
        for (int i = x; i <= plugin.xa; i++) {
            Block b = plugin.level.getBlock(new Vector3(i, y, z));
            if (b.getId() == 20) {
                plugin.level.setBlock(b, block);
                block = plugin.level.getBlock(new Vector3(i, y, z));
                plugin.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void clearEmptyZ(Block block, int x, int y, int z) {
        for (int i = z; i <= plugin.za; i++) {
            Block b = plugin.level.getBlock(new Vector3(x, y, i));
            if (b.getId() == 20) {
                plugin.level.setBlock(b, block);
                block = plugin.level.getBlock(new Vector3(x, y, i));
                plugin.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void clearEmptyX2(Block block, int x, int y, int z) {
        for (int i = x; i >= plugin.xi; i--) {
            Block b = plugin.level.getBlock(new Vector3(i, y, z));
            if (b.getId() == 20) {
                plugin.level.setBlock(b, block);
                block = plugin.level.getBlock(new Vector3(i, y, z));
                plugin.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void clearEmptyZ2(Block block, int x, int y, int z) {
        for (int i = z; i >= plugin.zi; i--) {
            Block b = plugin.level.getBlock(new Vector3(x, y, i));
            if (b.getId() == 20) {
                plugin.level.setBlock(b, block);
                block = plugin.level.getBlock(new Vector3(x, y, i));
                plugin.level.setBlock(block, Block.get(20, 0));
            } else {
                break;
            }
        }
    }

    private void randomBlock() {
        while (true) {
            Block block = randomV3();
            if (block.getId() == 20) {
                plugin.level.setBlock(block, Block.get(35, 0));
                this.check = check + 1;
                break;
            }
        }
    }

    private Block randomV3() {
        int x = plugin.xi;
        int z = plugin.zi;
        int y = new Random().nextInt(plugin.ya - plugin.yi) + plugin.yi;

        if (plugin.zi - plugin.za != 0) {
            z = new Random().nextInt(plugin.za - plugin.zi) + plugin.zi;
        } else if (plugin.xi - plugin.xa != 0) {
            x = new Random().nextInt(plugin.xa - plugin.xi) + plugin.xi;
        }

        return plugin.level.getBlock(new Vector3(x, y, z));
    }

    private void checkMaxNum(int num) {
        if (num > maxNum) {
            this.maxNum = num;
        }
    }

    @Override
    public void checkFinish() {
        if (this.maxNum >= 2048) {
            this.plugin.finish = true;
        } else if (this.check >= area) {
            this.plugin.finish = false;
            this.plugin.gamePlayer.sendMessage(">>  游戏失败");
        }
    }
}
