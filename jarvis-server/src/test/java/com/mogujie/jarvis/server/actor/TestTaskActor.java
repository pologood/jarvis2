package com.mogujie.jarvis.server.actor;

import com.mogujie.jarvis.protocol.RetryTaskProtos.*;
import com.mogujie.jarvis.server.scheduler.TestSchedulerBase;
import org.junit.Test;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/15.
 * used by jarvis-parent
 */
public class TestTaskActor extends TestSchedulerBase {
    @Test
    public void testRetry() {
        RestServerRetryTaskRequest request=RestServerRetryTaskRequest.newBuilder().build();

        ServerRetryTaskResponse response;
    }
}
