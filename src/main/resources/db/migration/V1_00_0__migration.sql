CREATE TABLE lobby(
    lobby_id VARCHAR(255) PRIMARY KEY,
    lobby_state VARCHAR(255) NOT NULL,
    create_datetime TIMESTAMP WITH TIME ZONE
);

CREATE TABLE player (
    username VARCHAR(255) NOT NULL,
    lobby_id VARCHAR(255) NOT NULL REFERENCES lobby(lobby_id),
    client_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (username, lobby_id)
);

CREATE TABLE game (
    id SERIAL PRIMARY KEY,
    round_time_minutes INTEGER,
    round_end_datetime TIMESTAMP WITH TIME ZONE,
    min_words_per_message INTEGER,
    max_words_per_message INTEGER,
    lobby_id VARCHAR(255) NOT NULL REFERENCES lobby(lobby_id)
);

CREATE TABLE story (
    id SERIAL PRIMARY KEY,
    game_id INTEGER NOT NULL REFERENCES game(id),
    creator_player_name VARCHAR(255) NOT NULL,
    editing_player_name VARCHAR(255) NOT NULL,
    story_state VARCHAR(255) NOT NULL
);

CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    story_id INTEGER NOT NULL REFERENCES story(id),
    creator_player_name VARCHAR(255) NOT NULL,
    text VARCHAR(65535) NOT NULL --TODO revisit size
);

ALTER ROLE postgres SET search_path TO write_shite;

