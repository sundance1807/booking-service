CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    firstName  VARCHAR(100) NOT NULL,
    secondName VARCHAR(100)
);

CREATE TABLE rooms (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(150) NOT NULL UNIQUE,
    floor    INT,
    capacity INT
);

CREATE TABLE slots (
    id   BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE bookings (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    start_time  TIMESTAMP    NOT NULL,
    end_time    TIMESTAMP    NOT NULL,
    user_id     BIGINT       NOT NULL,
    room_id     BIGINT       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE
);

CREATE TABLE booked_slots (
    date       DATE   NOT NULL,
    booking_id BIGINT NOT NULL,
    slot_id    BIGINT NOT NULL,
    room_id    BIGINT NOT NULL,
    CONSTRAINT unique_booking UNIQUE (date, slot_id, room_id),
    FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id) REFERENCES slots (id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE
);
