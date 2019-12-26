package controllers;

import akka.NotUsed;
import akka.japi.Pair;
import akka.japi.pf.PFBuilder;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import play.libs.F;
import play.mvc.*;
import sun.util.calendar.BaseCalendar;

import javax.inject.Inject;
import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A very simple chat client using websockets.
 */
public class HomeController extends Controller {

    private final Flow<String, String, NotUsed> userFlow;
//    private PrintWriter outPrinter;

    @Inject
    public HomeController(
                          Materializer mat) {
        //noinspection unchecked
        Source<String, Sink<String, NotUsed>> source = MergeHub.of(String.class)
                .recoverWithRetries(-1, new PFBuilder().match(Throwable.class, e -> Source.empty()).build());
        Sink<String, Source<String, NotUsed>> sink = BroadcastHub.of(String.class);

        Pair<Sink<String, NotUsed>, Source<String, NotUsed>> sinkSourcePair = source.toMat(sink, Keep.both()).run(mat);
        Sink<String, NotUsed> chatSink = sinkSourcePair.first();

        Source<String, NotUsed> chatSource = sinkSourcePair.second();
        this.userFlow = Flow.fromSinkAndSource(chatSink, chatSource);
    }

    public Result index(Http.Request request) {
        String url = routes.HomeController.chat().webSocketURL(request);
        return Results.ok(views.html.chatex.render(url));
    }

    private void log(Object whatever) {
        // load filewriter just to test messages
        // todo replace with db
        try(FileWriter fw = new FileWriter("C:\\Facultate\\mpai\\example2.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(whatever);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(whatever);
    }

    public WebSocket chat() {
        return WebSocket.Text.acceptOrResult(request -> {
            if (sameOriginCheck(request)) {

                Flow<String, String, NotUsed> userFlowCompletedFuture = this.userFlow
                    .map(
                        msg -> {
                            this.log("");
                            this.log(msg);
                            return "msg: " + msg;
                        });

                return CompletableFuture.completedFuture(F.Either.Right(userFlowCompletedFuture));
            }
                // forbidden
                return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
        });
    }

    /**
     * Checks that the WebSocket comes from the same origin.  This is necessary to protect
     * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
     *
     * See https://tools.ietf.org/html/rfc6455#section-1.3 and
     * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
     */
    private boolean sameOriginCheck(Http.RequestHeader request) {
        List<String> origins = request.getHeaders().getAll("Origin");
        if (origins.size() > 1) {
            // more than one origin found
            return false;
        }
        String origin = origins.get(0);
        return originMatches(origin);
    }

    private boolean originMatches(String origin) {
        if (origin == null) return false;
        try {
            URI url = new URI(origin);
            return url.getHost().equals("localhost")
                    && (url.getPort() == 9000 || url.getPort() == 19001);
        } catch (Exception e ) {
            return false;
        }
    }

}
