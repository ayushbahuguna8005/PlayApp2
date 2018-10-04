package actors;

/*import akka.actor.*;
import akka.stream.Materializer;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import scala.concurrent.duration.Duration;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;*/
import akka.actor.*;
import akka.stream.Materializer;
import akka.util.Timeout;
import play.libs.Json;
import scala.concurrent.duration.Duration;
import twitter4j.*;
import twitter4j.Status;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The main actor of the system that handles all browser
 */
public class UserParentActor extends AbstractActor {

    private final Materializer materializer;
    private final ActorSystem system;
    private final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
    ArrayList<ActorRef> tweetActors =new ArrayList<>();
    Twitter twitter;


    /**
     * This is the constructor for this class and intializes the value of ActorSystem and Materializer
     * @param system ActorSystem
     * @param materializer Materializer
     */
    public UserParentActor(ActorSystem system, Materializer materializer) {
        this.materializer = materializer;
        this.system = system;
    }

    /**
     * This is an extended method from class AbstractActor
     * overridden by custom string which indicate initialization of UserParent actor.
     */
    @Override
    public void preStart() {
        System.out.println("UserParent actor started  "+self().toString());
    }

    /**
     * This is an extended method from class AbstractActor
     * overridden by custom string which indicate TweetActor stopped.
     */
    @Override
    public void postStop() {
        System.out.println("UserParentActor actor stopped  "+self().toString());
    }


    /**
     * An actor has to define its initial receive behavior by implementing the createReceive method
     * @return Recieve information
     **/
    @Override
    public Receive createReceive() {
        return  receiveBuilder().match(Messages.RegisterClients.class,msg->{
            tweetActors.add(sender());
            System.out.println(tweetActors.size());
        }).match(Messages.RequestForNewTweets.class,msg-> {
            tweetActors.forEach(tweetTrialActor -> tweetTrialActor.tell(msg,self()));
        }).build();

    }


}


