package com.booking_service.util;

public enum MessageSource {
    SUCCESS_REGISTRATION("Вы успешно зарегистрировались."),
    USER_NOT_FOUND("Пользователь '%s' не найден."),
    USERNAME_ALREADY_EXISTS("Пользователь с юзернеймом '%s' существует."),
    TIME_SLOT_ALREADY_EXISTS("Слот '%s' уже существует."),
    TIME_SLOT_NOT_FOUND("Слот не найден, id: %s."),

    ;
    private String text;

    MessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
