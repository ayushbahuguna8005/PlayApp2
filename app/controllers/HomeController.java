package controllers;

import TwitterObjects.ITwitterStreamer;
import actors.Messages;
import actors.UserParentActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.F;
import play.libs.streams.ActorFlow;
import play.mvc.*;
import views.html.displayTweets;
import views.html.index;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import static akka.pattern.PatternsCS.ask;

import actors.*;
/**
 * HomeController handels all request from the browser and is the only controller to be used
 * */
public class HomeController extends Controller {

    ActorSystem system;
    Materializer materializer;
    ActorRef userParentActor;
    ITwitterStreamer streamer;
    ActorRef userProfileActor;

    /**
     * HomeController constructor, called once when the server starts
     *@param system This is the Actor System that is used the create all the actors in the system (Play injected)
     *@param   materializer   Use to allocate threads/resources for creating of the actors in the system (Play injected)
     *@param  streamer     An implementation of ITwitterStream that handles the requests to the Twitter API
     * */
    @Inject
    public HomeController(ActorSystem system, Materializer materializer, ITwitterStreamer streamer){
        this.system = system;
        this.materializer = materializer;
        userParentActor=system.actorOf(Props.create(UserParentActor.class,system,materializer),"userParentActor");
        this.streamer=streamer;
    }

    /**
     *   The home screen, the user first lands on this page for url localhost:9000
     *   @return index.scala.html that contains the search box for searching keywords
     * */
    public Result index(){
        return ok(index.render(request()));
    }

    /**
     *  This controller method handles new request for new tweet keywords
     *  @param keyword The keyword to be searched
     *  @return Http 200 message on successfully receiving the message
     * */
    public Result tellChildAboutnewQuery(String keyword){
        userParentActor.tell(new Messages.RequestForNewTweets(keyword),ActorRef.noSender());
        return ok();
    }

    /**
     * This controller method handles websocket connection from the client
     * @return Either a successfully web socket connection or a forbidden result
     * */
    public WebSocket ws() {
        return  WebSocket.Json.acceptOrResult(requestHeader->{
            if(sameOriginCheck(requestHeader))
            {
                F.Either<Result, Flow<JsonNode, JsonNode, ?>> either= F.Either.Right(ActorFlow.actorRef(webSocketActor->TweetActorVersion2.props(webSocketActor,streamer),system,materializer)) ;
                return CompletableFuture.completedFuture(either);
            }else{
                return  forbiddenResult();
            }
        });
    }

    /**
     * Checks whether the request to the Websocket in from the same domain
     *@return rh The request header of the request that aksed for the web socket client
     * @return true if the request is from same domain else false
     * */
    public boolean sameOriginCheck(Http.RequestHeader rh) {
        final Optional<String> origin = rh.header("Origin");

        if (! origin.isPresent()) {
            System.out.println("originCheck: rejecting request because no Origin header found");
            return false;
        } else if (originMatches(origin.get())) {
            System.out.println("originCheck: originValue = " + origin);
            return true;
        } else {
            System.out.println("originCheck: rejecting request because Origin header value " + origin + " is not in the same origin");
            return false;
        }
    }

    /**
     *The method is checks if the string formatted url matches a particular url format
     * @param origin The url address of the request
     * @return True if it matches, false otherwise
     * */
    public boolean originMatches(String origin) {
        return origin.contains("localhost:9000") || origin.contains("localhost:19001");
    }

    /**
     * This method is used to notifiy the user they don't have access to the websocket client
     * @return  Future ForbiddenResult
     * */
    public CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> forbiddenResult() {
        final Result forbidden = Results.forbidden("forbidden");
        final F.Either<Result, Flow<JsonNode, JsonNode, ?>> left = F.Either.Left(forbidden);
        return CompletableFuture.completedFuture(left);
    }

    /**
     * This controller method takes request for displaying Tweeter user page
     * @param  screenName The screen name of the user whose profile we want to display .
     * @return  Http Ok with the page of the user.
     * */
    public Result displayTweets(String screenName){

        return ok(displayTweets.render(screenName));
    }


    /**
     * This method is called from displayTweets javascript file for getting the information from the twitter api
     * @param screenName  The screen name of the user whose profile we want to display .
     * @return   Future JSON result containing the user profile info
     * @throws Exception  Wait for too long exception
     */
    public CompletionStage<Result> getUserTweets(String screenName) throws Exception{
        final Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
        ObjectMapper mapper = new ObjectMapper();
        userProfileActor=system.actorOf(TweetUserActor.props(streamer),"userProfileActor");
        CompletionStage<Object> res1=
                ask(userProfileActor, new Messages.UserProfile(screenName), timeout);
        CompletionStage<Result> finalResult=null;
        long startTime=System.currentTimeMillis();
        CompletableFuture<ObjectNode[]> obj=  (CompletableFuture<ObjectNode[]>) res1.toCompletableFuture().get();
        long endTime=System.currentTimeMillis();
        System.out.println("Total time taken "+(endTime-startTime ));
        finalResult =obj.thenApply(x-> Results.ok(mapper.convertValue(x, JsonNode.class)));
        return finalResult;
    }

}
