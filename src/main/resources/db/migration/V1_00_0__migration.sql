CREATE TABLE lobby(
    id VARCHAR(255) PRIMARY KEY,
    lobby_state VARCHAR(255) NOT NULL,
    create_datetime TIMESTAMP WITH TIME ZONE
);

CREATE TABLE player (
    name VARCHAR(255) NOT NULL PRIMARY KEY,
    lobby_id VARCHAR(255) NOT NULL REFERENCES lobby(id),
    client_id VARCHAR(255) NOT NULL
);

CREATE TABLE game (
    id SERIAL PRIMARY KEY,
    round_time_minutes INTEGER,
    round_end_datetime TIMESTAMP WITH TIME ZONE,
    min_words_per_message INTEGER,
    max_words_per_message INTEGER,
    lobby_id VARCHAR(255) NOT NULL REFERENCES lobby(id)
);

CREATE TABLE story (
    id SERIAL PRIMARY KEY,
    creator_player_name VARCHAR(255) NOT NULL REFERENCES player(name),
    editing_player_name VARCHAR(255) NOT NULL REFERENCES player(name),
    game_id INTEGER NOT NULL REFERENCES game(id),
    story_state VARCHAR(255) NOT NULL
);

CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    creator_player_name VARCHAR(255) NOT NULL REFERENCES player(name),
    story_id INTEGER NOT NULL REFERENCES story(id),
    text VARCHAR(65535) NOT NULL --TODO revisit size
);

ALTER ROLE postgres SET search_path TO write_shite;

