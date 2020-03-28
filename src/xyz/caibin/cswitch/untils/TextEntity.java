package xyz.caibin.cswitch.untils;


import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class TextEntity extends Entity {

    public String text;

    public TextEntity(FullChunk chunk, CompoundTag nbt, String text) {
        super(chunk, nbt);
        this.text = text;
        this.setNameTag(text);
    }

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setNameTagAlwaysVisible(true);
        this.getDataProperties().putLong(0, 65536L);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setCancelled(true);
        return false;
    }
}































