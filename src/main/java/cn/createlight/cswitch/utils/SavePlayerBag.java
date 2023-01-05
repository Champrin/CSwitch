package cn.createlight.cswitch.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.ArrayList;

public class SavePlayerBag {
    // 这里使用了若水的保存物品NBT的方法
    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length == 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static ArrayList<String> saveBag(Player player) {
        ArrayList<String> bagInfo = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            Item item = player.getInventory().getItem(i);
            String nbt = "null";
            if (item.hasCompoundTag()) {
                nbt = bytesToHexString(item.getCompoundTag());
            }
            bagInfo.add(item.getId() + "-" + item.getDamage() + "-" + item.getCount() + "-" + nbt);
        }
        player.getInventory().clearAll();
        return bagInfo;
    }

    public static void loadBag(Player player, ArrayList<String> bagInfo) {
        if (bagInfo.isEmpty()) return;
        player.getInventory().clearAll();
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            String[] a = bagInfo.get(i).split("-");
            Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
            if (!a[3].equals("null")) {
                CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
                item.setNamedTag(tag);
            }
            player.getInventory().setItem(i, item);
        }
        bagInfo.clear();
    }
}
