package com.example.a2024110541lab6;

import java.io.Serializable;

/**
 * 一张扑克牌。点数用于配对判断，花色只用于显示。
 */
public class Card implements Serializable {

    private static final long serialVersionUID = 2024110544L;

    private final String rank;
    private final String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getText() {
        return rank + suit;
    }
}
