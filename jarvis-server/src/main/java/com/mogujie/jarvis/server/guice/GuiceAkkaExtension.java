/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年12月15日 下午5:47:01
 */

package com.mogujie.jarvis.server.guice;

import com.google.inject.Injector;
import com.mogujie.jarvis.server.guice.GuiceAkkaExtension.GuiceAkkaExtensionImpl;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;

public class GuiceAkkaExtension extends AbstractExtensionId<GuiceAkkaExtensionImpl> {

    private static final GuiceAkkaExtension INSTANCE = new GuiceAkkaExtension();

    private GuiceAkkaExtension() {
    }

    public static GuiceAkkaExtension getInstance() {
        return INSTANCE;
    }

    public static GuiceAkkaExtensionImpl getExtensionInstance() {
        return GuiceAkkaExtensionImpl.INSTANCE;
    }

    @Override
    public GuiceAkkaExtensionImpl createExtension(ExtendedActorSystem system) {
        return GuiceAkkaExtensionImpl.INSTANCE;
    }

    public static class GuiceAkkaExtensionImpl implements Extension {

        private Injector injector;
        private static final GuiceAkkaExtensionImpl INSTANCE = new GuiceAkkaExtensionImpl();

        private GuiceAkkaExtensionImpl() {
        }

        public void initialize(Injector injector) {
            this.injector = injector;
        }

        public Props props(String actorName) {
            return Props.create(GuiceActorProducer.class, injector, actorName);
        }
    }

}
