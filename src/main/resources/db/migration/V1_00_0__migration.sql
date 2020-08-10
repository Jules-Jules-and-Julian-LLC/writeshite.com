CREATE TABLE lobby(
    lobby_id VARCHAR(255) PRIMARY KEY,
    lobby_state VARCHAR(255) NOT NULL,
    create_datetime TIMESTAMP WITH TIME ZONE,
    last_update_datetime TIMESTAMP WITH TIME ZONE
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

--Triggers to update lobby's last_update_datetime when any child objects change.
--TODO can lobby safely trigger an update on itself when it changes state?
CREATE OR REPLACE FUNCTION updateLobbyLastUpdateLobbyId() RETURNS TRIGGER AS $update_lobby_last_update_lobby_id$
BEGIN
    UPDATE lobby
    SET last_update_datetime = clock_timestamp()
    WHERE lobby_id = NEW.lobby_id;
    RETURN NEW;
END;
$update_lobby_last_update_lobby_id$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updateLobbyLastUpdateGameId() RETURNS TRIGGER AS $update_lobby_last_update_game_id$
BEGIN
    UPDATE lobby
    SET last_update_datetime = clock_timestamp()
    FROM game
    WHERE game.lobby_id = lobby.lobby_id
        AND game.id = NEW.game_id;
    RETURN NEW;
END;
$update_lobby_last_update_game_id$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updateLobbyLastUpdateStoryId() RETURNS TRIGGER AS $update_lobby_last_update_story_id$
BEGIN
    UPDATE lobby
    SET last_update_datetime = clock_timestamp()
    FROM game, story
    WHERE game.lobby_id = lobby.lobby_id
        AND story.game_id = game.id
        AND story.id = NEW.story_id;
    RETURN NEW;
END;
$update_lobby_last_update_story_id$ LANGUAGE plpgsql;

CREATE TRIGGER update_lobby_last_update_game_changes AFTER INSERT OR UPDATE OR DELETE ON game
FOR EACH ROW EXECUTE PROCEDURE updateLobbyLastUpdateLobbyId();

CREATE TRIGGER update_lobby_last_update_player_changes AFTER INSERT OR UPDATE OR DELETE ON player
FOR EACH ROW EXECUTE PROCEDURE updateLobbyLastUpdateLobbyId();

CREATE TRIGGER update_lobby_last_update_story_changes AFTER INSERT OR UPDATE OR DELETE ON story
FOR EACH ROW EXECUTE PROCEDURE updateLobbyLastUpdateGameId();

CREATE TRIGGER update_lobby_last_update_message_changes AFTER INSERT OR UPDATE OR DELETE ON message
FOR EACH ROW EXECUTE PROCEDURE updateLobbyLastUpdateStoryId();

ALTER ROLE postgres SET search_path TO write_shite;

