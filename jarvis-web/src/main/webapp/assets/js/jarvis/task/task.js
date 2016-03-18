var taskStatusJson = null;
var taskStatusColor = null;
var taskOperation = null;
var taskDetailUrl=contextPath+"/task/detail?taskId=";

$(function () {
    createDatetimePickerById("executeDate");
    createDatetimePickerById("scheduleDate");
    createDatetimePickerById("startDate");
    createDatetimePickerById("endDate");

    //初始化作业类型内容
    $.ajax({
        url:contextPath + "/assets/json/jobType.json",
        success:function(data){
            $("#jobType").select2({
                data: data,
                width: '100%',
                tags: true
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化任务类型', msg);
        }
    })

    //select采用select2 实现
    $(".input-group select").select2({width: '100%'});

    $.ajax({
        url:contextPath + "/api/task/getTaskStatus",
        async:false,
        success:function(data){
            taskStatusJson = data;

            var newData = new Array();
            var all = {};
            all["id"] = "all";
            all["text"] = "全部";
            newData.push(all);
            $(data).each(function (i, c) {
                if(c.id!=0){
                    var item = {};
                    item["id"] = c["id"];
                    item["text"] = c["text"];
                    newData.push(item);
                }

            });

            $(newData).each(function (index, content) {
                var value = content.id;
                var text = content.text;
                var input = $("<input type='checkbox' name='taskStatus'/>");
                $(input).attr("value", value);

                if (value == 'all') {
                    $(input).click(function () {
                        if (this.checked) {
                            $($("#taskStatus input")).each(function () {
                                this.checked = true;
                            });
                        }
                        else {
                            $($("#taskStatus input")).each(function () {
                                this.checked = false;
                            });
                        }
                    });
                }

                $("#taskStatus").append(input);
                $("#taskStatus").append(text);
                $("#taskStatus").append('  ');
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化执行状态', msg);
        }
    });
    //初始化颜色
    $.ajax({
        url:contextPath + "/assets/json/taskStatusColor.json",
        async:false,
        success:function(data){
            taskStatusColor = data;
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化执行状态颜色', msg);
        }
    });
    //初始化操作类型
    $.ajax({
        url:contextPath + "/assets/json/taskOperation.json",
        async:false,
        success:function(data){
            taskOperation = data;
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化操作类型', msg);
        }
    })


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
                if(data.status){
                    showMsg('error','模糊查询任务Id',data.status.msg);
                    return {
                        results: []
                    };
                }
                else{
                    return {
                        results: data.items
                    };
                }
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
    initExecuteUser();
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
                if(data.status){
                    showMsg('error','模糊查询任务名',data.status.msg);
                    return {
                        results: []
                    };
                }
                else{
                    return {
                        results: data.items
                    };
                }
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,
        width: '100%',
        tags:true
    });

    initSearchCondition();
    initData();
});

function initExecuteUser() {
    $.ajax({
        url:contextPath + "/api/common/getExecuteUsers",
        success:function(data){
            var newData = [];
            var all = {};
            all["id"] = "all";
            all["text"] = "全部";
            newData.push(all);

            $(data).each(function (i, c) {
                var item = {};
                item["id"] = c;
                item["text"] = c;
                newData.push(item);
            });
            $("#executeUser").select2({
                data: newData,
                width: '100%'
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化执行用户列表', msg);
        }
    })
}


//查找
function search() {
    $("#content").bootstrapTable("refresh");

}

function initSearchCondition() {
    if (taskQo != null) {
        if (taskQo.jobIdList != null) {
            //var data = [];
            $("#jobId").select2({data: [{id: -1, text: "撑住单元格,请@何剑"}]});
            for (var i = 0; i < taskQo.jobIdList.length; i++) {
                var jobId = taskQo.jobIdList[i];
                //data.push();
                $("#jobId").select2({data: [{id: jobId, text: jobId}]})
                    .val(jobId).trigger("change");
            }
        }
        if (taskQo.scheduleDate != null && taskQo.scheduleDate != "") {
            $("#scheduleDate").val(taskQo.scheduleDate);
        }
    }
}

//重置参数
function reset() {
    $("#scheduleDate").val("");
    $("#executeDate").val("");
    $("#startDate").val("");
    $("#endDate").val("");
    $("#jobId").val("all").trigger("change");
    $("#jobName").val("all").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#executeUser").val("all").trigger("change");
    $("#taskStatus input").each(function (i, c) {
        this.checked = false;
    });
}

//获取查询参数
function getQueryPara() {
    var queryPara = {};

    var scheduleDate = $("#scheduleDate").val();
    var executeDate = $("#executeDate").val();
    var startDate = $("#startDate").val();
    var endDate = $("#endDate").val();
    var jobIdList = $("#jobId").val();
    var jobNameList = $("#jobName").val();
    var jobTypeList = $("#jobType").val();
    var executeUserList = $("#executeUser").val();

    var taskStatus = new Array();
    var inputs = $("#taskStatus").find("input:checked[value!=all]");
    if(inputs.length==0){
        inputs = $("#taskStatus").find("input[value!=all]");
    }
    $(inputs).each(function (i, c) {
        var value = $(c).val();
        taskStatus.push(value);
    });

    jobIdList = jobIdList == "all" ? undefined : jobIdList;
    jobIdList = jobIdList == null ? undefined : jobIdList;
    jobNameList = jobNameList == 'all' ? undefined : jobNameList;
    jobNameList = jobNameList == null ? undefined : jobNameList;
    jobTypeList = jobTypeList == 'all' ? undefined : jobTypeList;
    jobTypeList = jobTypeList == null ? undefined : jobTypeList;
    executeUserList = executeUserList == "all" ? undefined : executeUserList;
    executeUserList = executeUserList == null ? undefined : executeUserList;

    queryPara["scheduleDate"] = scheduleDate;
    queryPara["executeDate"] = executeDate;
    queryPara["startDate"] = startDate;
    queryPara["endDate"] = endDate;
    queryPara["jobIdList"] = JSON.stringify(jobIdList);
    queryPara["jobNameList"] = JSON.stringify(jobNameList);
    queryPara["jobTypeList"] = JSON.stringify(jobTypeList);
    queryPara["executeUserList"] = JSON.stringify(executeUserList);
    queryPara["taskStatusArrStr"] = JSON.stringify(taskStatus);

    return queryPara;
}

//初始化数据及分页
function initData() {
    $("#content").bootstrapTable({
        columns: columns,
        pagination: true,
        sidePagination: 'server',
        search: false,
        url: contextPath + '/api/task/getTasks',
        queryParams: function (params) {
            var queryParams = getQueryPara();
            for (var key in queryParams) {
                var value = queryParams[key];
                params[key] = value;
            }
            return params;
        },responseHandler:function(res){
            if(res.status){
                showMsg("error","初始化执行列表",res.status.msg);
                return res;
            }
            else{
                return res;
            }
        },
        showColumns: true,
        showHeader: true,
        showToggle: true,
        sortable:true,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500, 1000],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'basic'
    });
}
//字段配置
var columns = [{
    field: 'taskId',
    title: '执行ID',
    switchable: true,
    sortable:true,
    visible: true,
    formatter:taskDetailFormatter
}, {
    field: 'attemptId',
    title: '最后尝试ID',
    switchable: true,
    sortable:true,
    visible: false
}, {
    field: 'jobId',
    title: '任务ID',
    sortable:true,
    switchable: true
}, {
    field: 'jobName',
    title: '任务名',
    switchable: true,
    sortable:true,
    formatter: jobNameFormatter
}, {
    field: 'jobType',
    title: '任务类型',
    sortable:true,
    switchable: true
}, {
    field: 'content',
    title: '任务内容',
    switchable: true,
    visible: false
}, {
    field: 'params',
    title: '任务参数',
    switchable: true,
    visible: false
}, {
    field: 'executeUser',
    title: '执行人',
    sortable:true,
    switchable: true
}, {
    field: 'scheduleTime',
    title: '调度时间',
    switchable: true,
    sortable:true,
    formatter: formatDateTimeWithoutYear
}, {
    field: 'dataTime',
    title: '数据时间',
    switchable: true,
    sortable:true,
    formatter: formatDateTimeWithoutYear
}, {
    field: 'progress',
    title: '进度',
    switchable: true,
    sortable:true,
    visible: false,
    formatter: progressFormatter
}, {
    field: 'workerGroupId',
    title: 'workerGroupId',
    switchable: true,
    sortable:true,
    visible: false
}, {
    field: 'workerId',
    title: 'workerId',
    sortable:true,
    switchable: true,
    visible: false
}, {
    field: 'executeStartTime',
    title: '开始执行时间',
    sortable:true,
    switchable: true,
    visible:false,
    formatter: formatDateTimeWithoutYear
}, {
    field: 'executeEndTime',
    title: '执行结束时间',
    sortable:true,
    switchable: true,
    visible:false,
    formatter: formatDateTimeWithoutYear
}, {
    field: 'executeTime',
    title: '执行时长',
    sortable:true,
    switchable: false,
    visible: true,
    formatter: formatTimeInterval
}, {
    field: 'status',
    title: '状态',
    switchable: true,
    sortable:true,
    width:'7%',
    formatter: taskStatusFormatter
}, {
    field: 'createTime',
    title: '执行创建时间',
    switchable: false,
    sortable:true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'updateTime',
    title: '执行更新时间',
    switchable: true,
    sortable:true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'submitUser',
    title: '任务创建者',
    sortable:true,
    switchable: true,
    visible: false
}, {
    field: 'appName',
    sortable:true,
    title: '应用名',
    switchable: true,
    visible: true,
    formatter:appNameFormatter
}, {
    field: 'priority',
    title: '任务优先级',
    sortable:true,
    switchable: true,
    visible: false
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    width:'12%',
    formatter: operateFormatter
}];


function taskDetailFormatter(value,row,index){
    var result="<a href='"+taskDetailUrl+value+"'>"+value+"</a>";

    return result;
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
            data["taskIds"] = [taskId];
            data["attemptId"] = attemptId;
            requestRemoteRestApi(url, text, data,true);
        }).on('pnotify.cancel', function () {
        });
}

function operateFormatter(value, row, index) {
    var jobId = row['jobId'];
    var attemptId = row['attemptId'];
    var taskId = row["taskId"];
    var status = row["status"];
    var operations = taskOperation[status];
    var operationStr = "";
    $(operations).each(function (i, c) {
        operationStr += '<li><a href="javascript:void(0)" onclick="TaskOperate(\'' + jobId + '\',\'' + taskId + '\',\'' + attemptId + '\',\'' + c.url + '\',\'' + c.text + '\')">' + c.text + '</a></li>';
    });

    var result = [
        '<div class="btn-group"> <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">查看 <span class="caret"></span> </button>',
        '<ul class="dropdown-menu">',
        '<li><a class="edit" href="' + contextPath + '/task/dependency?taskId=' + taskId + '" title="查看当前执行依赖" target="_blank">',
        '<i class="glyphicon glyphicon-object-align-horizontal text-success"></i>执行依赖',
        '</a></li>',
        '<li><a href="javascript:void(0)" onclick="showTaskHistory(' + taskId + ')" title="重试记录">',
        '<i class="glyphicon glyphicon-list text-success"></i>重试记录',
        '</a></li>',
        '</ul>',
        '</div>',
        ' <div class="btn-group"> <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">操作 <span class="caret"></span> </button>',
        '<ul class="dropdown-menu">',
        operationStr,
        '</ul>',
        '</div>'
    ].join('');

    result="<div style='white-space: nowrap'>"+result+"</div";

    return result;
}

var taskHistoryColumn = [{
    field: 'executeStartTime',
    title: '开始执行时间',
    switchable: true,
    formatter: formatDateTime
}, {
    field: 'executeEndTime',
    title: '执行结束时间',
    switchable: true,
    formatter: formatDateTime
}, {
    field: 'dataTime',
    title: '数据时间',
    switchable: true,
    formatter: formatDateTime
}, {
    field: 'executeUser',
    title: '执行者',
    switchable: true
}, {
    field: 'finishReason',
    title: '结束原因',
    switchable: true
}];

//获取taskHistory并用模态框显示
function showTaskHistory(taskId) {
    $("#taskHistory").bootstrapTable("destroy");

    var queryParams = {};
    queryParams["taskId"] = taskId;
    $.ajaxSettings.async = false;
    $("#taskHistory").bootstrapTable({
        columns: taskHistoryColumn,
        pagination: false,
        sidePagination: 'server',
        search: false,
        url: contextPath + '/api/taskHistory/getByTaskId',
        queryParams: function (params) {
            for (var key in queryParams) {
                var value = queryParams[key];
                params[key] = value;
            }
            return params;
        },responseHandler:function(res){
            if(res.status){
                showMsg("error","初始化执行历史列表",res.status.msg);
                return res;
            }
            else{
                return res;
            }
        },
        showColumns: true,
        showHeader: true,
        showToggle: true,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500, 1000],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页'
    });
    $.ajaxSettings.async = true;
    $("#taskHistoryModal").modal("show");
}

function jobNameFormatter(value, row, index) {
    var result = '<a href="' + contextPath + "/job/detail?jobId=" + row["jobId"] + '">' + value + '</a>';

    return result;
}
//执行状态格式化
function taskStatusFormatter(value, row, index) {
    var color = taskStatusColor[value].color;
    var text = taskStatusColor[value].text;
    var result = '<i class="fa fa-circle fa-2x" style="color: ' + color + '"></i>' + text;

    result="<div style='white-space:nowrap;'>"+result+"</div?";

    return result;
}
//百分比格式化
function progressFormatter(value, row, index) {
    var result = value * 100 + "%";
    return result;
}

//格式化结果
function formatResult(result) {
    return result.text;
}
//格式化结果选择框
function formatResultSelection(result) {
    return result.id;
}
function appNameFormatter(value,row,index){

    var result="<div style='white-space: nowrap'>"+value+"</div>";

    return result;
}
