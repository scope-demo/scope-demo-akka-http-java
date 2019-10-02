package com.undefinedlabs.scope;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class AkkaHttpTest {

    private static ActorSystem system;
    private static ActorMaterializer materializer;
    private static int localPort;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("testActorSystem");
        materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ActorRef userRegistryActor = system.actorOf(UserRegistryActor.props(), "userRegistryActor");

        QuickstartServer app = new QuickstartServer(system, userRegistryActor);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost" , 0), materializer).thenApply(new Function<ServerBinding, Object>() {
            @Override
            public Object apply(ServerBinding serverBinding) {
                localPort = serverBinding.localAddress().getPort();
                return serverBinding;
            }
        });
    }

    @AfterClass
    public static void teardown() throws TimeoutException, InterruptedException {
        Await.result(system.terminate(), Duration.apply(5, TimeUnit.SECONDS));
        system = null;
        materializer = null;
    }

    @Test
    public void should_call_akka_http_get_and_post_endpoints() throws IOException {
        //Given
        final OkHttpClient httpClient = new OkHttpClient.Builder().build();

        //When
        final ObjectMapper objMapper = new ObjectMapper();
        final RequestBody body = RequestBody.create(MediaType.parse("application/json"), objMapper.writeValueAsString(new UserRegistryActor.User("John Doe", 42, "SomeCountry")));
        final Request.Builder reqBuilder = new Request.Builder().url("http://localhost:"+localPort+"/users").post(body);
        final Response response = httpClient.newCall(reqBuilder.build()).execute();

        final Request.Builder reqBuilderTwo = new Request.Builder().url("http://localhost:"+localPort+"/users?q=queryparam");
        final Response responseTwo = httpClient.newCall(reqBuilderTwo.build()).execute();

        //Then
        assertThat(response.isSuccessful()).isTrue();
        assertThat(responseTwo.isSuccessful()).isTrue();
    }

}
