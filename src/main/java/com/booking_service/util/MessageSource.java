package com.booking_service.util;

public enum MessageSource {

    TIME_SLOT_ALREADY_EXISTS("Слот '%s' уже существует."),
    TIME_SLOT_NOT_FOUND("Слот не найден, id: %s."),
    ROOM_NAME_ALREADY_EXISTS("Комната '%s' уже существует."),
    ROOM_NAME_NOT_FOUND("Комната не найдена, id: %s.");

    private String text;

    MessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, params);
    }

}
