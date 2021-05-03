create table if not exists accounts (
	ID bigint auto_increment,
	UUID varchar(255),
	MC_VERSION varchar(10),
	GAME_TYPE varchar(15),
	INVENTORY blob,
	MAX_SIZE int,
	LOCKED boolean default false,
	CREATED timestamp default current_timestamp(),
	MODIFIED timestamp as current_timestamp(),
	PRIMARY KEY (UUID, MC_VERSION, GAME_TYPE)
);