/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 下午3:08:13
 */

package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.ServerModifyJobFlagResponse;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("modifyJobFlagActor")
@Scope("prototype")
public class ModifyJobFlagActor extends UntypedActor {

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerModifyJobFlagRequest) {
            RestServerModifyJobFlagRequest request = (RestServerModifyJobFlagRequest) obj;
            long jobId = request.getJobId();

            // TODO 修改Job Flag
            ServerModifyJobFlagResponse response = ServerModifyJobFlagResponse.newBuilder().setSuccess(true).setMessage("").build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

}
