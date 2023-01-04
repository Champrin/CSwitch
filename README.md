# CSwitch

+ 至今为止，这个插件包含了10多种有趣的小游戏
+ 如果你的服务器仍然只有几个玩家在同时在线，流行游戏插件基本上摆设
+ 这款游戏插件的游戏都是只需一个玩家自己即可进行游玩
+ 当服务器人少时，玩家无需愁找不到人进行娱乐游戏

1. 插件自带自动储存背包，且自动返回背包，故不需要再加装保存背包的插件
2. 启用游戏模式飞行，考虑到有些服主可能会设置比较大的游戏区域

## 游戏介绍

| 序号  |          游戏名          | 介绍                                                                          |
|:---:|:---------------------:|-----------------------------------------------------------------------------|
|  1  |     LightsOut(关灯)     | 胜利条件: 使所有方块都是绿色羊毛<br>玩法: 点击黑色羊毛，其上下左右的黑色<br>羊毛都会变成绿色而周围的绿色羊毛会重新变为黑色         |
|  2  |    OneToOne(一一对应)     | 游戏结束: 所有方块都变为黑色<br>玩法: 将手中的颜料，与游戏区域内的原料一一对应<br>对应正确得一分，错误扣1分，               |
|  3  |      Jigsaw(拼图)       | 胜利条件: 使游戏区域内的方块变为与模板一模一样的图案<br>玩法: 将手中的方块正确点击游戏区域,要做到<br>手中的方块与模板的同一位置的方块一样 |
|  4  |   RemoveAll(方块消消乐)    | 胜利条件: 消除所有方块<br>玩法: 点击一个方块可以消除当这个方块周围连着同样的<br>方块时,会被一起消除                    |
|  5  | BlockPlay_4(方块华容道4*4) | 胜利条件: 将方块的顺序移为跟模板一样的顺序<br>玩法: 点击你想移动的方块，会与玻璃方块互相交换<br>交换,最后移至跟模板一样即可        |
|  6  | BlockPlay_3(方块华容道3*3) | 胜利条件: 将方块的顺序移为跟模板一样的顺序<br>玩法: 点击你想移动的方块，会与玻璃方块互相交换<br>交换,最后移至跟模板一样即可        |
|  7  |   CrazyClick(疯狂点击)    |                                                                             |
|  8  | AvoidWhiteBlock(别踩白块) |                                                                             |
|  9  |      Sudoku(数独)       | 根据9×9盘面上的已知数字，推理出所有剩余空格的数字<br>并满足每一行、每一列、每一个粗线宫（3*3）内的数字均含1-9，且不重复[来自百度百科]  |
| 10  |    BeFaster(快速反应)     | 类似打地鼠                                                                       |
| 11  |   HanoiTower(汉诺塔游戏)   |                                                                             |
| 11  |         颜色记忆          | 寻找相同色块的方块，使前后两次点击的色块相同                                                      |

## 其他说明

+ 别踩白块：
  配置文件中有
```yaml
times : 15
```
这个是用来置别踩白块黑块的出现次数

+ 方块华容道模板

## 指令说明

```yaml
/cs help                                     ------ All command and help
/cs add [roomName] [Number before game type] ------ Create a new room
/cs set [roomName]                           ------ Set up a room
/cs del [roomName]                           ------ Delete a room
/csrank                                      ------ View the game rank
```

## 创建一个游戏的步骤

+ 首先输入/cs add xxx 1
+ 之后再输入/cs set xxx
+ 然后按照提示破坏方块即可

## 注意事项

+ 边框自行设置且不能使用羊毛自行建造模板
+ 请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2
+ 各类游戏的设置要求，会在设置时提示
+ 请按严格按照所提示的信息来建设游戏场地，不能有误。