create TABLE IF NOT EXISTS booking_status (
    status_name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_booking_status PRIMARY KEY (status_name)
);

create TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    available BOOLEAN,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

create TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    booker_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booker FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_status FOREIGN KEY (status) REFERENCES booking_status(status_name)
);

create TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(255) NOT NULL,
    author_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_booker_id FOREIGN KEY (author_id) REFERENCES users (id)
);

TRUNCATE TABLE comments;
TRUNCATE TABLE bookings;
TRUNCATE TABLE items;
TRUNCATE TABLE users;

