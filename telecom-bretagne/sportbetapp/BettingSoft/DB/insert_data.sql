-- Title :    	create_database.sql
-- Version :  	0.0.1 (alpha)
-- Date :     	8 May 2016
-- Names :		   Andrei-Florin BENCSIK, Ahmed SAMI MOHAMED, Clara GALIMBERTI, Ignacio EVANGELISTA, Leonard TOSHIMORI, Bich Phoung LUC
-- Description :  Database for FIL ROUGE group 4 AB
--
/* clean up */
DROP TABLE IF EXISTS Participant;
DROP TABLE IF EXISTS Bet;
DROP TABLE IF EXISTS Competition;
DROP TABLE IF EXISTS Competitor;
DROP TABLE IF EXISTS Subscriber;
DROP TABLE IF EXISTS Manager;

/* recreate the tables from scratch */

CREATE TABLE Manager(
	password varchar(50)
);
COMMENT ON TABLE Manager IS 'Table to store manager information.';

CREATE TABLE Subscriber(
    username varchar(30) primary key,
    first_name varchar(30) NOT NULL,
    last_name varchar(30) NOT NULL,
    password varchar(30) NOT NULL,
    born_date date NOT NULL,
    credit integer NOT NULL
);
COMMENT ON TABLE Subscriber IS 'Table to store subscribers and their info .';

CREATE TABLE Competitor
(
	id serial primary key,    
	typeOf integer,
	first_name varchar(30),
	last_name varchar(30),
	born_date date,
	team_name varchar(50),
	id_team integer references COMPETITOR(id)
);
COMMENT ON TABLE Competitor IS 'Competitors their names and if they are teams their roster. 1:individual 2:team';


CREATE TABLE Competition (    
	name VARCHAR(50) PRIMARY KEY,    
	starting_date date,    
	closing_date date,    
	status INT NOT NULL,    
    sport  VARCHAR(30)  NOT NULL,
    id_winner INT REFERENCES Competitor(id),
	id_second INT REFERENCES Competitor(id),    
	id_third INT REFERENCES Competitor(id)
);
COMMENT ON TABLE Competition IS 'Table to store Competitions, their names and winners .';

CREATE TABLE Participant
(
	id_competitor INT REFERENCES Competitor(id),    
	name_competition VARCHAR(50) REFERENCES Competition(name) 	 
);
COMMENT ON TABLE Participant IS 'Joint table for Competitor and Competition .';

CREATE TABLE Bet
(
	id serial PRIMARY KEY,
	username VARCHAR(30) REFERENCES Subscriber(username),
	name_comp VARCHAR(50) REFERENCES Competition(name),
	bet_date date,
	type integer,
	id_winner integer references Competitor(id),
	id_second integer references Competitor(id),
	id_third integer references Competitor(id),
	nb_tokens integer,    
	status integer,
	earnings integer
);
COMMENT ON TABLE Bet IS 'Table to store the bets, their owner, their competition and their winner/winners. bet 1:winner 2:podium / 1:in progress 2:win 3:lost 4:canceled <- discuss';
DROP FUNCTION IF EXISTS insert_subscriber(character varying, character varying, character varying, character varying, date);
DROP FUNCTION IF EXISTS insert_competition(character varying, date, date, integer, character varying);
DROP FUNCTION IF EXISTS insert_podium_bet(character varying, character varying, integer, integer, integer, integer);
DROP FUNCTION IF EXISTS insert_participant(character varying, integer);
DROP FUNCTION IF EXISTS insert_individual_competitor(character varying, character varying, date);
DROP FUNCTION IF EXISTS delete_team(integer);
DROP FUNCTION IF EXISTS delete_subscriber(character varying);
DROP FUNCTION IF EXISTS delete_participant(character varying, integer);
DROP FUNCTION IF EXISTS delete_individual_competitor(integer);
DROP FUNCTION IF EXISTS delete_bet(integer);
DROP FUNCTION IF EXISTS add_to_team(integer, integer);
DROP FUNCTION IF EXISTS competitions_for_competitor(integer);

CREATE OR REPLACE FUNCTION insert_subscriber(
	username varchar(30),
	first_name varchar(30),
	last_name varchar(30),
	pswd varchar(30),
	born_date date
)
RETURNS void AS $$
BEGIN
	INSERT INTO Subscriber VALUES
	(
    	username,
    	first_name,
    	last_name,
    	pswd,
    	born_date,
    	0
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_subscriber(
	user_name varchar(30)
	)
RETURNS void AS $$
BEGIN
	DELETE FROM Subscriber
    	WHERE user_name LIKE username;
    
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_individual_competitor(
	first_name varchar(30),
	last_name varchar(30),
	born_date date
)
RETURNS void AS $$
BEGIN
	INSERT INTO Competitor VALUES
	(
    	nextval('competitor_id_seq'),
    	1,
    	first_name,
    	last_name,
    	born_date,
    	null,
    	null
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_individual_competitor(
	id_individual int
)
RETURNS void AS $$
BEGIN
	DELETE FROM Competitor
	WHERE id_individual = id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_team(
	team_name varchar(30)
	)
RETURNS void AS $$
BEGIN
	INSERT INTO Competitor VALUES
	(
    	nextval('competitor_id_seq'),
    	2,
    	NULL,
    	NULL,
    	NULL,
    	team_name,
    	null
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_team(
	team_id int
)
RETURNS void AS $$
BEGIN
	DELETE FROM Competitor
	WHERE team_id = id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_competition(
	nameOf varchar(50),
	starting_date date,
	closing_date date,
	status int,
	sport varchar(30)
)
RETURNS void AS $$
BEGIN
	INSERT INTO Competition VALUES
	(
    	nameOf,
    	starting_date,
    	closing_date,
    	status,
    	sport,
    	NULL,
    	NULL,
    	NULL
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_competition(
	competition_name varchar(50)
)
RETURNS void AS $$
BEGIN
	DELETE FROM Competition
	WHERE competition_name LIKE name;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_participant(
	competitor_id int,
	competition_name varchar(50)
)
RETURNS void AS $$
BEGIN
	INSERT INTO Participant VALUES
	(
    competitor_id,
    	competition_name   	 
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_participant(
	competition_name varchar(50),
	competitor_id int
)
RETURNS void AS $$
BEGIN
	DELETE FROM Participant
	WHERE competitor_id = id_competitor AND competition_name LIKE name_competition;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_winner_bet(
	uname varchar(30),
	comp_name varchar(50),
	winner_id int,
	tokens int
)
RETURNS void AS $$
BEGIN
	INSERT INTO Bet VALUES
	(
    	nextval('bet_id_seq'),
    	uname,
    	comp_name,
    	1,
    	winner_id,
    	NULL,
    	NULL,
    	tokens,
    	0
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_podium_bet(
	uname varchar(30),
	comp_name varchar(50),
	winner_id int,
	second_id int,
	third_id int,
	tokens int
)
RETURNS void AS $$
BEGIN
	INSERT INTO Bet VALUES
	(
    	nextval('bet_id_seq'),
    	uname,
    	comp_name,
    	2,
    	winner_id,
    	second_id,
    	third_id,
    	tokens,
    	0
    	);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_bet(
	bet_id int
)
RETURNS void AS $$
BEGIN
	DELETE FROM Bet
	WHERE bet_id = id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_to_team(
	player_id int,
	team_id int
)
RETURNS void AS $$
BEGIN
	UPDATE Competitor
    SET id_team = team_id
    WHERE id = player_id;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION competitions_for_competitor(competitor_id int)
RETURNS TABLE (
    competition varchar(50)
) AS $$
BEGIN
	RETURN QUERY
	SELECT name_competition FROM Participant
	WHERE competitor_id = id_competitor;
END;
$$ LANGUAGE plpgsql;

select competitions_for_competitor(8);
/* insert some subscribers 
***************************************************************/
select insert_subscriber('afbencsi','Andrei','Bencsik', '123456789', '1992-04-05');
select insert_subscriber('asamimoh','Ahmed','Sami', 'abcdefghij', '1991-03-01');
select insert_subscriber('cgalimber','Clara','Galimberti', 'mypasswordis', '1989-02-01');
select insert_subscriber('ievangel','Ignacio','Ecangelista', '1a2b3c4d', '1962-04-05');
select insert_subscriber('ltoshimor','Leonard','Toshimori', 'thispassowrd', '1972-04-05');
select insert_subscriber('lbich','Pfuong','Luc', 'onemorepass', '1945-04-05');
select insert_subscriber('ptanguy','Philippe','Tanguy', 'password', '1975-04-05');

/***************************************************************
    insert some players f_name, l_name, birthday
***************************************************************/
select insert_individual_competitor('Cristiano','Ronaldo','1986-04-05');
select insert_individual_competitor('James','Rodriguez','1986-04-05');
select insert_individual_competitor('Karim','Benzema','1986-04-05');

select insert_individual_competitor('Leo','Messi','1986-04-05');
select insert_individual_competitor('L','Suarez','1986-04-05');
select insert_individual_competitor('Neymar','Jr','1986-04-05');

select insert_individual_competitor('Fernando','Torres','1986-04-05');
/***************************************************************
    insert some teams by name 
***************************************************************/
select insert_team('Real Madrid');
select insert_team('Barcelona');
select insert_team('Atletico Madrid');
/***************************************************************
    add some players to teams
***************************************************************/
select add_to_team(1, 8);
select add_to_team(2, 8);
select add_to_team(3, 8);

select add_to_team(4, 9);
select add_to_team(5, 9);
select add_to_team(6, 9);

select add_to_team(7, 10);

/* (nameof character varying, starting_date timestamp without time zone,
closing_date timestamp without time zone, status integer, sport character varying) */
select insert_competition('Primera Division Real-Barcelona', '2016-04-05', '2016-04-05', 1, 'football');
select insert_competition('Primera Division Real-Barcelona Rematch', '2016-05-05', '2016-05-05', 1, 'football');
select insert_competition('Champions league 2016', '2015-05-05', '2016-05-05', 1, 'football');
