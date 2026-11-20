package com.example.a2024110541lab6;

import java.io.Serializable;

/**
 * 一局翻牌游戏的战绩，实现 {@link Serializable}，可序列化保存到文件中。
 */
public class GameRecord implements Serializable {

    private static final long serialVersionUID = 2024110543L;

    private final long time;
    private final int score;
    private final int flips;
    private final int groups;

    public GameRecord(long time, int score, int flips, int groups) {
        this.time = time;
        this.score = score;
        this.flips = flips;
        this.groups = groups;
    }

    public long getTime() {
        return time;
    }

    public int getScore() {
        return score;
    }

    public int getFlips() {
        return flips;
    }

    public int getGroups() {
        return groups;
    }
}
