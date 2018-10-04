import TwitterObjects.ITwitterStreamer;
import TwitterObjects.TwitterStreamerFake;
import actors.Messages;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import controllers.HomeController;
import controllers.routes;
//import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.F;
import play.libs.typedmap.TypedKey;
import play.libs.typedmap.TypedMap;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.TestServer;
import play.test.WithApplication;
import play.twirl.api.Content;
import akka.testkit.javadsl.TestKit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import org.junit.Test;
import play.test.TestServer;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import static org.awaitility.Awaitility.*;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import static play.inject.Bindings.bind;
/**
 * A functional test starts a Play application for every test.
 *
 * https://www.playframework.com/documentation/latest/JavaFunctionalTest
 */
public class FunctionalTest extends WithApplication {

   /* @Test
    public void renderTemplate() {
        // If you are calling out to Assets, then you must instantiate an application
        // because it makes use of assets metadata that is configured from
        // the application.

        Content html = views.html.index.render("Your new application is ready.");
        assertThat("text/html").isEqualTo(html.contentType());
        assertThat(html.body()).contains("Your new application is ready.");
    }*/


   /* static HomeController homeController;
    static ActorSystem system;
    static Materializer materializer;
    static ITwitterStreamer twitterStreamer;
    Application application = new GuiceApplicationBuilder()
            .overrides(bind(ITwitterStreamer.class).to(TwitterStreamerFake.class))
            .build();
    *//*@Before
   public   void setUp()
   {
      // application.classloader();

       *//**//*Application application = new GuiceApplicationBuilder()
               .disable(Module.class)
               .build();*//**//*
      *//**//* system= ActorSystem.create();
       materializer=ActorMaterializer.create(system);
       twitterStreamer=new TwitterStreamerFake();
       homeController=new HomeController(system,materializer,twitterStreamer);*//**//*

   }*//*

  *//* @Test
   public void index(){
        Helpers help=new Helpers();
        Http.RequestBuilder requestBuilder=fakeRequest(controllers.routes.HomeController.index());
        Http.Context context=help.httpContext(requestBuilder.build());
        Http.Context.current.set(context);
        Result r=homeController.index();
        assertThat(r.status()).isEqualTo(200);

   }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
   @Test
    public void testRejectWebSocket() {
        TestServer server = testServer(37117);
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
    }*//*

   @Test
    public void testAcceptWebSocket() {

        TestServer server = testServer(9000);
        running(server, () -> {
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

                    //await().until(completionStage::isDone);
                    WebSocket websocket = completionStage.get();
                    //homeController.tellChildAboutnewQuery("SOEN");
                    //await().until(() -> websocket.isOpen() && queue.peek() != null);
                    if(websocket.isOpen())
                    {
                        System.out.println("Good news websocket is empty");
                    }
                     assertThat(websocket.isOpen()).isEqualTo(true);
                    //twitterStreamer.twitterStreamStart(null);
                    //String input = queue.take();
                    //System.out.println(input);
                    //JsonNode json = Json.parse(input);
                    //System.out.println("Here");
                    //System.out.println(json);
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }
   @Test
    public void doNothing(){

   }

    *//*@Test
    public void checkOrigin()
    {
        String url="localhost:9000";
        assertThat(homeController.originMatches(url)).isEqualTo(true) ;
        url="localhost:19001";
        assertThat(homeController.originMatches(url)).isEqualTo(true) ;
        url="localhost:19002";
        assertThat(homeController.originMatches(url)).isEqualTo(false) ;
    }*//*

   *//* @Test
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

    }*//*

   *//*@Test
    public void testSameOriginCheck()
    {
        play.mvc.Http.RequestHeader header = mock(play.mvc.Http.RequestHeader.class);
        assertThat(homeController.sameOriginCheck(header)).isEqualTo(false);
        Optional<String> origin=Optional.of("localhost:9001");
        when(header.header("Origin")).thenReturn(origin);
        assertThat(homeController.sameOriginCheck(header)).isEqualTo(false);
    }*//*
    *//*@Test
    public void testtellChildAboutnewQuery()
    {
        assertThat(homeController.tellChildAboutnewQuery("Salman Khan").status()).isEqualTo(200);

    }*//*

    *//*@Test
    public void testActorMessages(){
        Messages.RequestForNewTweets requestForNewTweets=new Messages.RequestForNewTweets("trump");
       assertThat(requestForNewTweets.newKeyWord).isEqualTo("trump");
       Messages m=new Messages();
       assertThat(m).isNotEqualTo(null);
    }*/
}
