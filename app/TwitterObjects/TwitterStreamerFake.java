package TwitterObjects;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;



/**
 * Twitter Streamer Fake Implementation
 */
public class TwitterStreamerFake implements  ITwitterStreamer {

    /**
     * Twitter Streamer Fake constructor
     */
    public TwitterStreamerFake(){
        System.out.println("TwitterStreamerFake constructor is called");
    }
    private  ActorRef wsOutActor;
    List<String> listOfTweetKeywords=new ArrayList<String>() ;

    /**
     * Actor cleanup and websocket close
     */
    public void cleanUp(){
        System.out.println("Fake cleanup");
    }
    /**
     * Setting websocket actor
     * @param wsOutActor websocket Actor
     */
    public void setWsOutActor(ActorRef wsOutActor){
        this.wsOutActor=wsOutActor;
    }

    /**
     * Twitter streaming process start
     * @param self Actor that starts the process
     */
    public void twitterStreamStart(ActorRef self){
        System.out.println("TwitterStreamerFake, got here");
        ObjectNode twitterObject= Json.newObject();
        twitterObject.put("tweet","SOEN6441");
        twitterObject.put("name","ASDSW");
        twitterObject.put("screenName","dividedbyzero360");
        wsOutActor.tell(twitterObject,self);
    }

    /**
     * Change to stream array for new keyword
     * @param listOfTweetKeywords New list of keywords
     */
    public void addTweetKeyword(List<String> listOfTweetKeywords){
        System.out.println("Do nothing");
    }

    /**
     * The User profile info are returned based on screen name
     * @param screenName  Screen name of the user
     * @return  User profile info in json format
     * @throws TwitterException
     */
    public CompletableFuture<ObjectNode[]> userTweets(String screenName) throws TwitterException{
        ObjectNode jsob[] = new ObjectNode[10];
        for(int i=0; i<10;i++)
        {
            jsob[i] = Json.newObject();
            jsob[i].put("name", "John Doe");
            jsob[i].put("screenName", "John Doe ScreenName");
            jsob[i].put("location", "John Doe Location");
            jsob[i].put("friendsCount", "John Doe Friend's Count");
            jsob[i].put("followersCount", "John Doe Followers Count");
            jsob[i].put("description", "John Doe Description");
            jsob[i].put("tweet", "John Doe Tweet");
        }
        return  CompletableFuture.completedFuture(jsob);
    }
}
