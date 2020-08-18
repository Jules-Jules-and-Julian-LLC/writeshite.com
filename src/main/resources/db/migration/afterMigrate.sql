INSERT INTO lobby(lobby_id, state, create_datetime, creator_username)
	VALUES ('orio', 'GATHERING_PLAYERS', clock_timestamp(), 'julian');

INSERT INTO player(username, lobby_id, client_id)
	VALUES ('julian', 'orio', 'asdf1234');

INSERT INTO game(round_time_minutes, round_end_datetime, min_words_per_message, max_words_per_message, lobby_id)
    VALUES(10, clock_timestamp(), null, null, 'orio');

INSERT INTO story(state, creator_username, editing_username, game_id)
    VALUES ('ACTIVE', 'julian', 'julian', 1);

INSERT INTO message(creator_username, story_id, text)
    VALUES ('julian', 1, 'hello');