/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:35:46
 */

package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.google.common.eventbus.EventBus;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.protocol.DeleteJobProtos.RestServerDeleteJobRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.observer.Event;
import com.mogujie.jarvis.server.observer.Observable;
import com.mogujie.jarvis.server.observer.Observer;
import com.mogujie.jarvis.server.scheduler.InitEvent;
import com.mogujie.jarvis.server.scheduler.JobDescriptor;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.StopEvent;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.dag.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.RemoveJobEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.UnhandleEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;

/**
 * Actor used to schedule job with three schedulers (
 * {@link com.mogujie.jarvis.server.scheduler.time.TimeScheduler},
 * {@link com.mogujie.jarvis.server.scheduler.dag.DAGScheduler}, and
 * {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler})
 *
 * @author guangming
 *
 */
@Named("JobSchedulerActor")
@Scope("prototype")
public class JobSchedulerActor extends UntypedActor implements Observable {

    private EventBus eventBus = new EventBus("JobSchedulerActor");
    private TimeScheduler timeScheduler = TimeScheduler.getInstance();
    private DAGScheduler dagScheduler = DAGScheduler.getInstance();
    private TaskScheduler taskScheduler = TaskScheduler.getInstance();

    @Override
    public void preStart() throws Exception {
        register(timeScheduler);
        register(dagScheduler);
        register(taskScheduler);

        notify(new InitEvent());
    }

    @Override
    public void preRestart(Throwable reason, scala.Option<Object> message) throws Exception {

    }

    @Override
    public void onReceive(Object obj) throws Exception {
        Event event = new UnhandleEvent();
        if (obj instanceof WorkerReportStatusRequest) {
            WorkerReportStatusRequest msg = (WorkerReportStatusRequest)obj;
            String fullId = msg.getFullId();
            String[] idList = fullId.split("_");
            long jobId = Long.valueOf(idList[0]);
            long taskId = Long.valueOf(idList[1]);

            JobStatus status = JobStatus.getInstance(msg.getStatus());
            if (status.equals(JobStatus.SUCCESS)) {
                event = new SuccessEvent(jobId, taskId);
            } else if (status.equals(JobStatus.FAILED)) {
                event = new FailedEvent(jobId, taskId);
            }
        } else if (obj instanceof RestServerSubmitJobRequest) {
            RestServerSubmitJobRequest msg = (RestServerSubmitJobRequest)obj;
            JobDescriptor jobDesc = SchedulerUtil.convert2JobDesc(msg);
            event = new AddJobEvent(-1, jobDesc);
        } else if (obj instanceof RestServerDeleteJobRequest) {
            RestServerDeleteJobRequest msg = (RestServerDeleteJobRequest)obj;
            long jobId = msg.getJobId();
            event = new RemoveJobEvent(jobId);
        } else {
            unhandled(obj);
        }

        notify(event);
    }

    @Override
    public void postStop() throws Exception {
        notify(new StopEvent());

        eventBus.unregister(timeScheduler);
        eventBus.unregister(dagScheduler);
        eventBus.unregister(taskScheduler);
    }

    @Override
    public void register(Observer o) {
        eventBus.register(o);
    }

    @Override
    public void unregister(Observer o) {
        eventBus.unregister(o);
    }

    @Override
    public void notify(Event event) {
        eventBus.post(event);
    }

}
