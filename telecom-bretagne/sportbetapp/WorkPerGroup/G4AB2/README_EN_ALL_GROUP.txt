BettingSoft 
Version 0.1 (BETA)

All rights reserved.

Developped by Dev4a (Telecom Bretagne)


!NOTE! For any problems in the software please report to one of the members of the team. We are prepared to help and support you.

e-mails:
bich.luc@telecom-bretagne.eu
ahmed.samimohamed@telecom-bretagne.eu
clara.galimberti@telecom-bretagne.eu
ignacio.evangelista@telecom-bretagne.eu
leonard.toshimori@telecom-bretagne.eu
af.bencsik@telecom-bretagne.eu

Thank you for the feedback and hope you like our software :)

Preparing and installing the requirements
With the Admin account create a role called “manager”.
With the Admin account run the script to create a DB called BETTING with the owner “manager”. 
Creating the database:
    -- DROP DATABASE IF EXISTS "BETTING";

CREATE DATABASE "BETTING"
  WITH OWNER = manager
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'fr_FR.UTF-8'
       LC_CTYPE = 'fr_FR.UTF-8'
       CONNECTION LIMIT = -1;

After creating the DB, connect with the account “manager” and run the following sql script to create the DB (this script is also available in the project folder under DB/create_database.sql, but for the sake of completion here it is)

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

!NOTE! 
By default the first time that the System is launched all tables in the DB are truncated (with CASCADE option). To avoid this insert a password for the manager (with postgres).

If there is a need for some data in the DB, we offer another script to fill some “dummy” data. 
Everything that is don’t in the script can be done with the CLIs that will be presented in the following section. (remember to insert a manager pass !)
The script for insertion is located under BettingSoft/DB/insert_data.sql. (as this is not mandatory, we will not list it here)

Running the application
To run the application we offer two CLIs: one for the client (subscriber) and one for the manager. The two are very similar.
In order to launch the system two JARs are available. 

To launch the manager: 
navigate to the BettingSoft folder in the cloned repository
write: java -jar bettingSoft.jar
To launch the client (subscriber):
navigate to the BettingSoft folder in the cloned repository
write: java -jar clientBettingSoft.jar

First launch manager:
if the database is empty you will be greeted with a message telling you “Welcome” and offering your default password ( “1234” )
from here you can start adding subscribers, competitor, competitions etc 
if creating a subscriber -> the new generated pass will be returned ! Keep this somewhere to be able to test the Subscriber afterwards! (if not, look in the DB)
you can also list the existing ones
First launch client:
the first time you launch the client it asks for the username and pass (be careful to have created a subscriber before this and keep the generated password!)
from here on you can place bets, consult informations, change your password, etc.

Some observations concerning some functionalities deviations from the interface/specifications
When a competitor is created, he is stored in the database even though he might not have a competition.
When a competitor is removed from a competition he is not removed from the database, which seems logic in case you want to add him to another competition.
You should not be able to settleWinner or settlePodium in a competition whose end date is in the future. This would certainly pose a problem to test this function since you would need to wait some days to be able to test these functions.
idem for deleting a competition 
It is only possible to delete a competition if it is cancelled or sold out (i.e. already paid) thus to delete a competition on course, first cancel it.

COMMENTS ON THE USE OF THE INTERFACE
When adding competitors to a competition, -1 stops the procedure.

SOME SCENARIOS TO TEST THE SYSTEM
Simple competition creation and betting
            As the MANAGER
Manage competitors
Create two team competitors
Manage competitions
Create a competition with the two recently competitors
            As the SUBSCRIBER
List some of the existing competitions and competitors
Choose the one you want to bet on (keep them in mind)
Bet on winner
Choose the good competition
Place a bet on one of the two competitors (best of luck!)
            As the MANAGER
Set results (place the one you bet on as the winner :] )
Check account of the user (should have won, obviously)
Check on the database, bets are set as WON.
Try to set the results again
        Let’s go a little further
            As the MANAGER
Create one extra team competitor
Create a new competition with the three competitors that you have created
            As the SUBSCRIBER
Place a bet on winner
Place a bet on podium (set the winner as the first for the podium
            As the MANAGER
Try to set the winner, you are not able
Set the podium (let you win on podium)
Check the user, should have got his money back because he won. Check in the database, the bets are set as WON.(check the comments on the tables to see what the integers mean)


