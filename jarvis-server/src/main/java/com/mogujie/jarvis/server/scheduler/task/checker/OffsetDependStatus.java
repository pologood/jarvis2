/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:10:47
 */
package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;
import org.joda.time.DateTime;

/**
 * @author guangming
 *
 */
public class OffsetDependStatus extends AbstractTaskStatus {

    private DependencyExpression offsetStrategy;

    public OffsetDependStatus(long myJobId, long preJobId, DependencyStrategyExpression commonStrategy,
            DependencyExpression offsetStrategy) {
        super(myJobId, preJobId, commonStrategy);
        this.offsetStrategy = offsetStrategy;
    }

    public DependencyExpression getOffsetStrategy() {
        return offsetStrategy;
    }

    public void setOffsetStrategy(DependencyExpression offsetStrategy) {
        this.offsetStrategy = offsetStrategy;
    }

    @Override
    public List<Long> getDependTaskIds() {
        return new ArrayList<Long>();
    }

    @Override
    public void setDependTaskIds(List<Long> dependTaskIds) {
    }

    @Override
    protected List<Boolean> getStatusList() {
        TaskService taskService = SpringContext.getBean(TaskService.class);
        return taskService.getTaskSuccessStatusBetween(getMyJobId(),offsetStrategy.getRange(DateTime.now()));
    }
}
