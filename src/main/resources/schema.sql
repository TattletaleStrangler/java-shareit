DROP TABLE IF EXISTS "comments" CASCADE;
DROP TABLE IF EXISTS "bookings" CASCADE;
DROP TABLE IF EXISTS "items" CASCADE;
DROP TABLE IF EXISTS "requests" CASCADE;
DROP TABLE IF EXISTS "users" CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(255) NOT NULL,
    email varchar(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description varchar(512) NOT NULL,
    requestor_id BIGINT NOT NULL,
    created timestamp NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(512) NOT NULL,
    available boolean,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
    CONSTRAINT fk_item_to_request FOREIGN KEY(request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    booker_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    status varchar(20) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
    CONSTRAINT fk_booking_to_item FOREIGN KEY(item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text varchar(512) NOT NULL,
    author_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    created_date timestamp NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_to_item FOREIGN KEY(item_id) REFERENCES items(id)
);