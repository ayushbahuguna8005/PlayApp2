package actors;

/**
 * Contains all the messages that the actor uses in the system.
 */
public class Messages {

    /**
     * Everytime a request for new tweet in handled my this method. An instantation of this class indicates,
     * the corresponding actor about  a request for new tweet
     */
    public static class RequestForNewTweets {
        public String newKeyWord;

        /**
         * Constructor with the new tweet keyword
         * @param newKeyWord  New tweet Keyword
         */
        public RequestForNewTweets(String newKeyWord) {
            this.newKeyWord = newKeyWord;
        }
    }

    /**
     * Websockets client use this message to register themselves with the parent actor of the system
     */
    public static class RegisterClients {
    }

    /**
     * Request for the User Profile page is handles through this message
     */
    public static class UserProfile{
        public String screenName;

        /**
         * Constructor for the UserProfile page
         * @param screenName User profile name
         */
        public UserProfile(String screenName){
            this.screenName = screenName;
        }
    }

}
