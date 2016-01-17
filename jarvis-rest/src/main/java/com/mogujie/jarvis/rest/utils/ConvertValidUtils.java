package com.mogujie.jarvis.rest.utils;

import com.google.common.base.Preconditions;
import com.mogujie.jarvis.core.domain.AlarmStatus;
import com.mogujie.jarvis.core.domain.AlarmType;
import com.mogujie.jarvis.core.domain.AppStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.core.util.ExpressionUtils;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.rest.vo.*;

import com.mogujie.jarvis.server.domain.CommonStrategy;

import java.util.Arrays;

/**
 * 检验函数
 *
 * @author muming
 */

public class ConvertValidUtils {

    /**
     * 计划表达式-转换
     *
     * @param input
     * @return
     */
    public static ScheduleExpressionEntry ConvertScheduleExpressionEntry(JobScheduleExpVo.ScheduleExpressionEntry input) {

        Integer mode = input.getOperatorMode();
        Preconditions.checkArgument(mode != null && OperationMode.isValid(mode), "操作模式不对");

        Long expressionId = input.getExpressionId() == null ? 0 : input.getExpressionId();
        if (mode == OperationMode.EDIT.getValue() || mode == OperationMode.DELETE.getValue()) {
            Preconditions.checkArgument(expressionId != 0, "删除与编辑模式下,计划表达式ID不能为空");
        }

        Integer expressType = input.getExpressionType();
        String expression = input.getExpression();
        //[追加]与[修改]模式下,计划表达式的内容要做检查
        if (mode == OperationMode.ADD.getValue() || mode == OperationMode.EDIT.getValue()) {
            ExpressionUtils.checkExpression(expressType, expression);
        }

        ScheduleExpressionEntry entry = ScheduleExpressionEntry.newBuilder()
                .setOperator(mode)
                .setExpressionId(expressionId)
                .setExpressionType(expressType == null ? 0 : expressType)
                .setScheduleExpression(expression == null ? "" : expression)
                .build();
        return entry;
    }

    /**
     * 依赖-转换
     *
     * @param input
     * @return
     */
    public static DependencyEntry ConvertDependencyEntry(JobDependencyVo.DependencyEntry input) {
        Integer mode = input.getOperatorMode();
        Preconditions.checkArgument(mode != null && OperationMode.isValid(mode), "操作模式不对.value:" + input.getOperatorMode());

        Long preJobId = input.getPreJobId();
        Preconditions.checkArgument(preJobId != null && preJobId != 0, "依赖JobId不能为空");

        Integer commonStrategy = input.getCommonStrategy();
        String offsetStrategy = input.getOffsetStrategy();

        if (mode == OperationMode.ADD.getValue() || mode == OperationMode.EDIT.getValue()) {
            Preconditions.checkArgument(commonStrategy != null, "依赖的通用策略不能为空.value:" + commonStrategy);
            Preconditions.checkArgument(CommonStrategy.isValid(commonStrategy), "依赖的通用策略不对.value:" + commonStrategy);

            Preconditions.checkArgument(offsetStrategy == null || offsetStrategy.equals("") || new TimeOffsetExpression(offsetStrategy).isValid()
                    , "依赖的偏移策略不对.value:" + input.getOffsetStrategy());
        }

        DependencyEntry entry = DependencyEntry.newBuilder()
                .setOperator(mode).setJobId(preJobId)
                .setCommonDependStrategy(commonStrategy == null ? 0 : commonStrategy)
                .setOffsetDependStrategy(offsetStrategy == null ? "" : offsetStrategy)
                .build();
        return entry;
    }

    /**
     * APP内容检查
     */
    public static void checkAppVo(OperationMode mode, String appName, String owner, Integer status, Integer maxConcurrency) {

        //追加模式
        if (mode == OperationMode.ADD) {
            Preconditions.checkArgument(appName != null && !appName.trim().equals(""), "appName不能为空");
            Preconditions.checkArgument(owner != null && !owner.trim().equals(""), "owner不能为空");
            Preconditions.checkArgument(status != null, "status不能为空");
            Preconditions.checkArgument(AppStatus.isValid(status), "status内容不对。value:" + status);
        }
        //编辑模式
        if (mode == OperationMode.EDIT) {
            Preconditions.checkArgument(appName == null || !appName.trim().equals(""), "appName不能为空");
            Preconditions.checkArgument(owner == null || !owner.trim().equals(""), "owner不能为空");
            Preconditions.checkArgument(status == null || AppStatus.isValid(status), "status内容不对。value:" + status);
        }
    }

    /**
     * alarm内容检查
     */
    public static void checkAlarm(OperationMode mode, AlarmVo vo) {

        Long jobId = vo.getJobId();
        Preconditions.checkArgument(!mode.isIn(OperationMode.ADD,OperationMode.EDIT,OperationMode.DELETE)
        || (jobId != null && jobId != 0),"jobId不能为空。");

        String type = vo.getAlarmType();
        Preconditions.checkArgument( !mode.isIn(OperationMode.ADD) || type != null,"alarmType不能为空。");
        Preconditions.checkArgument( type == null || AlarmType.isValid(type),"alarmType不对。value:" + type);

        Preconditions.checkArgument( !mode.isIn(OperationMode.ADD) || vo.getReceiver() != null,"receiver不能为空。");

        Integer status = vo.getStatus();
        Preconditions.checkArgument( !mode.isIn(OperationMode.ADD) || status != null,"status不能为空。");
        Preconditions.checkArgument(status == null || AlarmStatus.isValid(status),"status类型不对。 value:" + status);

    }

    /**
     * appWorkerGroup检查
     */
    public static void checkAppWorkerGroup(OperationMode mode, Integer appId, Integer workerGroupId) {
        Preconditions.checkArgument((appId != null && appId != 0),"jobId不能为空。");
        Preconditions.checkArgument((workerGroupId != null && workerGroupId != 0),"workerGroupId不能为空。");
    }



    /**
     * alarm查询检查
     */
    public static void checkAlarmQuery(AlarmQueryVo vo) {
        String jobIds = vo.getJobIds();
    }




}
