/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年12月15日 下午5:37:56
 */

package com.mogujie.jarvis.server.guice;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;

public class GuiceActorProducer implements IndirectActorProducer {

    private Injector injector;
    private String actorName;

    public GuiceActorProducer(Injector injector, String actorName) {
        this.injector = injector;
        this.actorName = actorName;
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return Actor.class;
    }

    @Override
    public Actor produce() {
        return injector.getBinding(Key.get(Actor.class, Names.named(actorName))).getProvider().get();
    }

}
