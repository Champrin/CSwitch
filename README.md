# CSwitch

    Nukkit Plugin - A Funny Game Plugin - Multi Room
    一款有趣的Nukkit多房间游戏插件
    Up to now,this plugin include more than 10 kinds of funny mini games.
    至今为止，这个插件包含了10多种有趣的小游戏
    If your server is still hava few players at the same time,so most of the game plugins are useless,but this plugin can help you.
    如果你的服务器仍然只有几个玩家在同时在线，所以大部分的游戏插件就没有用了，但是这个插件可以帮助你。
    What kinds of these mini games?These mini games just need one players to play!
    这一款游戏插件只需要玩家独自一个就可以进行游玩！
    Help all sentient beings!Please give a star！It's still being updated!
    普助众生！Star暗示！仍在更新中!
    Now,let us start to introduce it.
    下面开始介绍
    
## Picture 图片展示
![](https://attachment.mcbbs.net/forum/202003/24/220945mlu32mkocl239mpl.gif)
![](https://attachment.mcbbs.net/forum/202003/24/220945hkx94by8kby9u3a8.gif)
![](https://attachment.mcbbs.net/forum/202003/24/220945qjctqsb574stf77m.gif)
![](https://attachment.mcbbs.net/forum/202003/29/002336bbrmeccrriaehacz.jpg)

## Function 功能介绍
    
    This plugin comes with the self-storage backpack and automatically returns to the backpack, so there is no need to add another plugin to save the backpack.
    插件自带自动储存背包，且自动返回背包，故不需要再加转保存背包的插件
    Enable game mode flying, considering that some server owners may set a larger game area.
    启用游戏模式飞行，考虑到有些服主可能会设置比较大的游戏区域。
   
## Game 游戏介绍
### Game Type 游戏类型

 Number 序号 | GameName游戏名 
------------- | -------------
 1 | LightsOut(关灯) 
 2 | OneToOne(一一对应) 
 3 | Jigsaw(拼图) 
 4 | RemoveAll(方块消消乐) 
 5 | BlockPlay_4(方块华容道4*4) 
 6 | BlockPlay_3(方块华容道3*3) 
 7 | CrazyClick(疯狂点击) 
 8 | AvoidWhiteBlock(别踩白块) 
 9 | Sudoku(数独) 
 10 | BeFaster(快速反应) 
 11 | HanoiTower(汉诺塔游戏) 

方块华容道模板
![](https://attachment.mcbbs.net/forum/202003/26/195012k1v1gn442n6rez6e.jpg)
![](https://attachment.mcbbs.net/forum/202003/26/195015oom875rwixii75w3.png)


其他说明

    ●别踩白块：
    配置文件中有
    times : 15
    这个是用来置别踩白块黑块的出现次数
    
## 指令说明

    /cs help                                     ------ All command and help
    /cs add [roomName] [Number before game type] ------ Create a new room
    /cs set [roomName]                           ------ Set up a room
    /cs del [roomName]                           ------ Delete a room
    /csrank                                      ------ View the game rank


-To create a game 创建一个游戏的步骤

    -First input /cs add xxx 1
     首先输入/cs add xxx 1
    -Then input /cs set xxx
     之后再输入/cs set xxx
    -Then follow the prompts
     然后按照提示破坏方块即可
    
-Warning 注意事项

    -The frame shall be set by yourself and shall not be made of wool
    边框自行设置且不能使用羊毛自行建造模板
    -Please use the vertical plane and the top left corner must be set as point 1 and the bottom right corner must be set as point 2
     请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2
    -The setting requirements of all kinds of games will be prompted when you setting
     各类游戏的设置要求，会在设置时提示
     
-Please build the game ground strictly according to the information prompted. There is no mistake.
 请按严格按照所提示的信息来建设游戏场地，不能有误。
