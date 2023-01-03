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

    int row, col; //array的行数和列数
    private int[][] array = new int[4][4];

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
        buildArena();
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.isFinished) return;
        if (!this.gameTypeName.equals("C2048")) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;
        Item item = player.getInventory().getItemInHand();
        event.setCancelled();
        if (item.getId() != 35) return;

        // 获取行数/每列有多少元素： int rowLength = array.length;
        // 获取列数/每行有多少元素： int colLength = array[0].length;
        switch (item.getDamage()) {
            case 0:// up上移
                // 0.遍历每列
                for (int i = 0; i < array[0].length; i++) {
                    // 1.自上而下取出列元素
                    int[] cols = new int[array.length];
                    for (int j = 0; j < array.length; j++) {
                        cols[j] = array[j][i];
                    }
                    // 2.元素合并
                    cols = mergeNear(cols);
                    // 3.将合并后的元素重新给二维数组赋值
                    for (int k = 0; k < cols.length; k++) {
                        array[k][i] = cols[k];
                    }
                }
                break;
            case 1:// down下移
                // 0.遍历每列
                for (int i = 0; i < array[0].length; i++) {
                    // 1.自下而上取出列元素
                    int[] cols = new int[array.length];
                    for (int j = array.length - 1; j >= 0; j--) {
                        cols[3 - j] = array[j][i];
                    }
                    // 2.元素合并
                    cols = mergeNear(cols);
                    // 3.将合并后的元素重新给二维数组赋值
                    for (int k = 0; k < cols.length; k++) {
                        array[3 - k][i] = cols[k];
                    }
                }
                break;
            case 2:// left左移
                // 0.遍历每行
                for (int i = 0; i < array.length; i++) {
                    // 1.自左而右取出行元素
                    int[] rows = new int[array[0].length];
                    for (int j = 0; j < array[0].length; j++) {
                        rows[j] = array[i][j];
                    }
                    // 2.元素合并
                    rows = mergeNear(rows);
                    // 3.将合并后的元素重新给二维数组赋值
                    for (int k = 0; k < rows.length; k++) {
                        array[i][k] = rows[k];
                    }
                }
                break;
            case 3:// right右移
                // 0.遍历每行
                for (int i = 0; i < array.length; i++) {
                    // 1.自右而左取出行元素
                    int[] rows = new int[array[0].length];
                    for (int j = array[0].length - 1; j >= 0; j--) {
                        rows[3 - j] = array[i][j];
                    }
                    // 2.元素合并
                    rows = mergeNear(rows);
                    // 3.将合并后的元素重新给二维数组赋值
                    for (int k = 0; k < rows.length; k++) {
                        array[i][3 - k] = rows[k];
                    }
                }
                break;
        }
        updateBlock();

        checkFinish();
    }

    // 算法分析：
    // 0.   上移、下移操作的是列，左移、右移操作的是行
    //      上移、下移需要取出各列元素，上移：自上而下取出各列元素，下移：自下而上取出各列元素
    //      左移、右移需要取出各行元素，左移：自左而右取出各行元素，右移：自右向左取出各行元素
    // 1.   将0元素移到行的最后
    // 2.   合并相邻元素，相邻相同的元素值加到前一个元素中，后一个元素归零
    //      合并过程中，还会产生0元素，需要再次将0元素移到列的最后。
    // 3.   最后，将移动后的各列元素重新给4*4的二维数组赋值。

    public void updateBlock() {


        randomBlock();
    }

    /**
     * 将0移到数组最后
     *
     * @param arr 1*4数组
     * @return 移动后的数组
     */
    static int[] moveZero(int[] arr) {
        int[] res = new int[arr.length];
        int index = 0;
        for (int i = 0; i < 4; ++i) {
            if (arr[i] != 0) {
                res[index++] = arr[i];
            }
        }
        return res;
    }

    /**
     * 合并相邻相同方块
     *
     * @param arr arr 1*4数组
     * @return 合并后的数组
     */
    static int[] mergeNear(int[] arr) {
        //合并之前，先将0移到数组最后
        int[] arr1 = moveZero(arr);
        //合并相邻相同方块，合并结果放在前一个方块，后一个方块置零
        for (int i = 0; i < arr1.length - 1; i++) {
            if (arr1[i] != 0 && arr1[i] == arr1[i + 1]) {
                arr1[i] += arr1[i + 1];
                arr1[i + 1] = 0;
            }
        }
        //合并之后，将0移到数组最后，返回结果
        return moveZero(arr1);
    }

    //进行滑动后，将在随机位置产生一个新的方块。新方块有 90% 的几率为 ”2“, 10% 的几率是 ”4“。
//然后，继续进行游戏，直到方格中不再有能移动的方块为止。
//按理来说，这游戏的目标是达到一个值为“ 2048”的方块就结束了。但是，we never stop，我们可以继续进行游戏，来争取更大的胜利。理论上，方块最大值为 “ 131072” 。
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
        int x = room.xMin;
        int z = room.zMin;
        int y = new Random().nextInt(room.yMax - room.yMin) + room.yMin;

        if (room.zMin - room.zMax != 0) {
            z = new Random().nextInt(room.zMax - room.zMin) + room.zMin;
        } else if (room.xMin - room.xMax != 0) {
            x = new Random().nextInt(room.xMax - room.xMin) + room.xMin;
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
            this.room.isFinished = true;
        }
        //判断游戏是否结束
        //遍历二维数组，看是否存在横向和纵向两个相邻的元素相等，若存在，则游戏不结束，若不存在，则游戏结束。
        boolean flag = false; //判断标志位，flag = false表示游戏失败
        for (int i = 0; i < row - 1; ++i) {
            for (int j = 0; j < col - 1; ++j) {
                if (array[i][j] == array[i][j + 1] || array[i][j] == array[i + 1][j]) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            this.room.isFinished = false;
            this.room.gamePlayer.sendMessage(">>  游戏失败");
        }
    }

    @Override
    public void buildArena() {
        Block block = randomV3();

        room.level.setBlock(block, Block.get(35, 0));
        check = check + 1;
        buildOperation(true);
    }
}
