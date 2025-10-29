package com.bank.blocker.enums;

public enum Currency {
    EUR("Евро"),
    RUB("Рубль"),
    USD("Доллар");
    private String title;

    Currency(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
