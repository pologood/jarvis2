var taskStatusJson = null;
var planOperation = null;
var taskStatusColor = null;

$(function () {

    $('#dataTime').datetimepicker({
        language: 'zh-CN',
        minView: 'hour',
        format: 'yyyy-mm-dd hh:ii',
        autoclose: true
    });

    //初始化作业类型内容
    $.getJSON(contextPath + "/assets/json/jobType.json", function (data) {
        $("#jobType").select2({
            data: data,
            width: '100%',
            tags: true
        });
    });

    //初始化作业来源内容
    $.getJSON(contextPath + "/assets/json/jobPriority.json", function (data) {
        $("#priority").select2({
            data: data,
            width: '100%'
        });
    });


    //select采用select2 实现
    $(".input-group select").select2({width: '100%'});

    $("#jobId").select2({
        ajax: {
            url: contextPath + "/api/job/getSimilarJobIds",
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term, // search term
                    page: params.page
                };
            },
            processResults: function (data, page) {
                return {
                    results: data.items
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,

        width: '100%'
    });
    $("#jobName").select2({
        ajax: {
            url: contextPath + "/api/job/getSimilarJobNames",
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term, // search term
                    page: params.page
                };
            },
            processResults: function (data, page) {
                return {
                    results: data.items
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,
        width: '100%'
    });

    $.ajaxSettings.async = false;
    //
    $.getJSON(contextPath + "/api/job/getJobStatus", function (data) {
        taskStatusJson = data;
    });
    //初始化颜色
    $.getJSON(contextPath + "/assets/json/taskStatusColor.json", function (data) {
        taskStatusColor = data;
    });
    //初始化操作类型
    $.getJSON(contextPath + "/assets/json/planOperation.json", function (data) {
        planOperation = data;
    });

    $.ajaxSettings.async = true;


    initData();
});


function formatResult(result) {
    return result.text;
}
function formatResultSelection(result) {
    return result.id;
}

function search() {
    $("#content").bootstrapTable('destroy', '');
    initData();
}

function reset() {
    $("#jobId").val("").trigger("change");
    $("#jobName").val("").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#priority").val("all").trigger("change");
    $("#submitUser").val("all").trigger("change");
    $("#planDate").val("");
}


//获取查询参数
function getQueryPara() {
    var queryPara = {};

    var jobIdList = $("#jobId").val();
    var jobNameList = $("#jobNameList").val();
    var jobTypeList = $("#jobTypeList").val();
    var priorityList = $("#priority").val();
    var executeUserList = $("#executeUser").val();
    var dataTime = $("#dataTime").val();

    jobIdList = jobIdList == "all" ? undefined : jobIdList;
    jobIdList = jobIdList == null ? undefined : jobIdList;
    jobNameList = jobNameList == 'all' ? undefined : jobNameList;
    jobNameList = jobNameList == null ? undefined : jobNameList;
    jobTypeList = jobTypeList == 'all' ? undefined : jobTypeList;
    jobTypeList = jobTypeList == null ? undefined : jobTypeList;
    executeUserList = executeUserList == "all" ? undefined : executeUserList;
    executeUserList = executeUserList == null ? undefined : executeUserList;
    priorityList = priorityList == "all" ? undefined : priorityList;
    priorityList = priorityList == null ? undefined : priorityList;
    dataTime = dataTime == '' ? undefined : dataTime;

    queryPara["jobIdList"] = JSON.stringify(jobIdList);
    queryPara["jobNameList"] = JSON.stringify(jobNameList);
    queryPara["jobTypeList"] = JSON.stringify(jobTypeList);
    queryPara["executeUserList"] = JSON.stringify(executeUserList);
    queryPara["priorityList"] = JSON.stringify(priorityList);
    queryPara["dataTime"] = dataTime;

    return queryPara;
}

//初始化数据及分页
function initData() {
    var queryParams = getQueryPara();
    $("#content").bootstrapTable({
        columns: columns,
        pagination: true,
        sidePagination: 'server',
        search: false,
        url: contextPath + '/api/plan/getPlans',
        queryParams: function (params) {
            for (var key in queryParams) {
                var value = queryParams[key];
                params[key] = value;
            }
            //console.log(params);
            return params;
        },
        showColumns: true,
        showHeader: true,
        showToggle: true,
        pageNumber: 1,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500, 1000],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'all'
    });
}


var columns = [{
    field: 'taskId',
    title: '执行ID',
    switchable: true,
    visible: true
}, {
    field: 'jobId',
    title: '任务ID',
    switchable: true,
    visible: true,
    visible: false
}, {
    field: 'jobName',
    title: '任务名称',
    switchable: true
}, {
    field: 'appId',
    title: 'APP ID',
    switchable: true,
    visible: false
}, {
    field: 'appName',
    title: 'APP名',
    switchable: true,
    visible: false
}, {
    field: 'jobType',
    title: '任务类型',
    switchable: true,
    visible: true
}, {
    field: 'content',
    title: '任务内容',
    switchable: true,
    visible: false,
    formatter: StringFormatter
}, {
    field: 'priority',
    title: '任务优先级',
    switchable: true,
    visible: true
}, {
    field: 'params',
    title: '任务参数',
    switchable: true,
    visible: false,
    formatter: StringFormatter
}, {
    field: 'submitUser',
    title: '创建用户',
    switchable: true,
    visible: false
}, {
    field: 'executeUser',
    title: '执行用户',
    switchable: true,
    visible: true
}, {
    field: 'workerGroupId',
    title: 'workerGroupId',
    switchable: true,
    visible: false
}, {
    field: 'workerId',
    title: 'workerId',
    switchable: true,
    visible: false
}, {
    field: 'scheduleTime',
    title: '调度时间',
    switchable: true,
    formatter: formatDateTime
}, {
    field: 'predictExecuteTime',
    title: '预计执行时长',
    switchable: true,
    formatter: formatTimeInterval
}, {
    field: 'status',
    title: '状态',
    switchable: true,
    visible: true,
    formatter: taskStatusFormatter
}, {
    field: 'executeTime',
    title: '执行时长',
    switchable: true,
    visible: false,
    formatter: formatTimeInterval
}, {
    field: 'executeStartTime',
    title: '执行开始时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'executeEndTime',
    title: '执行结束时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'updateTime',
    title: '最后更新时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateFormatter
}];

function operateFormatter(value, row, index) {
    //console.log(row);
    var jobId = row["jobId"];
    var taskId = row["taskId"];
    var attemptId = row["attemptId"];

    var status = row["status"];
    var operations = planOperation[status];

    var operationStr = "";
    $(operations).each(function (i, c) {
        operationStr += '<li><a href="javascript:void(0)" onclick="TaskOperate(\'' + jobId + '\',\'' + taskId + '\',\'' + attemptId + '\',\'' + c.url + '\',\'' + c.text + '\')">' + c.text + '</a></li>';
    });


    //console.log(jobId);
    var result = [
        '<a class="edit" href="' + contextPath + '/plan/dependency?taskId=' + taskId + '" title="查看执行详情" target="_blank">',
        '<i class="glyphicon glyphicon-eye-open"></i>',
        '</a>  ',
        '<div class="btn-group"> <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">操作 <span class="caret"></span> </button>',
        '<ul class="dropdown-menu">',
        operationStr,
        '</ul>',
        '</div>'
    ].join('');

    //console.log(result);

    return result;
}
function taskStatusFormatter(value, row, index) {
    var color = taskStatusColor[value];
    var result = '<i class="fa fa-circle fa-2x" style="color: ' + color + '"></i>';

    return result;
}
function StringFormatter(value, row, index) {
    return value;
}

//重试还是kill，type与rest接口一一对应
function TaskOperate(jobId, taskId, attemptId, url, text) {

    (new PNotify({
        title: '任务操作',
        text: '确定' + text + "?",
        icon: 'glyphicon glyphicon-question-sign',
        hide: false,
        confirm: {
            confirm: true
        },
        buttons: {
            closer: false,
            sticker: false
        },
        history: {
            history: false
        }
    })).get().on('pnotify.confirm', function () {
            var data = {};
            data["jobId"] = jobId;
            data["taskId"] = taskId;
            data["attemptId"] = attemptId;
            requestRemoteRestApi(url, text, data);
        }).on('pnotify.cancel', function () {

        });
}