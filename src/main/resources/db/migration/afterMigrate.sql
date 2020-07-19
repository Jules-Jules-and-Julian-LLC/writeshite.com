INSERT INTO lobby(id, lobby_state, create_datetime)
	VALUES ('orio', 'GATHER_PLAYERS', clock_timestamp());

INSERT INTO player(name, lobby_id, client_id)
	VALUES ('julian', 'orio', 'asdf1234');