/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:13:54
 */

package com.mogujie.jarvis.server.actor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RouterConfig;
import akka.routing.SmallestMailboxPool;

import com.google.common.base.Throwables;
import com.google.protobuf.GeneratedMessage;
import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.exeception.AppTokenInvalidException;
import com.mogujie.jarvis.dao.generate.AppMapper;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.AppExample;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import com.mogujie.jarvis.server.util.SpringExtension;

@Named("serverActor")
@Scope("prototype")
public class ServerActor extends UntypedActor {

    @Autowired
    private AppMapper appMapper;

    private Map<Class<?>, Pair<ActorRef, ActorEntry>> map = new HashMap<>();
    private List<Pair<ActorRef, List<ActorEntry>>> actorRefs = new ArrayList<>();

    public static Props props() {
        return Props.create(ServerActor.class);
    }

    private App queryAppByName(String appName) {
        AppExample example = new AppExample();
        example.createCriteria().andAppNameEqualTo(appName);
        List<App> list = appMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    private void addActor(String actorName, List<ActorEntry> handledMessages) {
        ActorRef actorRef = getContext().actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props(actorName));
        actorRefs.add(new Pair<>(actorRef, handledMessages));
    }

    private void addActor(String actorName, RouterConfig routerConfig, List<ActorEntry> handledMessages) {
        ActorRef actorRef = getContext()
                .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props(actorName).withRouter(routerConfig));
        actorRefs.add(new Pair<>(actorRef, handledMessages));
    }

    private void addActors() {
        addActor("taskMetricsActor", TaskMetricsActor.handledMessages());
        addActor("heartBeatActor", HeartBeatActor.handledMessages());
        addActor("workerRegistryActor", WorkerRegistryActor.handledMessages());
        addActor("taskActor", new SmallestMailboxPool(10), TaskActor.handledMessages());
        addActor("jobActor", JobActor.handledMessages());
        addActor("modifyWorkerStatusActor", ModifyWorkerStatusActor.handledMessages());
        addActor("appActor", AppActor.handledMessages());
        addActor("workerGroupActor", WorkerGroupActor.handledMessages());
        addActor("systemActor", SystemActor.handledMessages());
    }

    private Object generateResponse(Class<? extends GeneratedMessage> clazz, boolean success, String msg) {
        try {
            Method method = clazz.getMethod("getDefaultInstance", new Class[] {});
            Object object = method.invoke(null, new Object[] {});
            for (Field field : object.getClass().getDeclaredFields()) {
                if ("success_".equals(field.getName())) {
                    field.setAccessible(true);
                    field.set(object, success);
                }

                if ("message_".equals(field.getName())) {
                    field.setAccessible(true);
                    field.set(object, msg);
                }
            }

            return object;
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            Throwables.propagate(e);
        }

        return null;
    }

    @Override
    public void preStart() throws Exception {
        addActors();
        for (Pair<ActorRef, List<ActorEntry>> pair : actorRefs) {
            ActorRef actorRef = pair.getFirst();
            for (ActorEntry handledMessage : pair.getSecond()) {
                map.put(handledMessage.getRequestClass(), new Pair<ActorRef, ActorEntry>(actorRef, handledMessage));
            }
        }
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        Pair<ActorRef, ActorEntry> pair = map.get(clazz);
        if (pair == null) {
            unhandled(obj);
            return;
        }

        ActorEntry actorEntry = pair.getSecond();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == AppAuth.class) {
                field.setAccessible(true);
                AppAuth appAuth = (AppAuth) field.get(obj);
                String appName = appAuth.getName();
                App app = queryAppByName(appName);
                if (app == null) {
                    Object msg = generateResponse(actorEntry.getResponseClass(), false, "App[" + appName + "] not found");
                    getSender().tell(msg, getSelf());
                    return;
                } else {
                    try {
                        // 验证token
                        AppTokenUtils.verifyToken(app.getAppKey(), appAuth.getToken());
                        // 验证授权
                        if (actorEntry.getMessageType() == MessageType.SYSTEM && app.getAppType() != MessageType.SYSTEM.getValue()) {
                            Object msg = generateResponse(actorEntry.getResponseClass(), false, "request is rejected");
                            getSender().tell(msg, getSelf());
                            return;
                        }
                    } catch (AppTokenInvalidException e) {
                        Object msg = generateResponse(actorEntry.getResponseClass(), false, e.getMessage());
                        getSender().tell(msg, getSelf());
                        return;
                    }
                }
            }
        }

        pair.getFirst().forward(obj, getContext());
    }

}
