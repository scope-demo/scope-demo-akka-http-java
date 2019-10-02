package com.undefinedlabs.scope;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class QuickstartServer extends AllDirectives {

    private final UserRoutes userRoutes;

    public QuickstartServer(ActorSystem system, ActorRef userRegistryActor) {
        userRoutes = new UserRoutes(system, userRegistryActor);
    }

    protected Route createRoute() {
        return userRoutes.routes();
    }
}



