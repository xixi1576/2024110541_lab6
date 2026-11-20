package com.example.a2024110541lab6;

/**
 * 第 3 个 Fragment：三张牌翻牌游戏。
 * 每次翻开三张牌，三张点数相同才配对成功。
 */
public class ThreeCardGameFragment extends GameFragment {

    @Override
    protected int getCardsPerFlip() {
        return 3;
    }

    @Override
    protected int getGroupCount() {
        return 4;
    }

    @Override
    protected String getHistoryFileName() {
        return "game_three_history.dat";
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_game3;
    }

    @Override
    protected int getHintRes() {
        return R.string.game3_hint;
    }
}
