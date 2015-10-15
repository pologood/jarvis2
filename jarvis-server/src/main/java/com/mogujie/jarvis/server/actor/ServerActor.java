/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:13:54
 */

package com.mogujie.jarvis.server.actor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.dao.AppMapper;
import com.mogujie.jarvis.dto.App;
import com.mogujie.jarvis.dto.AppExample;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuthResponse;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RouterConfig;
import akka.routing.SmallestMailboxPool;

/**
 * ServerActor forward any messages to other actors
 *
 */
@Named("serverActor")
@Scope("prototype")
public class ServerActor extends UntypedActor {

    @Autowired
    private AppMapper appMapper;

    private Multimap<Class<?>, ActorRef> multimap = ArrayListMultimap.create();
    private List<Pair<ActorRef, Set<Class<?>>>> actorRefs = new ArrayList<>();

    public static Props props() {
        return Props.create(ServerActor.class);
    }

    private boolean verifyApp(String appName, String appKey) {
        AppExample example = new AppExample();
        example.createCriteria().andAppNameEqualTo(appName).andAppKeyEqualTo(appKey);
        List<App> list = appMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return true;
        }

        return false;
    }

    private void addActor(String actorName, Set<Class<?>> handledMessages) {
        ActorRef actorRef = getContext().actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props(actorName));
        actorRefs.add(new Pair<>(actorRef, handledMessages));
    }

    private void addActor(String actorName, RouterConfig routerConfig, Set<Class<?>> handledMessages) {
        ActorRef actorRef = getContext()
                .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props(actorName).withRouter(routerConfig));
        actorRefs.add(new Pair<>(actorRef, handledMessages));
    }

    private void addActors() {
        addActor("taskMetricsActor", TaskMetricsActor.handledMessages());
        addActor("heartBeatActor", HeartBeatActor.handledMessages());
        addActor("workerRegistryActor", WorkerRegistryActor.handledMessages());
        addActor("killTaskActor", new SmallestMailboxPool(10), KillTaskActor.handledMessages());
        addActor("jobActor", JobActor.handledMessages());
        addActor("modifyWorkerStatusActor", ModifyWorkerStatusActor.handledMessages());
        addActor("appActor", AppActor.handledMessages());
        addActor("workerGroupActor", WorkerGroupActor.handledMessages());
        addActor("systemActor", SystemActor.handledMessages());
    }

    @Override
    public void preStart() throws Exception {
        addActors();
        for (Pair<ActorRef, Set<Class<?>>> pair : actorRefs) {
            ActorRef actorRef = pair.getFirst();
            for (Class<?> handledMessage : pair.getSecond()) {
                multimap.put(handledMessage, actorRef);
            }
        }
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == AppAuth.class) {
                field.setAccessible(true);
                AppAuth appAuth = (AppAuth) field.get(obj);
                boolean vaild = verifyApp(appAuth.getName(), appAuth.getKey());
                if (!vaild) {
                    AppAuthResponse response = AppAuthResponse.newBuilder().setSuccess(false).setMessage("App验证失败").build();
                    getSender().tell(response, getSelf());
                    return;
                }
            }
        }

        Class<?> clazz = obj.getClass();
        if (multimap.containsKey(clazz)) {
            for (ActorRef actorRef : multimap.get(clazz)) {
                actorRef.forward(obj, getContext());
            }
        } else {
            unhandled(obj);
        }
    }

}
