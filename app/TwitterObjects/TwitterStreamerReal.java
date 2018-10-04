package TwitterObjects;

import akka.actor.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.nashorn.internal.ir.annotations.Ignore;
import play.libs.Json;
import twitter4j.*;
import twitter4j.Status;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * Twitter Streamer Real Implementation
 */
public class TwitterStreamerReal implements  ITwitterStreamer{

    private  ActorRef wsOutActor;
    TwitterStream twitterStream=null;
    Twitter twitter=null;
    List<String> listOfTweetKeywords=new ArrayList<String>() ;

    /**
     * Twitter Streamer constructor
     */
    public TwitterStreamerReal(){
        System.out.println("TwitterStreamerReal constructor");
    }

    /**
     * Actor cleanup and websocket close
     */
    public void cleanUp(){
        if(twitterStream!=null){
            twitterStream.cleanUp();
        }
    }

    /**
     * Setting websocket actor
     * @param wsOutActor websocket Actor
     */
    public void setWsOutActor(ActorRef wsOutActor){
        this.wsOutActor=wsOutActor;
    }



    /**
     * Change to stream array for new keyword
     * @param listOfTweetKeywords New list of keywords
     */
    public void addTweetKeyword(List<String> listOfTweetKeywords){
        this.listOfTweetKeywords=listOfTweetKeywords;
    }

    /**
     * Twitter streaming process start
     * @param self Actor that starts the process
     */
    public void twitterStreamStart(ActorRef self) {
        System.out.println("100% here");
        ConfigurationBuilder configBuilder= new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("0AZpxC7v99PWQ9Pyq4XOUtDUG")
                .setOAuthConsumerSecret("Xz4qRzpaIE0cElgpnkFtf9vR2O0tL7BtEqKWd18SYiYcPsHQkJ")
                .setOAuthAccessToken("58406433-T4TaAocRdqoWYB8fsZTLyKUp1pQ4MbevgkFyc73By")
                .setOAuthAccessTokenSecret("D7qz32jKTDFiOazLDjREOtHRWXxDmWl11qNYcOFv6cSAl");
        Configuration config=configBuilder.build();
        twitterStream= new TwitterStreamFactory(config).getInstance();
        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
            }
            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }
            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println(warning);
            }
            @Override
            public void onStatus(Status status) {
                String text=status.getText();
                String userName=status.getUser().getName();
                String screenName=status.getUser().getScreenName();
                ObjectNode twitterObject= Json.newObject();
                twitterObject.put("tweet",text);
                twitterObject.put("name",userName);
                twitterObject.put("screenName",screenName);
                wsOutActor.tell(twitterObject,self);
            }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("what is ths "+numberOfLimitedStatuses);
            }
        };

        twitterStream.addListener(listener);
        FilterQuery tweetFilterQuery = new FilterQuery();
        tweetFilterQuery.track(listOfTweetKeywords.toArray(new String[0]));
        twitterStream.filter(tweetFilterQuery);
    }

    /**
     * The User profile info are returned based on screen name
     * @param screenName  Screen name of the user
     * @return  User profile info in json format
     * @throws TwitterException
     */
    public CompletableFuture<ObjectNode[]> userTweets(String screenName) throws TwitterException {
        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("0AZpxC7v99PWQ9Pyq4XOUtDUG")
                .setOAuthConsumerSecret("Xz4qRzpaIE0cElgpnkFtf9vR2O0tL7BtEqKWd18SYiYcPsHQkJ")
                .setOAuthAccessToken("58406433-T4TaAocRdqoWYB8fsZTLyKUp1pQ4MbevgkFyc73By")
                .setOAuthAccessTokenSecret("D7qz32jKTDFiOazLDjREOtHRWXxDmWl11qNYcOFv6cSAl");
        Configuration config = configBuilder.build();
        twitter = new TwitterFactory(config).getInstance();
        String name;
        ObjectNode jsob[] = new ObjectNode[10];
        System.out.println("Have to wait");
        CompletableFuture<ObjectNode[]> obj= CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Completable future started to run");
                List<Status> statuses = twitter.getUserTimeline(screenName);
                int noOfTweetsToShow=(statuses.size() > 10 ? 10 : statuses.size());
                for (int i = 0; i < noOfTweetsToShow; i++) {
                    Status status = statuses.get(i);
                    jsob[i] = Json.newObject();
                    jsob[i].put("name", status.getUser().getName());
                    jsob[i].put("screenName", status.getUser().getScreenName());
                    jsob[i].put("location", status.getUser().getLocation());
                    jsob[i].put("friendsCount", status.getUser().getFriendsCount());
                    jsob[i].put("followersCount", status.getUser().getFollowersCount());
                    jsob[i].put("description", status.getUser().getDescription());
                    jsob[i].put("tweet", status.getText());
                }
                return jsob;
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        });
        System.out.println("Have to wait?");
        return  obj;
    }

}
