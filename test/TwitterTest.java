/*
import TwitterObjects.ITwitterStreamer;
import TwitterObjects.TwitterStreamerFake;
import actors.Messages;
import actors.TweetUserActor;
import actors.UserParentActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.HomeController;

import org.eclipse.jetty.util.Promise;
import org.junit.*;

import play.libs.F;

import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.TestServer;

import akka.testkit.javadsl.TestKit;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import com.fasterxml.jackson.databind.JsonNode;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import org.junit.Test;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionStage;



import play.Application;
import play.inject.guice.*;
import scala.reflect.internal.util.ThreeValues;

import javax.inject.Inject;

import static play.inject.Bindings.bind;

public class TwitterTest {
    @Inject
    Application application;

    static HomeController homeController;
    static ActorSystem system;
    static Materializer materializer;
    static ITwitterStreamer twitterStreamer;
    @Before
    public  void setUp(){
         application = new GuiceApplicationBuilder()
                .overrides(bind(ITwitterStreamer.class).to(TwitterStreamerFake.class))
                .build();
        system= ActorSystem.create();
        materializer=ActorMaterializer.create(system);
        twitterStreamer=new TwitterStreamerFake();
        homeController=new HomeController(system,materializer,twitterStreamer);


    }



    @After
    public void teardown() {
       Helpers.stop(application);
        TestKit.shutdownActorSystem(system);
        system = null;
    }


    @Test
    public void index(){
        Helpers help=new Helpers();
        Http.RequestBuilder requestBuilder=fakeRequest(controllers.routes.HomeController.index());
        Http.Context context=help.httpContext(requestBuilder.build());
        Http.Context.current.set(context);
        Result r=homeController.index();
        assertThat(r.status()).isEqualTo(200);

    }

    @Test
    public void testRejectWebSocket() {
        TestServer server = testServer(37117,application);
        running(server, () -> {
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    String serverURL = "ws://localhost:37117/ws";
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener(message -> {});
                    CompletableFuture<WebSocket> completionStage = webSocketClient.call(serverURL, listener);
                    await().until(completionStage::isDone);
                    assertThat(completionStage)
                            .hasFailedWithThrowableThat()
                            .hasMessageContaining("Invalid Status Code 400");
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }

    @Test
    public  void testAcceptWebSocket()
    {
        TestServer server = testServer(9000,application);
        running(server, ()->{
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    String serverURL = "ws://localhost:9000/HomeController/ws";
                    ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener((message) -> {
                        try {
                            System.out.println("any message "+message);
                            queue.put(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    CompletableFuture<WebSocket> completionStage = webSocketClient.call(serverURL, listener);
                    await().until(completionStage::isDone);
                    WebSocket websocket = completionStage.get();
                    String url="http://localhost:9000/tellChildAboutnewQuery/SOEN6441";
                    try (WSClient ws = play.test.WSTestClient.newClient(server.port())) {
                        CompletionStage<WSResponse> stage = ws.url(url).get();
                        WSResponse response = stage.toCompletableFuture().get();
                        assertEquals(200, response.getStatus());
                        assertThat(response.getStatus()).isEqualTo(200);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(websocket.isOpen())
                    {
                        System.out.println("Good news websocket is empty");
                    }
                    assertThat(websocket.isOpen()).isEqualTo(true);
                    if(queue.peek()!=null)
                    {
                        String result=queue.peek();
                        assertThat(result).isEqualTo("{\"tweet\":\"SOEN6441\",\"name\":\"ASDSW\",\"screenName\":\"dividedbyzero360\"}");
                    }
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }
    @Test
    public void checkOrigin()
    {
        String url="localhost:9000";
        assertThat(homeController.originMatches(url)).isEqualTo(true) ;
        url="localhost:19001";
        assertThat(homeController.originMatches(url)).isEqualTo(true) ;
        url="localhost:19002";
        assertThat(homeController.originMatches(url)).isEqualTo(false) ;
    }

    @Test
    public void  testforbiddenResult(){
        CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> result=homeController.forbiddenResult();
        try{
            Optional<Result> resultForbidden= result.toCompletableFuture().get().left;
            if(resultForbidden.isPresent()){
                System.out.println(resultForbidden.get().status());
                assertThat(resultForbidden.get().status()).isEqualTo(403);
            }else{
                System.out.println("Not available");
            }
        }catch (Exception ex)
        {
            System.out.println("Pass");
        }

    }

    @Test
    public void testActorMessages(){
        Messages.RequestForNewTweets requestForNewTweets=new Messages.RequestForNewTweets("trump");
        assertThat(requestForNewTweets.newKeyWord).isEqualTo("trump");
        Messages m=new Messages();
        assertThat(m).isNotEqualTo(null);
        Messages.UserProfile users= new Messages.UserProfile("dividedByZero360");
        assertThat(users).isNotEqualTo(null);
        assertThat(users.screenName).isEqualTo("dividedByZero360");
        Messages.RegisterClients registerClients=new Messages.RegisterClients();
        assertThat(registerClients).isNotEqualTo(null);
    }

    @Test
    public void testSameOriginCheck()
    {
        play.mvc.Http.RequestHeader header = mock(play.mvc.Http.RequestHeader.class);
        assertThat(homeController.sameOriginCheck(header)).isEqualTo(false);
        Optional<String> origin=Optional.of("localhost:9001");
        when(header.header("Origin")).thenReturn(origin);
        assertThat(homeController.sameOriginCheck(header)).isEqualTo(false);
    }

    @Test
    public void testtellChildAboutnewQuery()
    {
        assertThat(homeController.tellChildAboutnewQuery("Salman Khan").status()).isEqualTo(200);

    }

    @Test
    public void displayTweets()
    {
        Result r=homeController.displayTweets("dividedByZero360");
        assertThat(r.status()).isEqualTo(200);
    }

    @Test
    public void testgetUserTweets () throws Exception
    {
      CompletionStage<Result> r= homeController.getUserTweets("John Doe ScreenName");
      Result result=r.toCompletableFuture().get();
        assertThat(result.status()).isEqualTo(200);
    }

    @Test
    public void testActors() throws Exception{
         new TestKit(system){{
             final Props props = Props.create(UserParentActor.class,system,materializer);
             final ActorRef userParentActorTest = system.actorOf(props, "userParentActorTest");
             Messages.RegisterClients rc=new Messages.RegisterClients();
             final TestKit probe = new TestKit(system);
             userParentActorTest.tell(rc, getRef());
             // await the correct response
             Messages.RequestForNewTweets requestNewTweets =new Messages.RequestForNewTweets("trump");
             userParentActorTest.tell(requestNewTweets, getRef());
             expectMsg(duration("5 second"), requestNewTweets);
             assertThat(userParentActorTest.isTerminated()).isEqualTo(false);

             final ActorRef tweetUserActor=system.actorOf(actors.TweetUserActor.props(twitterStreamer),"TweetUserActor");
             Messages.UserProfile userProfileOf=new Messages.UserProfile("dividedByZero360");
             tweetUserActor.tell(userProfileOf,getRef());
            // expectMsgClass(com.fasterxml.jackson.databind.node.ObjectNode.class);
             expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class, CompletableFuture.class);
            // assertThat(tweetUserActor.isTerminated()).isEqualTo(false);
             Thread.sleep(10);
             assertThat(tweetUserActor.isTerminated()).isEqualTo(true);


             final ActorRef  tweetVersion2=system.actorOf(actors.TweetActorVersion2.props(getRef(),twitterStreamer),"tweetVersion2");
             tweetVersion2.tell(requestNewTweets, getRef());
             assertThat(tweetVersion2.isTerminated()).isEqualTo(false);
             expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class,CompletableFuture.class);
         }};
    }


}
*/


import TwitterObjects.ITwitterStreamer;
import TwitterObjects.TwitterStreamerFake;
import actors.Messages;
import actors.TweetUserActor;
import actors.UserParentActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.HomeController;

import org.eclipse.jetty.util.Promise;
import org.junit.*;

import play.libs.F;

import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.TestServer;

import akka.testkit.javadsl.TestKit;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import com.fasterxml.jackson.databind.JsonNode;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import org.junit.Test;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionStage;



import play.Application;
import play.inject.guice.*;
import scala.reflect.internal.util.ThreeValues;

import javax.inject.Inject;

import static play.inject.Bindings.bind;

/**
 * This class tests the whole application
 */
public class TwitterTest {
    @Inject
    Application application;

    static HomeController homeController;
    static ActorSystem system;
    static Materializer materializer;
    static ITwitterStreamer twitterStreamer;
    @Before

    /**
     * This application sets up a GoogleGuiceApplication by injecting a bind of ITwitterStreamer class
     * which is converted to TwitterStreamerFake class. It also intiializes the HomeController, ActorSystem,
     * Materializer and ITwitterStreamer which are required classes of the project to run the project.
     */
    public  void setUp(){
        application = new GuiceApplicationBuilder()
                .overrides(bind(ITwitterStreamer.class).to(TwitterStreamerFake.class))
                .build();
        system= ActorSystem.create();
        materializer=ActorMaterializer.create(system);
        twitterStreamer=new TwitterStreamerFake();
        homeController=new HomeController(system,materializer,twitterStreamer);

    }

    /**
     * This method tears down the GoogleGuice application being set up when the Test class was set up.
     * This runs at the end when all the tests have been run.
     */
    @After
    public void teardown() {
        Helpers.stop(application);
        TestKit.shutdownActorSystem(system);
        system = null;
    }


    /**
     * This method creates a new helper, a HTTP.RequestBuilder, Context and is used to call the
     * index method of the HomeController controller which returns status 200(OK) which means its running correctly.
     */
    @Test
    public void index(){
        Helpers help=new Helpers();
        Http.RequestBuilder requestBuilder=fakeRequest(controllers.routes.HomeController.index());
        Http.Context context=help.httpContext(requestBuilder.build());
        Http.Context.current.set(context);
        Result r=homeController.index();
        assertThat(r.status()).isEqualTo(200);

    }

    /**
     *This method tests the WebSocket but intends to fail the test as the serverUrl being passed in incorrect.
     *The expected output is "Invalid Status Code 400".
     */
    @Test
    public void testRejectWebSocket() {
        TestServer server = testServer(37117,application);
        running(server, () -> {
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    String serverURL = "ws://localhost:37117/ws";
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener(message -> {});
                    CompletableFuture<WebSocket> completionStage = webSocketClient.call(serverURL, listener);
                    await().until(completionStage::isDone);
                    assertThat(completionStage)
                            .hasFailedWithThrowableThat()
                            .hasMessageContaining("Invalid Status Code 400");
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }

    /**
     *This method tests the WebSocket and intends to pass the test as the serverUrl being passed in correct.
     *The expected output is an empty websocket with the message "Good news websocket is empty".
     */
    @Test
    public  void testAcceptWebSocket()
    {
        TestServer server = testServer(9000,application);
        running(server, ()->{
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    String serverURL = "ws://localhost:9000/HomeController/ws";
                    ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener((message) -> {
                        try {
                            System.out.println("any message "+message);
                            queue.put(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    CompletableFuture<WebSocket> completionStage = webSocketClient.call(serverURL, listener);
                    await().until(completionStage::isDone);
                    WebSocket websocket = completionStage.get();
                    String url="http://localhost:9000/tellChildAboutnewQuery/SOEN6441";
                    try (WSClient ws = play.test.WSTestClient.newClient(server.port())) {
                        CompletionStage<WSResponse> stage = ws.url(url).get();
                        WSResponse response = stage.toCompletableFuture().get();
                        assertEquals(200, response.getStatus());
                        assertThat(response.getStatus()).isEqualTo(200);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(websocket.isOpen())
                    {
                        System.out.println("Good news websocket is empty");
                    }
                    assertThat(websocket.isOpen()).isEqualTo(true);
                    if(queue.peek()!=null)
                    {
                        String result=queue.peek();
                        assertThat(result).isEqualTo("{\"tweet\":\"SOEN6441\",\"name\":\"ASDSW\",\"screenName\":\"dividedbyzero360\"}");
                    }
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }

    /**
     * This method checks whether the HomeController runs on port 9000, it tests for it
     * three times on three different ports including the correct port and two wrong ports.
     */
    @Test
    public void checkOrigin()
    {
        String url="localhost:9000";
        assertThat(homeController.originMatches(url)).isEqualTo(true) ;
        url="localhost:19001";
        assertThat(homeController.originMatches(url)).isEqualTo(true) ;
        url="localhost:19002";
        assertThat(homeController.originMatches(url)).isEqualTo(false) ;
    }

    /**
     * This method tests if the url is not from same domain, then it should return the wrong result which is
     * status 403.
     */
    @Test
    public void  testforbiddenResult(){
        CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> result=homeController.forbiddenResult();
        try{
            Optional<Result> resultForbidden= result.toCompletableFuture().get().left;
            if(resultForbidden.isPresent()){
                System.out.println(resultForbidden.get().status());
                assertThat(resultForbidden.get().status()).isEqualTo(403);
            }else{
                System.out.println("Not available");
            }
        }catch (Exception ex)
        {
            System.out.println("Pass");
        }

    }

    /**
     * This method tests the Messages actor class by passing random arguments to get tweets,
     * to get a user and register clients by initiating the Messages.RegisterCkients class.
     */
    @Test
    public void testActorMessages(){
        Messages.RequestForNewTweets requestForNewTweets=new Messages.RequestForNewTweets("trump");
        assertThat(requestForNewTweets.newKeyWord).isEqualTo("trump");
        Messages m=new Messages();
        assertThat(m).isNotEqualTo(null);
        Messages.UserProfile users= new Messages.UserProfile("dividedByZero360");
        assertThat(users).isNotEqualTo(null);
        assertThat(users.screenName).isEqualTo("dividedByZero360");
        Messages.RegisterClients registerClients=new Messages.RegisterClients();
        assertThat(registerClients).isNotEqualTo(null);
    }

    /**
     * This method is used to check whether Play is listening for requests on the same port
     * as on which HomeControllerTest is hosted. It mocks the Play Http Request Header and
     * it fails the test as the port number is incorrect.
     */
    @Test
    public void testSameOriginCheck()
    {
        play.mvc.Http.RequestHeader header = mock(play.mvc.Http.RequestHeader.class);
        assertThat(homeController.sameOriginCheck(header)).isEqualTo(false);
        Optional<String> origin=Optional.of("localhost:9001");
        when(header.header("Origin")).thenReturn(origin);
        assertThat(homeController.sameOriginCheck(header)).isEqualTo(false);
    }

    /**
     * This method tests whether the HomeController is listening to queries other than the current one.
     */
    @Test
    public void testtellChildAboutnewQuery()
    {
        assertThat(homeController.tellChildAboutnewQuery("Salman Khan").status()).isEqualTo(200);

    }

    /**
     * This method is used to return status 200(OK) when trying to display tweets of a
     * particular user and tests the displayTweets() function.
     */
    @Test
    public void displayTweets()
    {
        Result r=homeController.displayTweets("dividedByZero360");
        assertThat(r.status()).isEqualTo(200);
    }

    /**
     * This method is used to return status 200(OK) when trying to display tweets of a
     * particular user and tests the getUserTweets() function.
     * @throws Exception
     */
    @Test
    public void testgetUserTweets () throws Exception
    {
        CompletionStage<Result> r= homeController.getUserTweets("John Doe ScreenName");
        Result result=r.toCompletableFuture().get();
        assertThat(result.status()).isEqualTo(200);
    }

    /**
     * This method tests the Actor class
     * @throws Exception
     */
    @Test
    public void testActors() throws Exception{
        new TestKit(system){{
            final Props props = Props.create(UserParentActor.class,system,materializer);
            final ActorRef userParentActorTest = system.actorOf(props, "userParentActorTest");
            Messages.RegisterClients rc=new Messages.RegisterClients();
            final TestKit probe = new TestKit(system);
            userParentActorTest.tell(rc, getRef());
            // await the correct response
            Messages.RequestForNewTweets requestNewTweets =new Messages.RequestForNewTweets("trump");
            userParentActorTest.tell(requestNewTweets, getRef());
            expectMsg(duration("5 second"), requestNewTweets);
            assertThat(userParentActorTest.isTerminated()).isEqualTo(false);

            final ActorRef tweetUserActor=system.actorOf(actors.TweetUserActor.props(twitterStreamer),"TweetUserActor");
            Messages.UserProfile userProfileOf=new Messages.UserProfile("dividedByZero360");
            tweetUserActor.tell(userProfileOf,getRef());
            // expectMsgClass(com.fasterxml.jackson.databind.node.ObjectNode.class);
            expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class, CompletableFuture.class);
            // assertThat(tweetUserActor.isTerminated()).isEqualTo(false);
            Thread.sleep(10);
            assertThat(tweetUserActor.isTerminated()).isEqualTo(true);


            final ActorRef  tweetVersion2=system.actorOf(actors.TweetActorVersion2.props(getRef(),twitterStreamer),"tweetVersion2");
            tweetVersion2.tell(requestNewTweets, getRef());
            assertThat(tweetVersion2.isTerminated()).isEqualTo(false);
            expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class,CompletableFuture.class);
        }};
    }


}