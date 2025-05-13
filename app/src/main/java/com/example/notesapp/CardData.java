package com.example.notesapp;

public class CardData {
    private String cardViewTitle;
    private String cardViewInner;

    public CardData(String cardViewInner, String cardViewTitle) {
        this.cardViewInner = cardViewInner;
        this.cardViewTitle = cardViewTitle;
    }

    public String getCardViewTitle() {
        return cardViewTitle;
    }

    public void setCardViewTitle(String cardViewTitle) {
        this.cardViewTitle = cardViewTitle;
    }

    public String getCardViewInner() {
        return cardViewInner;
    }

    public void setCardViewInner(String cardViewInner) {
        this.cardViewInner = cardViewInner;
    }
}
