package com.booking_service.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageSource {
    SUCCESS_REGISTRATION("Вы успешно зарегистрировались."),
    USER_NOT_FOUND("Пользователь '%s' не найден."),
    USERNAME_ALREADY_EXISTS("Пользователь с юзернеймом '%s' существует."),
    ROOM_NOT_FOUND("Комната с id: '%s' не найдена."),
    TIME_SLOTS_ALREADY_USED("Выбранные период занят"),
    TIME_SLOTS_NOT_FOUND("Временные периоды не найдены"),
    BOOKING_TIME_NOT_AVAILABLE("Данный период бронирования уже занят"),
    ;
    private final String text;

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
