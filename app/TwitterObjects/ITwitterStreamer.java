package TwitterObjects;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.node.ObjectNode;
import twitter4j.TwitterException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *  Inteferace Api for TwitterReal and Twitter Fake
 */
public interface ITwitterStreamer {
    /**
     * Actor cleanup and websocket close
     */
    void cleanUp();

    /**
     * Setting websocket actor
     * @param wsOutActor websocket Actor
     */
    void setWsOutActor(ActorRef wsOutActor);

    /**
     * Twitter streaming process start
     * @param self Actor that starts the process
     */
    void twitterStreamStart(ActorRef self);

    /**
     * Change to stream array for new keyword
     * @param listOfTweetKeywords New list of keywords
     */
    void addTweetKeyword(List<String> listOfTweetKeywords);

    /**
     * The User profile info are returned based on screen name
     * @param screenName  Screen name of the user
     * @return  User profile info in json format
     * @throws TwitterException
     */
    CompletableFuture<ObjectNode[]> userTweets(String screenName) throws TwitterException;
}
