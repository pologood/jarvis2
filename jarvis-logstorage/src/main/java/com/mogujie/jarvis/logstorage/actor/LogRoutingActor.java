package com.mogujie.jarvis.logstorage.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestReadLogRequest;
import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;


import java.util.ArrayList;
import java.util.List;

/**
 * @author wuya
 */
public class LogRoutingActor extends UntypedActor {

    private int size;
    private List<ActorRef> writerActors = new ArrayList<ActorRef>();

    public LogRoutingActor(int size) {
        this.size = size;
        for (int i = 0; i < size; i++) {
            writerActors.add(getContext().actorOf(LogWriterActor.props()));
        }
    }

    public static Props props(int size) {
        return Props.create(LogRoutingActor.class, size);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerWriteLogRequest) {
            WorkerWriteLogRequest msg = (WorkerWriteLogRequest) obj;
            long taskId = IdUtils.parse(msg.getFullId(), IdType.TASK_ID);
            writerActors.get((int) taskId % size).tell(msg, getSelf());
        } else if (obj instanceof RestReadLogRequest) {
            ActorRef ref = getContext().actorOf(LogReaderActor.props());
            ref.forward(obj, getContext());
        } else {
            unhandled(obj);
        }
    }

}
