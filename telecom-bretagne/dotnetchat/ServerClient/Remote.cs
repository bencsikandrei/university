using System;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.Remoting.Messaging;
/// <summary>
/// 
/// </summary>
namespace RemotingServerClient
{
    public delegate void delegateUserInfo(string username);
    public delegate void delegateCommunication(Communication message);
    public delegate void delegateUpdateUserList(string userList);
    
    public enum MessageType
    {
        NEW_USER_SIGNED_IN,
        NORMAL_MESSAGE,
        USER_SIGNED_OUT
    }

    [Serializable()]
    public class Communication
    {
        private string username = "";
        private string message = "";
        private MessageType type = MessageType.NORMAL_MESSAGE;

        public Communication(string username, string message, MessageType type)
        {
            this.message = message;
            this.username = username;
            this.type = type;
        }

        public string UserName
        {
            get { return username; }
            set { username = value; }
        }

        public string Content
        {
            get { return message; }
            set { message = value; }
        }
        
        public MessageType Type
        {
            get { return type; }
            set { type = value; }
        }
    }
    /// <summary>
    /// Class that allows objects to be created on the client side, with the purpose of
    /// using the methods on the server
    /// </summary>
    public class ServerCommunicator : MarshalByRefObject
    {
        /* delegates for user activity */
        private static delegateUserInfo addUser;
        private static delegateUserInfo userLeft;
        /* delegate for the communication */
        private static delegateCommunication userSendsToServer;
        /* keep the users in a linkedList */
        private static LinkedList<UserHolder> userList = new LinkedList<UserHolder>();

        /* a queue is the best option for messenging */
        private static Queue queueUserToServerMessages = Queue.Synchronized(new Queue());

        public void loginUserAndAdd(string username, delegateCommunication servToClient)
        {
            userList.AddFirst(new UserHolder(username, servToClient));

            if (addUser != null)
                addUser(username);
        }

        public void logoutUserAndRemove(string username)
        {
            
            if (userLeft != null)
                userLeft(username);
        }
        /// <summary>
        /// The host should register a function pointer to which it wants a signal
        /// send when a User Registers
        /// </summary>
        public static delegateUserInfo NewUser
        {
            get { return addUser; }
            set { addUser = value; }
        }

        public static delegateUserInfo UserLeft
        {
            get { return userLeft; }
            set { userLeft = value; }
        }
        /// <summary>
        /// The host should register a function pointer to which it wants the CommsInfo object
        /// send when the client wants to communicate to the server
        /// </summary>
        public static delegateCommunication ClientToHost
        {
            get { return userSendsToServer; }
            set { userSendsToServer = value; }
        }

        // the static method that will be invoked by the server when it wants to send a message
        // to a specific user or all of them.
        public static void notifyUsersOfCommunication(string sender, string Message, MessageType type)
        {
            foreach (UserHolder user in userList)
            {
                if (user.UserName != sender && user.HostToUser != null)
                    user.HostToUser(new Communication(user.UserName, Message, type));
            }
        }
        

        // this instance method allows a client to send a message to the server
        public void sendMessage(Communication Message)
        {
            queueUserToServerMessages.Enqueue(Message);
        }

        public static Queue userToSeverQueue
        {
            get {
                return queueUserToServerMessages;
            }
        }
        /// <summary>
        /// Use a wrapper class for user attibutes and methods
        /// </summary>
        private class UserHolder
        {
            private string username = "";
            private delegateCommunication serverToUser = null;
            public UserHolder(string username, delegateCommunication serverToUser)
            {
                this.username = username;
                this.serverToUser = serverToUser;
            }
            public string UserName
            {
                get { return username; }
            }

            public delegateCommunication HostToUser
            {
                get { return serverToUser; }
            }
        }
    }
    
    /// <summary>
    /// The Sink will be used on the client side as a trigger for server actions
    /// </summary>
    public class CallbackSink : MarshalByRefObject
    {
        public event delegateCommunication fromServerToUser;

        public CallbackSink()
        { }

        [OneWay]
        public void HandleToClient(Communication info)
        {
            if (fromServerToUser != null)
                fromServerToUser(info);
        }
    }
}
