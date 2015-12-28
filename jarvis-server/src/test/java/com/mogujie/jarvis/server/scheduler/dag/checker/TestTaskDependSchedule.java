///*
// * 蘑菇街 Inc.
// * Copyright (c) 2010-2015 All Rights Reserved.
// *
// * Author: guangming
// * Create Date: 2015年12月21日 下午1:34:40
// */
//
//package com.mogujie.jarvis.server.scheduler.dag.checker;
//
//import org.joda.time.DateTime;
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.mogujie.jarvis.core.expression.DependencyExpression;
//import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
//
///**
// * @author guangming
// *
// */
//public class TestTaskDependSchedule {
//
//    private long myJobId;
//    private long preJobId;
//    private long task1Id = 1;
//    private long task2Id = 2;
//    private long task3Id = 3;
//    private long t1 = new DateTime("2015-10-10T10:10:00").getMillis();
//    private long t2 = new DateTime("2015-10-11T11:11:00").getMillis();
//    private long t3 = new DateTime("2015-10-12T12:12:00").getMillis();
//
//    @Test
//    public void testRuntime() {
//        TaskDependSchedule dependSchedule = new TaskDependSchedule(myJobId, preJobId, null);
//        dependSchedule.scheduleTask(task1Id, t1);
//        dependSchedule.scheduleTask(task2Id, t2);
//
//        Assert.assertEquals(true, dependSchedule.check(t1));
//        dependSchedule.resetSelected();
//        Assert.assertEquals(true, dependSchedule.check(t2));
//        dependSchedule.resetSelected();
//        Assert.assertEquals(true, dependSchedule.check(t3));
//        dependSchedule.finishSchedule();
//        Assert.assertEquals(false, dependSchedule.check(t1));
//        Assert.assertEquals(false, dependSchedule.check(t2));
//        Assert.assertEquals(false, dependSchedule.check(t3));
//    }
//
//    @Test
//    public void testCurrentOffset() {
//        DependencyExpression dependencyExpression = new TimeOffsetExpression("cd");
//        TaskDependSchedule dependSchedule = new TaskDependSchedule(myJobId, preJobId, dependencyExpression);
//
//        dependSchedule.scheduleTask(task1Id, t1);
//        dependSchedule.scheduleTask(task2Id, t2);
//
//        Assert.assertEquals(true, dependSchedule.check(t1));
//        dependSchedule.resetSelected();
//        Assert.assertEquals(true, dependSchedule.check(t2));
//        dependSchedule.resetSelected();
//        Assert.assertEquals(false, dependSchedule.check(t3));
//
//        Assert.assertEquals(true, dependSchedule.check(t1));
//        dependSchedule.finishSchedule();
//        Assert.assertEquals(true, dependSchedule.check(t2));
//        dependSchedule.finishSchedule();
//
//        Assert.assertEquals(false, dependSchedule.check(t1));
//        Assert.assertEquals(false, dependSchedule.check(t2));
//        Assert.assertEquals(false, dependSchedule.check(t3));
//    }
//
//    @Test
//    public void testPastOffset() {
//
//    }
//
//    @Test
//    public void testFutureOffset() {
//
//    }
//}
