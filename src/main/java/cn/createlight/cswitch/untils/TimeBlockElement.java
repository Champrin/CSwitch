package cn.createlight.cswitch.untils;

import cn.nukkit.block.Block;
import cn.nukkit.scheduler.Task;

public class TimeBlockElement extends Task {

    private int second;
    private final Block showBlock;

    public TimeBlockElement(int second, Block showBlock) {
        this.second = second;
        this.showBlock = showBlock;
    }

    @Override
    public void onRun(int i) {
        this.second = second - 1;
        if (this.second <= 0) {
            if (showBlock.getLevel().getBlock(showBlock).getId() != Block.GLASS) {
                showBlock.getLevel().setBlock(showBlock, Block.get(Block.WOOL, 0));
            }
            this.cancel();
        }
    }
}
