package com.booking_service.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageSource {

    SUCCESS_REGISTRATION("Вы успешно зарегистрировались."),
    USER_NOT_FOUND("Пользователь '%s' не найден."),
    USERNAME_ALREADY_EXISTS("Пользователь с юзернеймом '%s' существует."),
    TIME_SLOT_ALREADY_EXISTS("Слот '%s' уже существует."),
    TIME_SLOT_NOT_FOUND("Слот не найден, id: %s."),
    ROOM_NAME_ALREADY_EXISTS("Комната '%s' уже существует."),
    ROOM_NAME_NOT_FOUND("Комната не найдена, id: %s."),
    ROOM_NOT_FOUND("Комната с id: '%s' не найдена.");

    private String text;

    public String getText(String... params) {
        return String.format(this.text, params);
    }

}
