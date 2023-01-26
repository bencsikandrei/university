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
