package actors;

import TwitterObjects.ITwitterStreamer;
import akka.actor.AbstractActor;
import akka.actor.*;
import java.util.ArrayList;
import java.util.List;


/**
 *This class includes implementation for AbstractActor abstract class from Akka package.
 *It also includes necessary methods and overrides it.
 */
public class TweetActorVersion2 extends AbstractActor {
    List<String> listOfTweetKeywords=new ArrayList<String>() ;
    private  ActorRef wsOutActor;

    /*@Inject*/
    ITwitterStreamer streamer;

    /**
     * @param wsout ActorRef
     * @param streamer TwitterStreamer
     * @return Prop of this actor
     */
    public static Props props(final ActorRef wsout, final ITwitterStreamer streamer) {
        return Props.create(TweetActorVersion2.class, wsout,streamer);
    }

    /**
     * This method establishes a connection with WebSocket actor and registers itself with the user parent actor.
     * @param wsOutActor WebSocket actor
     * @param streamer TwitterStreamer
     */
    public TweetActorVersion2( ActorRef wsOutActor,ITwitterStreamer streamer) {

        this.wsOutActor=wsOutActor;
        this.streamer=streamer;
        this.streamer.setWsOutActor(wsOutActor);
        context().actorSelection("/user/userParentActor/").tell(new Messages.RegisterClients(),self());
    }

    /**
     * Runs when the actor starts
     */
    @Override
    public void preStart() {
        System.out.println("TweetActor actor started");
    }

    /**
     * Runs when the actor stops
     */
    @Override
    public void postStop() {
        System.out.println("TweetActor actor stopped");
        if(streamer!=null){
            streamer.cleanUp();
        }
    }


    /**
     * The message handler of tweets  and cleanup process
     * @return Recieve event of the message
     */

    @Override
    public Receive createReceive() {
        return  receiveBuilder()
                .match(Messages.RequestForNewTweets.class,msg->{
                    System.out.println(msg.newKeyWord);
                    listOfTweetKeywords.add(msg.newKeyWord);
                    if(streamer!=null){
                        streamer.cleanUp();
                    }
                    streamer.addTweetKeyword(listOfTweetKeywords);
                    streamer.twitterStreamStart(self());
                }).build();
    }
}
