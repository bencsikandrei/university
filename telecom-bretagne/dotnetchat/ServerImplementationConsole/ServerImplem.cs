using System;
using RemotingServerClient;
using System.Threading;
using System.Runtime.Remoting.Channels;
using System.Collections;
using System.Runtime.Remoting.Channels.Tcp;
using System.Runtime.Remoting;
/// <summary>
/// The actual impleentation of the server
/// @author: Andrei-Florin BENCSIK
/// @date: 3.12.2016
/// 
/// Has all the methods needed for a full communication 
/// </summary>
namespace ServerImplementationConsole
{
    class ServerImplem
    {
        public ServerImplem()
        {
            /* register the channel and make all preparations */
            register();
            /* prepare the delegates */
            ServerCommunicator.NewUser = new delegateUserInfo(loginAndAdd);
            ServerCommunicator.UserLeft = new delegateUserInfo(logoutAndRemove);
            ServerCommunicator.ClientToHost = new delegateCommunication(userToHost);
            /* create a new thread to run the checks on the message queue, in order not to block the console */
            Thread thrd = new Thread(new ThreadStart(checkMessageQueue));
            /* fire the thread */
            thrd.Start();
            /* log some info */
            Log.debug("Server started !");
            /* keep the console alive */
            Console.ReadLine();
        }
        /// <summary>
        /// When user logs in this method is used as the delegate 
        /// cf. constructor 
        /// </summary>
        private void loginAndAdd(string username)
        {
            /* use the lab to get add the user */
            UserLab.getInstance().addUser(username);
            /* create a message for the user list */
            string currentUserList = "";
            /* create the user list to be sent */
            foreach (User u in UserLab.getInstance().getUserList())
            {
                Log.debug("User online:" + u.getUserName());
                currentUserList += (u.getUserName() + ";");
            }
            /* create a communication using the user list type */
            Communication userListUpdate = new Communication("server", 
                currentUserList, 
                MessageType.NEW_USER_SIGNED_IN);
            /* post a new communication */
            ServerCommunicator.userToSeverQueue.Enqueue(userListUpdate);
        }
        /// <summary>
        /// This method is called when the user logs out
        /// cf. constructor
        /// </summary>
        /// <param name="username"></param>
        private void logoutAndRemove(string username)
        {
            /* use lab to remove */
            UserLab.getInstance().removeUser(username);
            /* create the current user list */
            string currentUserList = "";
            /* the user list */
            foreach (User u in UserLab.getInstance().getUserList())
            {
                Log.debug("User online:" + u.getUserName());
                currentUserList += (u.getUserName() + ";");
            }
            /* create a communication using the user list type */
            Communication userListUpdate = new Communication("server", 
                currentUserList, 
                MessageType.NEW_USER_SIGNED_IN);
            /* post a change in the user list */
            ServerCommunicator.userToSeverQueue.Enqueue(userListUpdate);
        }
        /// <summary>
        /// This method is what the second thread executes
        /// 
        /// </summary>
        private void checkMessageQueue()
        {
            Communication message;
            for (;;)
            {
                Thread.Sleep(70);   // allow rest of the system to continue whilst waiting...
                if (ServerCommunicator.userToSeverQueue.Count > 0)
                {
                    /* take the message out of the queue */
                    message = (Communication) ServerCommunicator.userToSeverQueue.Dequeue();
                    /* log the communication */
                    Log.debug("One communication is pending ..");
                    Log.debug("Type: " + message.Type);
                    /* use the delegate to send the message */
                    userToHost(message);
                }
            }

        }
        /// <summary>
        /// This method will call the notification for the users
        /// </summary>
        /// <param name="Info"></param>
        private void userToHost(Communication Info)
        {
            /* log the communication */
            Log.debug("From " + Info.UserName + " : " + Info.Content + Environment.NewLine);
            /* send the message to all concerned users */
            ServerCommunicator.notifyUsersOfCommunication(Info.UserName, 
                Info.Content, 
                Info.Type);
        }
        /// <summary>
        /// 
        /// </summary>
        private void register()
        {
            /* we need a formatter for the delegates to work */
            BinaryServerFormatterSinkProvider serverFormatter = new BinaryServerFormatterSinkProvider();
            /* security level */
            serverFormatter.TypeFilterLevel = System.Runtime.Serialization.Formatters.TypeFilterLevel.Full;
            /* set the properties */
            Hashtable properties = new Hashtable();
            properties["name"] = "ServerTCPChannel";
            properties["port"] = 12345;
            /* create the channel */
            TcpChannel channel = new TcpChannel(properties, null, serverFormatter);
            /* register */
            ChannelServices.RegisterChannel(channel, false);
            /* the well known service */
            RemotingConfiguration.RegisterWellKnownServiceType(typeof(ServerCommunicator), 
                "ServerAccess", 
                WellKnownObjectMode.Singleton); /* we need a singleton for all the clients to work */
        }

    }
}
