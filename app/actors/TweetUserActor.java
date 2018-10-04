package actors;
import TwitterObjects.ITwitterStreamer;
import akka.actor.AbstractActor;
import akka.actor.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Actor for handling all request related to the User profile information
 */
public class TweetUserActor  extends AbstractActor{

    ITwitterStreamer streamer;

    /**
     * The method that creates the actor
     * @param streamer The implementation of ItwitterStreamer that handles request for the twitter
     * @return Props
     */
    public static Props props( final ITwitterStreamer streamer) {
        return Props.create(TweetUserActor.class,streamer);
    }

    /**
     * Constructor method that takes a streamer object
     * @param streamer The implementation of ItwitterStreamer that handles request for the twitter
     */
    public TweetUserActor(ITwitterStreamer streamer)
    {
        System.out.println("Tweet User Actor - One that handles User Page is created");
        this.streamer=streamer;
    }

    /**
     * The message handler method for the User profile, the actor dies after handling the client request
     * @return Recieve information
     */
    @Override
    public Receive createReceive() {
        return  receiveBuilder().match(Messages.UserProfile.class, sc -> {
            sender().tell(this.streamer.userTweets(sc.screenName),self());
            getContext().stop(self());
        }).build();
    }
}
