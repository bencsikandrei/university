namespace RemotingServerClient
{
    /* record for client */
    public class User
    { 
        private string userName;
        /* ctor */
        public User(string userName)
        {
            this.userName = userName;
        }
        /* simpel get */
        public string getUserName ()
        {
            return this.userName;
        }
    }
}
