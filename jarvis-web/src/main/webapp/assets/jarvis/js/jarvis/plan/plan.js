var taskStatusJson=null;

$(function(){

    $('#planDate').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    //初始化作业类型内容
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        $("#jobType").select2({
            data:data,
            width:'100%'
        });
    });

    //初始化作业来源内容
    $.getJSON("/assets/jarvis/json/jobPriority.json",function(data){
        $("#priority").select2({
            data:data,
            width:'100%'
        });
    });



    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});

    $("#jobId").select2({
        ajax: {
            url: "/jarvis/api/job/getSimilarJobIds",
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
        escapeMarkup: function (markup) { return markup; },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,

        width:'100%'
    });
    $("#jobName").select2({
        ajax: {
            url: "/jarvis/api/job/getSimilarJobNames",
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
        escapeMarkup: function (markup) { return markup; },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,
        width:'100%'
    });

    $.ajaxSettings.async = false;
    $.getJSON("/assets/jarvis/json/taskStatus.json",function(data){
        taskStatusJson=data;
    });
    $.ajaxSettings.async = true;



    initData();
});


function formatResult(result){
    return result.text;
}
function formatResultSelection(result){
    return result.id;
}

function search(){
    $("#content").bootstrapTable('destroy','');
    initData();
}

function reset(){
    $("#jobId").val("").trigger("change");
    $("#jobName").val("").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#priority").val("all").trigger("change");
    $("#submitUser").val("all").trigger("change");
    $("#planDate").val("");
}


//获取查询参数
function getQueryPara(){
    var queryPara={};

    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
    var jobType=$("#jobType").val();
    var priority=$("#priority").val();
    var submitUser=$("#submitUser").val();
    var planDate=$("#planDate").val();

    jobId=jobId=="all"?'':jobId;
    jobName=jobName=='all'?'':jobName;
    jobType=jobType=='all'?'':jobType;
    submitUser=submitUser=="all"?'':submitUser;
    priority=priority=="all"?'':priority;
    planDate=planDate==''?undefined:planDate;

    queryPara["jobId"]=jobId;
    queryPara["jobName"]=jobName;
    queryPara["jobType"]=jobType;
    queryPara["submitUser"]=submitUser;
    queryPara["priority"]=priority;
    queryPara["planDate"]=planDate;

    return queryPara;
}

//初始化数据及分页
function initData(){
    var queryParams=getQueryPara();
    $("#content").bootstrapTable({
        columns:columns,
        pagination:true,
        sidePagination:'server',
        search:false,
        url:'/jarvis/api/plan/getPlans',
        queryParams:function(params) {
            for(var key in queryParams){
                var value = queryParams[key];
                params[key]=value;
            }
            return params;
        },
        showColumns:true,
        showHeader:true,
        showToggle:true,
        pageNumber:1,
        pageSize:20,
        pageList:[10,20,50,100,200,500,1000],
        paginationFirstText:'首页',
        paginationPreText:'上一页',
        paginationNextText:'下一页',
        paginationLastText:'末页',
        showExport:true,
        exportTypes:['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType:'all'
    });
}








var columns=[{
    field: 'taskId',
    title: '执行ID',
    switchable:true,
    visible:true
}, {
    field: 'jobId',
    title: '任务ID',
    switchable:true,
    visible:true
}, {
    field: 'jobName',
    title: '任务名称',
    switchable:true
},{
    field: 'appId',
    title: 'APP ID',
    switchable:true,
    visible:false
},{
    field: 'appName',
    title: 'APP名',
    switchable:true,
    visible:false
},{
    field: 'jobType',
    title: '任务类型',
    switchable:true,
    visible:false
}, {
    field: 'content',
    title: '任务内容',
    switchable:true,
    visible:false,
    formatter:StringFormatter
},  {
    field: 'priority',
    title: '任务优先级',
    switchable:true,
    visible:false
}, {
    field: 'params',
    title: '任务参数',
    switchable:true,
    visible:false,
    formatter:StringFormatter
}, {
    field: 'submitUser',
    title: '创建用户',
    switchable:true,
    visible:false
},{
    field: 'executeUser',
    title: '执行用户',
    switchable:true,
    visible:true
},{
    field: 'workerGroupId',
    title: 'workerGroupId',
    switchable:true,
    visible:false
},{
    field: 'workerId',
    title: 'workerId',
    switchable:true,
    visible:false
},{
    field: 'scheduleTime',
    title: '调度时间',
    switchable:true,
    formatter:formatDateTime
},{
    field: 'status',
    title: '状态',
    switchable:true,
    visible:true,
    formatter:taskStatusFormatter
},{
    field: 'executeStartTime',
    title: '执行开始时间',
    switchable:true,
    visible:false,
    formatter:formatDateTime
},{
    field: 'executeEndTime',
    title: '执行结束时间',
    switchable:true,
    visible:false,
    formatter:formatDateTime
},{
    field: 'executeTime',
    title: '执行时长',
    switchable:true,
    visible:false,
    formatter:formatTimeInterval
},{
    field: 'createTime',
    title: '创建时间',
    switchable:true,
    visible:false,
    formatter:formatDateTime
}, {
    field: 'updateTime',
    title: '最后更新时间',
    switchable:true,
    visible:false,
    formatter:formatDateTime
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateFormatter
}];

function operateFormatter(value, row, index) {
    //console.log(row);
    var taskId=row["taskId"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="/jarvis/plan/dependency?taskId='+taskId+'" title="查看执行详情" target="_blank">',
        '<i class="glyphicon glyphicon-eye-open"></i>',
        '</a>  '
    ].join('');

    //console.log(result);

    return result;
}
function taskStatusFormatter(value,row,index){
    return formatStatus(taskStatusJson,value);
}
function StringFormatter(value,row,index){
    return value;
}