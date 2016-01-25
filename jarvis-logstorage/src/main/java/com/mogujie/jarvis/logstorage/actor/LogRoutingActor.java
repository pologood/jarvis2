package com.mogujie.jarvis.logstorage.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.LogProtos.RestReadLogRequest;
import com.mogujie.jarvis.protocol.LogProtos.WorkerWriteLogRequest;
import com.mogujie.jarvis.protocol.LogProtos.LogStorageWriteLogResponse;
import com.mogujie.jarvis.protocol.LogProtos.LogStorageReadLogResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.List;

/**
 * @author wuya
 */
public class LogRoutingActor extends UntypedActor {

    private final static Logger logger = LogManager.getLogger();
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
            writeLog((WorkerWriteLogRequest) obj);
        } else if (obj instanceof RestReadLogRequest) {
            readLog((RestReadLogRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    private void writeLog(WorkerWriteLogRequest request) {
        try {
            long taskId = IdUtils.parse(request.getFullId(), IdType.TASK_ID);
            writerActors.get((int) taskId % size).forward(request, getContext());
        } catch (Exception e) {
            LogStorageWriteLogResponse response = LogStorageWriteLogResponse.newBuilder().setSuccess(false)
                    .setMessage(e.getMessage() != null ? e.getMessage() : e.toString()).build();
            getSender().tell(response, getSelf());
            logger.error(e);
            throw e;
        }
    }

    private void readLog(RestReadLogRequest request) {
        try {
            ActorRef ref = getContext().actorOf(LogReaderActor.props());
            ref.forward(request, getContext());
        } catch (Exception e) {
            LogStorageReadLogResponse response = LogStorageReadLogResponse.newBuilder().setSuccess(false)
                    .setMessage(e.getMessage() != null ? e.getMessage() : e.toString()).build();
            getSender().tell(response, getSelf());
            logger.error(e);
            throw e;
        }
    }

}
