INSERT INTO write_shite.lobby(id, lobby_state, create_datetime)
	VALUES ('orio', 'GATHER_PLAYERS', clock_timestamp());

INSERT INTO write_shite.player(name, lobby_id, client_id)
	VALUES ('julian', 'orio', 'asdf1234');