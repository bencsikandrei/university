using System;
using System.Collections.Generic;

namespace RemotingServerClient
{
    public class UserLab
    {
        private static LinkedList<User> sClientList = new LinkedList<User>();
        private static UserLab sClientLab = null;

        private UserLab()
        {
        }

        public static UserLab getInstance()
        {
            if (sClientLab == null)
            {
                sClientLab = new UserLab();
            }
            return sClientLab;
        }

        public User getUser(string username)
        {
            foreach (User u in sClientList)
            {
                if (u.getUserName().Equals(username))
                {
                    return u;
                }
            }
            return null;
        }

        public ICollection<User> getUserList()
        {
            return sClientList;
        }

        public bool addUser(string username)
        {
            /* create username */
            User toAdd = new User(username);
            /* add it to the list */
            sClientList.AddFirst(toAdd);
            return true;
        }

        public void removeUser(string username)
        {
            User toDelete = this.getUser(username);
            /* remove it */
            sClientList.Remove(toDelete);

        }
    }
}
