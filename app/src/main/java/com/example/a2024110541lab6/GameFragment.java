package com.example.a2024110541lab6;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 翻牌游戏的公共 Fragment 基类。
 *
 * <p>子类只需指定“每次翻开几张牌”“牌组数量”“历史记录文件名”，
 * 两张牌与三张牌两种玩法共用同一套翻牌、判重、计分与序列化保存逻辑。</p>
 */
public abstract class GameFragment extends Fragment {

    /** 历史战绩最多保存条数。 */
    private static final int MAX_HISTORY = 8;

    /** 每次翻开几张牌才能判定一次：2 张或 3 张。 */
    protected abstract int getCardsPerFlip();

    /** 一共有多少组同点数的牌。 */
    protected abstract int getGroupCount();

    /** 该玩法历史战绩的序列化文件名。 */
    protected abstract String getHistoryFileName();

    /** 该玩法的标题文字资源。 */
    protected abstract int getTitleRes();

    /** 该玩法的规则说明资源。 */
    protected abstract int getHintRes();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ArrayList<Card> cards = new ArrayList<>();
    private final ArrayList<Integer> selected = new ArrayList<>();
    private final ArrayList<Integer> matched = new ArrayList<>();
    private final ArrayList<GameRecord> history = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
    private final Random random = new Random();

    private TextView[] cardViews;
    private TextView textScore;
    private TextView textFlips;
    private TextView textMatched;
    private TextView textStatus;
    private TextView textHistory;
    private GridLayout layoutCards;

    private int score;
    private int flips;
    private boolean busy;
    private int cardHeightPx;
    private int cardMarginPx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textScore = view.findViewById(R.id.text_score);
        textFlips = view.findViewById(R.id.text_flips);
        textMatched = view.findViewById(R.id.text_matched);
        textStatus = view.findViewById(R.id.text_status);
        textHistory = view.findViewById(R.id.text_history);
        layoutCards = view.findViewById(R.id.layout_cards);

        view.<TextView>findViewById(R.id.text_game_title).setText(getTitleRes());
        view.<TextView>findViewById(R.id.text_hint).setText(getHintRes());
        view.findViewById(R.id.button_restart).setOnClickListener(v -> startNewGame());

        float density = getResources().getDisplayMetrics().density;
        cardHeightPx = (int) (78 * density);
        cardMarginPx = (int) (4 * density);

        loadHistory();
        startNewGame();
        renderHistory();
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    /**
     * 重新发牌开始一局新游戏。
     */
    private void startNewGame() {
        handler.removeCallbacksAndMessages(null);
        cards.clear();
        selected.clear();
        matched.clear();
        score = 0;
        flips = 0;
        busy = false;

        String[] ranks = getResources().getStringArray(R.array.card_ranks);
        String[] suits = getResources().getStringArray(R.array.card_suits);
        int perFlip = getCardsPerFlip();
        for (int group = 0; group < getGroupCount(); group++) {
            String rank = ranks[group % ranks.length];
            String suit = suits[group % suits.length];
            for (int i = 0; i < perFlip; i++) {
                cards.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(cards, random);

        buildCardViews();
        textStatus.setText(perFlip == 2 ? "请翻开两张牌，点数相同即配对成功"
                : "请翻开三张牌，三张点数相同即配对成功");
        updateStats();
    }

    private void buildCardViews() {
        layoutCards.removeAllViews();
        cardViews = new TextView[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            TextView cardView = new TextView(requireContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));
            params.width = 0;
            params.height = cardHeightPx;
            params.setMargins(cardMarginPx, cardMarginPx, cardMarginPx, cardMarginPx);
            cardView.setLayoutParams(params);
            cardView.setGravity(Gravity.CENTER);
            cardView.setTextSize(20);
            cardView.setTextColor(getResources().getColor(R.color.card_back_text, null));
            cardView.setBackgroundResource(R.drawable.bg_card_back);
            cardView.setText(R.string.card_back_symbol);
            final int index = i;
            cardView.setOnClickListener(v -> onCardClicked(index));
            layoutCards.addView(cardView);
            cardViews[i] = cardView;
        }
    }

    private void onCardClicked(int index) {
        if (busy || matched.contains(index) || selected.contains(index)) {
            return;
        }
        selected.add(index);
        flips++;
        score--;
        showFace(index, true);
        updateStats();

        if (selected.size() == getCardsPerFlip()) {
            busy = true;
            handler.postDelayed(this::checkSelection, 700);
        }
    }

    /**
     * 判定本次翻开的牌是否配对成功。
     */
    private void checkSelection() {
        String firstRank = cards.get(selected.get(0)).getRank();
        boolean same = true;
        for (Integer index : selected) {
            if (!firstRank.equals(cards.get(index).getRank())) {
                same = false;
                break;
            }
        }

        int perFlip = getCardsPerFlip();
        if (same) {
            for (Integer index : selected) {
                matched.add(index);
                showMatched(index);
            }
            score += 12 * perFlip;
            textStatus.setText("配对成功！得分 +" + (12 * perFlip));
        } else {
            for (Integer index : selected) {
                showFace(index, false);
            }
            score -= 2 * perFlip;
            textStatus.setText("点数不同，配对失败，得分 -" + (2 * perFlip));
        }
        selected.clear();
        busy = false;
        updateStats();

        if (matched.size() == cards.size()) {
            finishGame();
        }
    }

    private void showFace(int index, boolean chosen) {
        TextView cardView = cardViews[index];
        cardView.setText(cards.get(index).getText());
        cardView.setTextColor(getResources().getColor(R.color.text_red, null));
        cardView.setBackgroundResource(chosen ? R.drawable.bg_card_chosen : R.drawable.bg_card_front);
    }

    private void showMatched(int index) {
        TextView cardView = cardViews[index];
        cardView.setText(cards.get(index).getText());
        cardView.setTextColor(getResources().getColor(R.color.red_primary, null));
        cardView.setBackgroundResource(R.drawable.bg_card_matched);
    }

    private void hideFace(int index) {
        TextView cardView = cardViews[index];
        cardView.setText(R.string.card_back_symbol);
        cardView.setTextColor(getResources().getColor(R.color.card_back_text, null));
        cardView.setBackgroundResource(R.drawable.bg_card_back);
    }

    private void updateStats() {
        textScore.setText(getString(R.string.score_format, score));
        textFlips.setText(getString(R.string.flip_format, flips));
        textMatched.setText(getString(R.string.matched_format, matched.size() / getCardsPerFlip(),
                getGroupCount()));
    }

    /**
     * 一局结束：把战绩写入序列化文件并刷新历史记录列表。
     */
    private void finishGame() {
        GameRecord record = new GameRecord(System.currentTimeMillis(), score, flips, getGroupCount());
        history.add(0, record);
        while (history.size() > MAX_HISTORY) {
            history.remove(history.size() - 1);
        }
        ObjectStore.write(requireContext(), getHistoryFileName(), history);
        renderHistory();

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.win_title)
                .setMessage(getString(R.string.win_message_format, getGroupCount(), flips, score))
                .setPositiveButton(R.string.win_positive, (dialog, which) -> startNewGame())
                .setCancelable(false)
                .show();
    }

    @SuppressWarnings("unchecked")
    private void loadHistory() {
        Object data = ObjectStore.read(requireContext(), getHistoryFileName());
        if (data instanceof ArrayList) {
            history.clear();
            history.addAll((ArrayList<GameRecord>) data);
        }
    }

    private void renderHistory() {
        if (history.isEmpty()) {
            textHistory.setText(R.string.history_empty);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            GameRecord record = history.get(i);
            builder.append(getString(R.string.game_history_item_format,
                            i + 1,
                            record.getScore(),
                            record.getFlips(),
                            dateFormat.format(new Date(record.getTime()))))
                    .append('\n');
        }
        textHistory.setText(builder.toString().trim());
    }

    /**
     * 还原全部卡片背面（用于重新开始后清屏）。
     */
    @SuppressWarnings("unused")
    private void resetAllCards() {
        for (int i = 0; i < cardViews.length; i++) {
            hideFace(i);
        }
    }
}
