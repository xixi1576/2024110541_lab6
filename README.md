# 实验六 多 Fragment 应用

Android 移动应用开发课程实验六，使用 Fragment 重构 APP。

## 功能说明

应用标题为「杨文辉2024110541」，包含三个 Fragment，顶部标签可切换：

1. 个人信息 Fragment：显示并可修改姓名、学号、学院、专业、爱好，保存时把个人信息对象与历史记录序列化写入文件。
2. 两张牌翻牌游戏 Fragment：每次翻开两张牌，两张点数相同即配对成功（+12 分），不同则翻回（-2 分），每翻一张牌 -1 分。
3. 三张牌翻牌游戏 Fragment：每次翻开三张牌，三张点数相同才配对成功（+18 分），否则翻回（-3 分）。

个人信息与两个翻牌游戏的历史记录都采用**对象序列化**方式持久化：

- 写文件：`openFileOutput` + `ObjectOutputStream`
- 读文件：`openFileInput` + `ObjectInputStream`
- 参与序列化的对象（`Profile`、`ProfileRecord`、`GameRecord`、`Card`）均实现了 `Serializable` 接口

因此退出程序后再重新打开，个人信息与历史战绩都可以恢复。

## 分支说明

| 分支 | 内容 |
| --- | --- |
| main | 项目初始化：Gradle 构建配置、界面资源与图标 |
| dev | 在 Dev 分支上：序列化存储工具、数据模型与个人信息 Fragment |
| frag | 在 Frag 分支上：两张牌 / 三张牌翻牌 Fragment 与主界面切换，完成本次作业 |

## 主要源码

| 文件 | 说明 |
| --- | --- |
| `MainActivity.java` | Fragment 容器与三个标签的切换 |
| `PersonalInfoFragment.java` | 个人信息展示、保存与历史记录 |
| `GameFragment.java` | 翻牌游戏公共逻辑（发牌、判重、计分、保存战绩） |
| `TwoCardGameFragment.java` / `ThreeCardGameFragment.java` | 两张牌 / 三张牌玩法 |
| `ObjectStore.java` | 对象序列化读写工具 |
| `Profile.java` / `ProfileRecord.java` / `GameRecord.java` / `Card.java` | 可序列化数据模型 |

## 运行环境

- JDK 25（Android Studio 自带 JBR）
- AGP 9.5.1，Gradle 9.6.0
- compileSdk 37，minSdk 24
- Java 11 编译选项，`androidx.fragment:fragment:1.8.9`
