package net.createlight.champrin.cswitch.game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.untils.TimeBlockElement;
import net.createlight.champrin.cswitch.room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CardMemory extends Game {

    private int[][] value;

    public CardMemory(Room room) {
        super(room);
        shuffleLayout();
    }

    private void shuffleLayout() {
        this.firstClick = null;
        ArrayList<Integer> board = new ArrayList<>();
        for (int i = 0; i < area / 2; i++) {
            board.add(new Random().nextInt(16));
        }
        board.addAll(board);
        Collections.shuffle(board);
        int x = 0, y = 0;
        int width = (int) this.room.data.get("width");
        int length = (int) this.room.data.get("length");
        value = new int[length][width];
        for (int mate : board) {
            if (x == width) {
                x = 0;
                y = y + 1;
            }
            if (y == length) {
                break;
            }
            value[y][x] = mate;
            x = x + 1;
        }
    }

    private Block firstClick = null;
    private int firstL, firstW;

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.isFinished) return;
        if (this.gameType.equals("CardMemory")) {
            if (!this.room.isStarted) return;
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Block block = event.getBlock();
                Level level = block.level;
                if (block.getId() != Block.WOOL) return;
                if (firstClick == null) {
                    firstClick = block;
                    firstL = Math.abs(room.yMax - firstClick.getFloorY());
                    switch (room.direction) {
                        case X_PLUS:
                        case X_MINUS:
                            firstW = Math.abs(room.xMax - firstClick.getFloorX());
                            break;
                        case Z_PLUS:
                        case Z_MINUS:
                            firstW = Math.abs(room.zMax - firstClick.getFloorZ());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + room.direction);
                    }
                    level.setBlock(firstClick, Block.get(Block.WOOL, value[firstL][firstW]));
                    this.room.plugin.getServer().getScheduler().scheduleRepeatingTask(new TimeBlockElement(3, firstClick), 20);
                } else if (!checkSame(block)) {//阻止再次点击同一块,判断Y轴避免讨论direction
                    int nextL = Math.abs(room.yMax - block.getFloorY());
                    int nextW;
                    switch (room.direction) {
                        case X_PLUS:
                        case X_MINUS:
                            nextW = Math.abs(room.xMax - block.getFloorX());
                            break;
                        case Z_PLUS:
                        case Z_MINUS:
                            nextW = Math.abs(room.zMax - block.getFloorZ());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + room.direction);
                    }
                    level.setBlock(block, Block.get(Block.WOOL, value[nextL][nextW]));
                    this.room.plugin.getServer().getScheduler().scheduleRepeatingTask(new TimeBlockElement(3, block), 20);
                    if (value[firstL][firstW] == value[nextL][nextW]) {
                        this.room.point = room.point + 2;
                        checkFinish();
                        level.setBlock(firstClick, Block.get(Block.GLASS, 0));
                        level.setBlock(block, Block.get(Block.GLASS, 0));
                    }
                    firstClick = null;
                }
            }
        }
    }

    private Boolean checkSame(Block nextBlock) {
        int i = 0;
        if (nextBlock.getFloorY() == firstClick.getFloorY()) {
            i = i + 1;
        }
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                if (nextBlock.getFloorX() == firstClick.getFloorX()) {
                    i = i + 1;
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                if (nextBlock.getFloorZ() == firstClick.getFloorZ()) {
                    i = i + 1;
                }
                break;
        }
        return i == 2;
    }

    @Override
    public void checkFinish() {
        if (room.point >= area) {
            this.room.isFinished = true;
            this.room.point = 0;
        }
    }

    @Override
    public void buildArena() {
        shuffleLayout();
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(35, 0));
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(room.xMin, y, z), Block.get(35, 0));
                    }
                }
                break;
        }
        buildOperation(true);
    }
}
