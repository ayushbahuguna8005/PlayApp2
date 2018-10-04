

/*import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.BoundRequestBuilder;
import play.shaded.ahc.org.asynchttpclient.ListenableFuture;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocketListener;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocketTextListener;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

*//**
 * A quick wrapper around AHC WebSocket
 *
 * https://github.com/AsyncHttpClient/async-http-client/blob/2.0/client/src/main/java/org/asynchttpclient/ws/WebSocket.java
 *//*
public class WebSocketClient {

    private AsyncHttpClient client;

    public WebSocketClient(AsyncHttpClient c) {
        this.client = c;
    }

    public CompletableFuture<WebSocket> call(String url, WebSocketTextListener listener) throws ExecutionException, InterruptedException {
        final BoundRequestBuilder requestBuilder = client.prepareGet(url);

        final WebSocketUpgradeHandler handler = new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build();
        final ListenableFuture<WebSocket> future = requestBuilder.execute(handler);
        return future.toCompletableFuture();
    }

    static class LoggingListener implements WebSocketTextListener {
        private final Consumer<String> onMessageCallback;

        public LoggingListener(Consumer<String> onMessageCallback) {
            this.onMessageCallback = onMessageCallback;
        }

        private Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingListener.class);

        private Throwable throwableFound = null;

        public Throwable getThrowable() {
            return throwableFound;
        }

        public void onOpen(WebSocket websocket) {
            //logger.info("onClose: ");
            //websocket.sendMessage("hello");
        }

        public void onClose(WebSocket websocket) {
            //logger.info("onClose: ");
        }

        public void onError(Throwable t) {
            //logger.error("onError: ", t);
            throwableFound = t;
        }

        @Override
        public void onMessage(String s) {
            //logger.info("onMessage: s = " + s);
            onMessageCallback.accept(s);
        }
    }

}*/



import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
        import play.shaded.ahc.org.asynchttpclient.BoundRequestBuilder;
        import play.shaded.ahc.org.asynchttpclient.ListenableFuture;
        import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
        import play.shaded.ahc.org.asynchttpclient.ws.WebSocketListener;
        import play.shaded.ahc.org.asynchttpclient.ws.WebSocketTextListener;
        import play.shaded.ahc.org.asynchttpclient.ws.WebSocketUpgradeHandler;
        import org.slf4j.Logger;

        import java.util.concurrent.CompletableFuture;
        import java.util.concurrent.ExecutionException;
        import java.util.function.Consumer;

/**
 * A quick wrapper around AHC WebSocket
 *
 * https://github.com/AsyncHttpClient/async-http-client/blob/2.0/client/src/main/java/org/asynchttpclient/ws/WebSocket.java
 */
public class WebSocketClient {

    private AsyncHttpClient client;

    /**
     *This is the constructor for the class WebSocketClient and takes an instance of an Asynchronous HTTP Client as parameter
     * and initializes the private client variable of the class.
     * @param c AsyncHttpClient
     **/
    public WebSocketClient(AsyncHttpClient c) {
        this.client = c;
    }

    /**
     * This function returns a CompletableFuture object. First, an asynchronous request is created for the tweet,
     * then a WebSocketUpgradeHandler is created. Then a ListenableFuture of type WebSocket is created using
     * the BoundRequestBuilder and WebSocketUpgradeHandler created earlier and then this ListenableFuture is returned
     * as a CompletableFuture.
     * @param url String
     * @param listener WebSocketTextListener
     * @return CompletableFuture of WebSocket
     * @throws ExecutionException
     * @throws InterruptedException
     **/
    public CompletableFuture<WebSocket> call(String url, WebSocketTextListener listener) throws ExecutionException, InterruptedException {
        final BoundRequestBuilder requestBuilder = client.prepareGet(url);

        final WebSocketUpgradeHandler handler = new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build();
        final ListenableFuture<WebSocket> future = requestBuilder.execute(handler);
        return future.toCompletableFuture();
    }

    /**
     * This class implements WebSocketTextListener and is used to listen to tweets received from Twitter api
     * and is used to display data received from twitter4j library.
     */
    static class LoggingListener implements WebSocketTextListener {
        private final Consumer<String> onMessageCallback;

        /**
         * This method accepts a Consumer object of type String and assigns it to the private Consumer
         * onMessageCallback variable which is used to do callbacks.
         * @param onMessageCallback Consumer for String
         */
        public LoggingListener(Consumer<String> onMessageCallback) {
            this.onMessageCallback = onMessageCallback;
        }

        private Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingListener.class);

        private Throwable throwableFound = null;

        /**
         * This method is used to throw an error and returns a Throwable object.
         * @return Throwable
         */
        public Throwable getThrowable() {
            return throwableFound;
        }

        /**
         * A WebSocket has been connected. This is the first invocation and it is made at most once.
         * @param websocket WebSocket
         */
        public void onOpen(WebSocket websocket) {
            //logger.info("onClose: ");
            //websocket.sendMessage("hello");
        }

        /**
         * This is the last invocation from the WebSocket. By the time this invocation begins the WebSocket's
         * input will have been closed. Be prepared to receive this invocation at any time after onOpen
         * regardless of whether or not any messages have been requested from the WebSocket.
         * @param websocket WebSocket
         */
        public void onClose(WebSocket websocket) {
            //logger.info("onClose: ");
        }

        /**
         * This is the last invocation from the WebSocket. By the time this invocation begins both WebSocket's
         * input and output will have been closed. If an exception is thrown from this method,
         * resulting behavior is undefined.
         * @param t Throwable
         */
        public void onError(Throwable t) {
            //logger.error("onError: ", t);
            throwableFound = t;
        }

        /**
         * This method receives a call back message .
         * @param s String
         */
        @Override
        public void onMessage(String s) {
            //logger.info("onMessage: s = " + s);
            onMessageCallback.accept(s);
        }
    }
}

