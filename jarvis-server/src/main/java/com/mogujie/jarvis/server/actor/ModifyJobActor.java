/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 下午3:20:53
 */

package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.ServerModifyJobResponse;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("modifyJobActor")
@Scope("prototype")
public class ModifyJobActor extends UntypedActor {

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerModifyJobRequest) {
            RestServerModifyJobRequest request = (RestServerModifyJobRequest) obj;
            long jobId = request.getJobId();

            // TODO 修改Job

            ServerModifyJobResponse response = ServerModifyJobResponse.newBuilder().setJobId(jobId).setSuccess(true).setMessage("").build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

}
