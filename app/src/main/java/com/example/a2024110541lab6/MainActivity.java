package com.example.a2024110541lab6;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 主界面：用三个 Fragment 分别展示个人信息、两张牌翻牌游戏、三张牌翻牌游戏。
 * 顶部三个标签用于切换 Fragment，所有 Fragment 都放在同一个 Fragment 容器中。
 */
public class MainActivity extends FragmentActivity {

    private static final String STATE_INDEX = "state_index";

    private static final String[] TAGS = {
            "fragment_info", "fragment_game2", "fragment_game3"
    };

    private TextView[] tabs;
    private int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = new TextView[]{
                findViewById(R.id.tab_info),
                findViewById(R.id.tab_game2),
                findViewById(R.id.tab_game3)
        };
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabs[i].setOnClickListener(v -> showFragment(index));
        }

        int startIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_INDEX, 0);
        showFragment(startIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_INDEX, currentIndex);
    }

    /**
     * 显示指定位置的 Fragment：不存在的先添加，已存在的直接显示，其余隐藏。
     */
    private void showFragment(int index) {
        if (index == currentIndex) {
            updateTabState();
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (String tag : TAGS) {
            Fragment fragment = manager.findFragmentByTag(tag);
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
        Fragment target = manager.findFragmentByTag(TAGS[index]);
        if (target == null) {
            target = createFragment(index);
            transaction.add(R.id.fragment_container, target, TAGS[index]);
        } else {
            transaction.show(target);
        }
        transaction.commit();
        currentIndex = index;
        updateTabState();
    }

    private Fragment createFragment(int index) {
        switch (index) {
            case 1:
                return new TwoCardGameFragment();
            case 2:
                return new ThreeCardGameFragment();
            default:
                return new PersonalInfoFragment();
        }
    }

    /**
     * 当前标签高亮显示。
     */
    private void updateTabState() {
        for (int i = 0; i < tabs.length; i++) {
            boolean selected = i == currentIndex;
            tabs[i].setBackgroundColor(getColor(selected ? R.color.tab_selected_bg : R.color.bar_bg));
            tabs[i].setTextColor(getColor(selected ? R.color.tab_selected_text : R.color.text_red));
            tabs[i].getPaint().setFakeBoldText(selected);
            tabs[i].invalidate();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentIndex > 0) {
            showFragment(0);
        } else {
            super.onBackPressed();
        }
    }

}
