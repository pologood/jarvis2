package com.mogujie.jarvis.rest.utils;

import com.google.common.base.Preconditions;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.*;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.rest.vo.JobEntryVo;
import com.mogujie.jarvis.server.domain.CommonStrategy;

/**
 * 检验函数
 *
 * @author muming
 */

public class ConvertValidUtils {


    public static ScheduleExpressionEntry ConvertScheduleExpressionEnty(JobEntryVo.ScheduleExpressionEntry input) {

        Integer expressionType = input.getExpressionType();
        Preconditions.checkArgument(expressionType != null && ScheduleExpressionType.isValid(expressionType), "scheduleExpressionType不对");

        String expression = input.getExpression();
        Preconditions.checkArgument(expression != null , "scheduleExpression不能为空");
        ScheduleExpression scheduleExpression = null;
        if (expressionType == ScheduleExpressionType.CRON.getValue()) {
            scheduleExpression = new CronExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_RATE.getValue()) {
            scheduleExpression = new FixedRateExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_DELAY.getValue()) {
            scheduleExpression = new FixedDelayExpression(expression);
        } else if (expressionType == ScheduleExpressionType.ISO8601.getValue()) {
            scheduleExpression = new ISO8601Expression(expression);
        }
        Preconditions.checkArgument(scheduleExpression.isValid(), "scheduleExpression不对");

        ScheduleExpressionEntry entry = ScheduleExpressionEntry.newBuilder()
                .setExpressionType(expressionType)
                .setScheduleExpression(expression)
                .build();
        return entry;
    }


    public static DependencyEntry ConvertDependcyEnty(JobEntryVo.DependencyEntry input) {
        Integer mode = input.getOperatorMode();
        Preconditions.checkArgument(mode != null && OperationMode.isValid(mode), "操作模式不对");

        Long preJobId = input.getPreJobId();
        Preconditions.checkArgument(preJobId != null && preJobId != 0, "依赖JobId不能为空");

        Integer commonStrategy = input.getCommonStrategy();
        Preconditions.checkArgument(commonStrategy != null && CommonStrategy.isValid(commonStrategy), "依赖的通用策略不对");

        //偏移策略可以为空，表示runtime模式。
        String offsetStrategy = input.getOffsetStrategy();
        if (offsetStrategy == null || offsetStrategy.equals("")) {
            offsetStrategy = "";
        } else {
            Preconditions.checkArgument(new TimeOffsetExpression(offsetStrategy).isValid(), "依赖的偏移策略不对");
        }

        DependencyEntry entry = DependencyEntry.newBuilder()
                .setOperator(mode)
                .setJobId(preJobId)
                .setCommonDependStrategy(commonStrategy)
                .setOffsetDependStrategy(offsetStrategy)
                .build();

        return entry;
    }


}
