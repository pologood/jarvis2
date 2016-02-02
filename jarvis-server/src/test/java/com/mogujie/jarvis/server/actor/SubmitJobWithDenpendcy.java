package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import com.mogujie.jarvis.core.domain.JobPriority;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobDependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.JobScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.server.actor.util.TestJarvisConstants;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.FutureUtils;
import com.typesafe.config.Config;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/2/2.
 * used by jarvis-parent
 */
public class SubmitJobWithDenpendcy {

    /**
     * 验证在有依赖时候
     * <p>
     * A   B
     * \  /
     * C
     *
     * @param args
     */
    public static void main(String[] args) {
        TaskService taskService = Injectors4Test.getInjector().getInstance(TaskService.class);
        JobService jobService = Injectors4Test.getInjector().getInstance(JobService.class);
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        ActorSystem system = ActorSystem.create(TestJarvisConstants.TEST_AKKA_SYSTEM_NAME, akkaConfig);
        String actorPath = TestJarvisConstants.TEST_SERVER_ACTOR_PATH;
        ActorSelection serverActor = system.actorSelection(actorPath);
        DateTime now = DateTime.now();
        SubmitJobWithDenpendcy submitJobWithDenpendcy = new SubmitJobWithDenpendcy();

        long jobA = submitJobWithDenpendcy.submitJob(serverActor, 1, appAuth, "hive", "show databases;", now, false, 0L, 0L);
        long jobB = submitJobWithDenpendcy.submitJob(serverActor, 1, appAuth, "shell", "ls;", now, false, 0L, 0L);
        long jobC = submitJobWithDenpendcy.submitJob(serverActor, 3, appAuth, "hive", "show databases;", now, true, jobA, jobB);


    }

    public long submitJob(ActorSelection serverActor, int minuteOffset
            , AppAuth appAuth, String jobType, String content, DateTime now
            , boolean isDependcy, long jobA, long jobB) {
        ServerSubmitJobResponse response = null;
        RestSubmitJobRequest request = null;
        List<DependencyEntry> dependencyEntryList = null;
        String timeExpression = "R1/" + DateTime.now().plusMinutes(minuteOffset).toString() + "/PT1H";
        List<ScheduleExpressionEntry> expressionEntries = Lists.newArrayList();
        //添加时间依赖
        ScheduleExpressionEntry expressionEntry = ScheduleExpressionEntry.newBuilder()
                .setExpressionType(ScheduleExpressionType.ISO8601.getValue())
                .setOperator(OperationMode.ADD.getValue())
                .setScheduleExpression(timeExpression)
                .setExpressionId(35L)
                .build();

        expressionEntries.add(expressionEntry);

        if (isDependcy) {  //添加任务依赖
            dependencyEntryList = Lists.newArrayList();

            DependencyEntry dependencyEntryA = DependencyEntry.newBuilder()
                    .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                    .setJobId(jobA).setOffsetDependStrategy("d(1)")
                    .setOperator(OperationMode.ADD.getValue())
                    .build();
            DependencyEntry dependencyEntryB = DependencyEntry.newBuilder()
                    .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                    .setJobId(jobB).setOffsetDependStrategy("d(1)")
                    .setOperator(OperationMode.ADD.getValue())
                    .build();
            dependencyEntryList.add(dependencyEntryA);
            dependencyEntryList.add(dependencyEntryB);
        }

        request = RestSubmitJobRequest.newBuilder()
                .setJobName("qh_submit")
                .setAppName("jarvis-web")
                .setAppAuth(appAuth)
                .setContent(content)
                .setPriority(JobPriority.HIGH.getValue())
                .setParameters("{\"para1\":\"1\",\"para2\":\"2\"}")
                .setStatus(JobStatus.ENABLE.getValue())
                .setUser("qinghuo")
                .addAllExpressionEntry(expressionEntries)
                .addAllDependencyEntry(dependencyEntryList)
                .setExpiredTime(86400)
                .setFailedAttempts(3)
                .setFailedInterval(3)
                .setBizGroupId(11)
                .setJobType(jobType)
                .setWorkerGroupId(1)
                .setActiveStartTime(now.getMillis())
                .setActiveEndTime(now.plusHours(1).getMillis())
                .build();

        try {
            response = (ServerSubmitJobResponse) FutureUtils.awaitResult(serverActor, request, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.getJobId();
    }

    static class CheckService implements Runnable {
        DateTime now;
        long jobA;
        long jobB;
        long jobC;
        TaskService taskService;
        JobService jobService;
        boolean isLastOne;

        public CheckService(DateTime now, long jobA, long jobB, long jobC, TaskService taskService, JobService jobService, boolean isLastOne) {
            this.now = now;
            this.jobA = jobA;
            this.jobB = jobB;
            this.jobC = jobC;
            this.taskService = taskService;
            this.jobService = jobService;
            this.isLastOne = isLastOne;
        }

        @Override
        public void run() {
            while (true) {

            }


        }


    }
}
