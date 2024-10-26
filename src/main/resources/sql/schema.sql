CREATE TABLE if not exists user_g (
   id bigint auto_increment,
   telegram_id VARCHAR(255),
   first_name VARCHAR(255),
   last_name VARCHAR(255),
   username VARCHAR(255),
   phone VARCHAR(255),
   balance DOUBLE PRECISION NOT NULL,
   credit INT NOT NULL,
   deleted BOOLEAN NOT NULL,
   timestamp TIMESTAMP,
   CONSTRAINT pk_user_g PRIMARY KEY (id)
);

CREATE TABLE if not exists game (
   id bigint auto_increment,
   name VARCHAR(255),
   admin_id bigint,
   min_players INT NOT NULL,
   max_players INT NOT NULL,
   min_bet DOUBLE PRECISION NOT NULL,
   game_type VARCHAR(255),
   started_at TIMESTAMP,
   status VARCHAR(255),
   CONSTRAINT pk_game PRIMARY KEY (id)
);

CREATE TABLE if not exists participant (
   id bigint auto_increment,
   game_id bigint,
   user_id bigint,
   bet_amount DOUBLE PRECISION,
   status VARCHAR(255),
   CONSTRAINT pk_participant PRIMARY KEY (id)
);

CREATE TABLE if not exists game_participants (
   game_id bigint,
   participants_id bigint
);


ALTER TABLE participant ADD CONSTRAINT if not exists FK_PARTICIPANT_ON_GAME FOREIGN KEY (game_id) REFERENCES game (id);


ALTER TABLE game_participants ADD CONSTRAINT if not exists uc_game_participants_participants UNIQUE (participants_id);

ALTER TABLE game_participants ADD CONSTRAINT if not exists fk_gampar_on_game FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_participants ADD CONSTRAINT if not exists fk_gampar_on_participant FOREIGN KEY (participants_id) REFERENCES participant (id);


CREATE TABLE if not exists result (
   id bigint auto_increment,
   game_id bigint,
   game_type VARCHAR(255),
   started_at TIMESTAMP,
   finished_at TIMESTAMP,
   CONSTRAINT pk_result PRIMARY KEY (id)
);

CREATE TABLE if not exists result_amount (
   id bigint auto_increment,
   result_id bigint,
   participant_id bigint,
   amount DOUBLE PRECISION,
   CONSTRAINT pk_resultamount PRIMARY KEY (id)
);

CREATE TABLE if not exists result_result_amounts (
   result_id bigint,
   result_amounts_id bigint
);


ALTER TABLE result_amount ADD CONSTRAINT if not exists FK_RESULTAMOUNT_ON_PARTICIPANTID FOREIGN KEY (participant_id) REFERENCES participant (id);

ALTER TABLE result_amount ADD CONSTRAINT if not exists FK_RESULTAMOUNT_ON_RESULTID FOREIGN KEY (result_id) REFERENCES result (id);


ALTER TABLE result_result_amounts ADD CONSTRAINT if not exists uc_result_result_amounts_resultamounts UNIQUE (result_amounts_id);

ALTER TABLE result_result_amounts ADD CONSTRAINT if not exists fk_resresamo_on_result FOREIGN KEY (result_id) REFERENCES result (id);

ALTER TABLE result_result_amounts ADD CONSTRAINT if not exists fk_resresamo_on_result_amount FOREIGN KEY (result_amounts_id) REFERENCES result_amount (id);

