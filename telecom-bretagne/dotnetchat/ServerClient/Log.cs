using System;

namespace RemotingServerClient
{
    public class Log
    {
        public static void debug(string message)
        {
            Console.WriteLine("[DEBUG]" + message);
        }

        public static void error(string message)
        {
            Console.WriteLine("[ERROR]" + message);
        }
    }
}
