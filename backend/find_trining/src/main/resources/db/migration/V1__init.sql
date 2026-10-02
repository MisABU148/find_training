CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    full_name  VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,
    phone      VARCHAR(20),
    role       VARCHAR(20)   NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE gyms
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150)  NOT NULL,
    address     VARCHAR(255)  NOT NULL,
    city        VARCHAR(100)  NOT NULL,
    phone       VARCHAR(20),
    description VARCHAR(2000),
    created_at  TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE sports
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE coaches
(
    id               BIGSERIAL PRIMARY KEY,
    full_name        VARCHAR(100)  NOT NULL,
    email            VARCHAR(150)  NOT NULL UNIQUE,
    phone            VARCHAR(20),
    bio              VARCHAR(2000),
    experience_years INTEGER       NOT NULL DEFAULT 0,
    gym_id           BIGINT REFERENCES gyms (id) ON DELETE SET NULL,
    created_at       TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE coach_sport
(
    coach_id BIGINT NOT NULL REFERENCES coaches (id) ON DELETE CASCADE,
    sport_id BIGINT NOT NULL REFERENCES sports (id) ON DELETE CASCADE,
    PRIMARY KEY (coach_id, sport_id)
);

CREATE INDEX idx_coaches_gym_id ON coaches (gym_id);
