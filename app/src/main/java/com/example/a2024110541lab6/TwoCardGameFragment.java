package com.example.a2024110541lab6;

/**
 * 第 2 个 Fragment：两张牌翻牌游戏。
 * 每次翻开两张牌，两张点数相同即配对成功。
 */
public class TwoCardGameFragment extends GameFragment {

    @Override
    protected int getCardsPerFlip() {
        return 2;
    }

    @Override
    protected int getGroupCount() {
        return 6;
    }

    @Override
    protected String getHistoryFileName() {
        return "game_two_history.dat";
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_game2;
    }

    @Override
    protected int getHintRes() {
        return R.string.game2_hint;
    }
}
