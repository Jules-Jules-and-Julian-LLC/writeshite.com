CREATE TABLE player (
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE lobby(
    id SERIAL PRIMARY KEY,
    lobby_state varchar(255) NOT NULL,
    create_datetime timestamp with time zone
);

CREATE TABLE game (
    id SERIAL PRIMARY KEY,
    round_time_minutes integer,
    round_end_datetime timestamp with time zone,
    min_words_per_message integer,
    max_words_per_message integer,
    lobby_id integer NOT NULL REFERENCES lobby(id)
);

CREATE TABLE story (
    id SERIAL PRIMARY KEY,
    creator_player_id integer NOT NULL REFERENCES player(id),
    editing_player_id integer NOT NULL REFERENCES player(id),
    game_id integer NOT NULL REFERENCES game(id),
    story_state varchar(255) NOT NULL
);

CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    creator_player_id integer NOT NULL REFERENCES player(id),
    story_id integer NOT NULL REFERENCES story(id),
    text varchar(65535) NOT NULL --TODO revisit size
);
