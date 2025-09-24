package com.bank.exchange_generator.enums;

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
