using System;
using System.Windows;
using System.Windows.Controls;
using RemotingServerClient;
using System.Runtime.Remoting.Channels.Tcp;
using System.Runtime.Remoting.Channels;
using System.Diagnostics;
/// <summary>
/// The WPF GUI of te client
/// @author: Andrei-Florin BENCSIK
/// @date: 3.12.2016
/// 
/// The gui offers the possibility of loging in and out
/// The chat box is simple and intuitive
/// Controls are activated and deactivated as needed 
/// </summary>
namespace RemoteClientWithGUI
{

    public partial class MainWindow : Window
    {
        /* store the user name of this session */
        private string username = "";
        /* the TCP channel used */
        private TcpChannel channel;
        /* we have a server reference so we can remotely call methods on it */
        private ServerCommunicator serverReference = null;
        /* we keep a callback sink (classical thing to do) to make the server call client side methods */
        private CallbackSink messagesCallback = null;  
        
        public MainWindow()
        {
            InitializeComponent();
            /* make only the good controls available */
            initializeGUI();
        }
        /// <summary>
        /// The state in which the GUI should be when the app starts 
        /// </summary>
        private void initializeGUI()
        {
            this.textBoxUserName.IsEnabled = true;
            this.textBoxPortNumber.IsEnabled = true;
            this.buttonLogin.IsEnabled = true;
            this.textBoxSend.IsEnabled = false;
            this.buttonSend.IsEnabled = false;
            this.textBoxMessages.Text = "";
            this.textBoxUserList.Text = "";
        }
        /// <summary>
        /// The state in which the app should be when the app is in chat mode
        /// </summary>
        private void initializeForChat()
        {
            this.textBoxUserName.IsEnabled = false;
            this.textBoxPortNumber.IsEnabled = false;
            this.textBoxSend.IsEnabled = true;
            this.buttonSend.IsEnabled = true;
            // make sure we can't register again!
            buttonLogin.IsEnabled = false;
        }
        /// <summary>
        /// When the user clicks the login button we use the data entered by the user to try to login
        /// Checks are performed to assure that the user input is correct 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonLogin_Click(object sender, RoutedEventArgs e)
        {   
            /* get the user name */
            this.username = textBoxUserName.Text;
            /* if there is no input */
            if( this.username.Length == 0)
            {
                /* show an error dialog when the user does not enter the correct data */
                MessageBox.Show("Please enter a valid user name!", 
                    "Invalid user name", 
                    MessageBoxButton.OK, 
                    MessageBoxImage.Exclamation);
                /* focus the text box */
                this.textBoxUserName.Focus();
                return;
            }
            /* a default port number */
            int portNumber = 12346;
            /* check if the port is correct */
            if (textBoxPortNumber.Text.Length > 0)
            {
                try
                {
                    /* parse it to int */
                    portNumber = Int32.Parse(textBoxPortNumber.Text);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Please enter a valid port number!",
                    "Invalid port number",
                    MessageBoxButton.OK,
                    MessageBoxImage.Exclamation);
                    return;
                }
            }
            /* create a callback sink for the client */
            messagesCallback = new CallbackSink();
            /* we attach the delagate so that we can get messages from the server through it */
            messagesCallback.fromServerToUser += new delegateCommunication(delegateFromServerToUser);
            try
            {
                /* because of the delegate we need a channel from the server to the client */
                channel = new TcpChannel(portNumber);
                /* register it */
                ChannelServices.RegisterChannel(channel, false);
                /* create the reference */
                serverReference = (ServerCommunicator)Activator.GetObject(typeof(ServerCommunicator), 
                    "tcp://localhost:12345/ServerAccess");
            }
            catch (Exception exc)
            {
                Debug.WriteLine(exc);
            }
            /* now we can use the reference to login */
            serverReference.loginUserAndAdd(this.textBoxUserName.Text, new delegateCommunication(messagesCallback.HandleToClient));
            /* prepare de GUI */
            initializeForChat();
        }
        /// <summary>
        /// When the user clicks the logout button we use the reference to logout of the server 
        /// We also remove the event cause we no longer need it
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonLogout_Click(object sender, RoutedEventArgs e)
        {
            /* logout */
            serverReference.logoutUserAndRemove(this.username);
            /* clean the garbage */
            messagesCallback.fromServerToUser -= new delegateCommunication(delegateFromServerToUser);
            /* reset the GUI for another potential login */
            initializeGUI();
        }
        /// <summary>
        /// When the user clicks the send button we can trigger an event and store the message on the server
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonSend_Click(object sender, RoutedEventArgs e)
        {
            /* call the reference method to store the message somewhere on the server */
            serverReference.sendMessage(new Communication(this.username, 
                this.textBoxSend.Text, 
                MessageType.NORMAL_MESSAGE));
            /* update the GUI */
            this.textBoxMessages.Text = "me: " + this.textBoxSend.Text + Environment.NewLine + this.textBoxMessages.Text;
            /* clear the box after sending */
            this.textBoxSend.Text = "";
        }
        /// <summary>
        /// This is the method that will be called when a new communication arrives, as declared in the constructor
        /// </summary>
        /// <param name="message"></param>
        void delegateFromServerToUser(Communication message)
        {
            /* log the event */
            Debug.WriteLine("New communication : " + message.Type);
            /* we need to use the dispatcer to run it on the main thread, cause we are doing GUI updates */
            Dispatcher.Invoke(
                () =>
                {
                    /* multiple types of messages make it so that we need to have diferent behavior */
                    if (message.Type == MessageType.NEW_USER_SIGNED_IN)
                    {
                        /* update user list */
                        this.textBoxUserList.Text = "";
                        string[] userList = message.Content.Split(';');
                        foreach (string s in userList)
                        {
                            this.textBoxUserList.Text = s + Environment.NewLine + this.textBoxUserList.Text;
                        }
                    }
                    else
                    {
                        /* we received a chat message */
                        this.textBoxMessages.Text = message.UserName + " : " + message.Content + Environment.NewLine + this.textBoxMessages.Text;
                    }
                }
                );

        }
        /// <summary>
        /// Improve UX by using enter as a send key
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void textBoxSend_EnterPressed(object sender, System.Windows.Input.KeyEventArgs e)
        {
            /* simulate a key press whenever we press enter whilst in the send text box */
            if(e.Key == System.Windows.Input.Key.Enter)
            {
                this.buttonSend.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
            }
        }
    }
}
