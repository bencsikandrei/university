
                          BETTINGSOFT GR4 AB2
  
  Le groupe G4AB2, dont les membres sont: Andrei-Florin BENCSIK, Ahmed SAMI
  MOHAMED, a choisi la classe Subscriber (et SubscriberManager - le DAO)
  pour l'evaluation.
  
  Le travail de notre groupe n'arretait pas ici. Nous avons travaille sur:
  1) toute fonction dans BettingSystem.java dont le but est de gerer les
  joueurs(i.e. subscribe, unsubscribe, getByUsername, credit, debit, etc)
  2) toute fonction liee a l'interface (CLI)
  3) fonction d'utilite dans Utils.java (randomString)
  4) toute fonction liee a la base de donnees (tout DAO: RootManager, 
  SubscriberManager, CompetitorManager, CompetitionManager, ParticipantManager
  BetsManager)
  5) les exceptions pour le Subscriber
  6) les tests pour le Subscriber (SubscriberTestSuite.java, TestSubscriberPersistanceAuthenticationFail.java, TestSubscriberPersistanceDelete.java, TestSubscriberPersistanceRetrieve.java, TestSubscriberTwoUsersWithDifferentUserNames.java,TestSubscriberTwoUsersWithSameName.java
  
  
  !NOTE! Pour commencer rapidement voir la partie Installation & Requirements  
  Aussi il y a la version en anglais pour plus de details.
    
  Introduction
  -------------

  L'application BETTINGSOFT est faite par le groupe de developpement G4AB2
  Elle a pour but la possibilite de parier sur des competitions sportives.
  Pour aboutir a ca, nous avons divise le developement avec l'aide des outils
  comme: le diagramme UML, diagramme de sequence, cahier des charges, etc.
  

  La derniere version
  ------------------

  La derniere version contient une interface en ligne de commande (CLI)
  Elle respecte toute demande VIT et IMP du cahier des charges.
  

  Documentation
  -------------

  Le code contient des commentaires pertinentes et des 
  explications pour les parties un peu plus compliques. Cette documentation
  rigureuse permet le client de continuer a develope l'application, sans 
  avoir neccesairement besoin d'aide. 
  

  Installation & Requirements
  ---------------------------

  Pour avoir acces a toute fonctionnalite de l'application il vous faut:
  *  une base de donnees PostgreSQL, dont le nom est BETTING, et le proprietaire
    (owner) est: manager (mot de passe: manager)
    Configuration:
            username    "manager";
	        password    "manager";
	        address     "localhost";
	        port        "54321";
	        dbName      "BETTING";
  *  JDK 1.7 (or up)
  *  un terminal pour rouler (EX: java -jar bettingSoft.jar)
  
  Pour lancer: 
        Lancez d'abord deux terminaux.
        Naviguez dans l'endroit ou vous avez telecharge l'application
        Trouvez les deux fichiers .jar
        Lancez d'abord le manager: java -jar bettingSoft.jar
        (maintenant dans l'autre termina)
        Lancez le client: java -jar clientBettingSoft.jar
        Authentifiez vous pour les deux.
  !NOTE! Si c'est la premiere fois que vous lancez l'application vous allez
  voir le message qui vous donne le mot de passe initial ("1234")
  Pour plus d'info et pour des exemples d'utilisation voir le README en
  version anglaise.       
              
              
  Licensing
  ---------
  
  S'il vous plait referer au cahier de charges.
  

  Contacts
  --------

     ahmed.samimohamed@telecom-bretagne.eu
     af.bencsik@telecom-bretagne.eu (andrei.bencsik@gmail.com)

