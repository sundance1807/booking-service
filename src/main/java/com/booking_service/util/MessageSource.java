package com.booking_service.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageSource {

    SUCCESS_REGISTRATION("Вы успешно зарегистрировались."),
    USER_NOT_FOUND("Пользователь '%s' не найден."),
    USERNAME_ALREADY_EXISTS("Пользователь с юзернеймом '%s' существует."),
    TELEGRAM_LINK_ALREADY_EXISTS("'%s' уже существует."),
    ROOM_NOT_FOUND("Комната с id: '%s' не найдена."),
    BOOKING_TIME_NOT_AVAILABLE("Данный период бронирования уже занят"),
    ROOM_NAME_ALREADY_EXISTS("Комната '%s' уже существует."),
    ROOM_NAME_NOT_FOUND("Комната не найдена, id: %s."),
    BOOKING_NOT_FOUND("Запись с id: '%s' не найдена."),
    UNABLE_DELETE_OTHER_BOOKINGS("Невозможно удалить чужую бронь."),

    ;
    private final String text;

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
