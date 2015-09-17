$(function(){
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        $("#jobType").select2({
            data:data,
            width:'100%'
        });
    });

    $.getJSON("/assets/jarvis/json/executeCycle.json",function(data){
        $("#executeCycle").select2({
            data:data,
            width:'100%'
        });
    });

    $.getJSON("/assets/jarvis/json/jobSource.json",function(data){
        $("#jobSource").select2({
            data:data,
            width:'100%'
        });
    });
    $.getJSON("/assets/jarvis/json/jobPriority.json",function(data){
        $("#jobPriority").select2({
            data:data,
            width:'100%'
        });
    });


    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});

    initData();
});





//查找
function search(){
    initData();
}
//重置参数
function reset(){
    $("#jobId").val("all").trigger("change");
    $("#jobName").val("all").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#submitUser").val("all").trigger("change");
    $("#executeCycle").val("all").trigger("change");
    $("#jobSource").val("all").trigger("change");
    $("#jobPriority").val("all").trigger("change");
}


//获取查询参数
function getQueryPara(){
    var queryPara={};

    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
    var jobType=$("#jobType").val();
    var jobSource=$("#jobSource").val();
    var jobPriority=$("#jobPriority").val();
    var submitUser=$("#submitUser").val();
    var executeCycle=$("#executeCycle").val();

    queryPara["jobId"]=jobId;
    queryPara["jobName"]=jobName;
    queryPara["jobType"]=jobType;
    queryPara["jobSource"]=jobSource;
    queryPara["jobPriority"]=jobPriority;
    queryPara["submitUser"]=submitUser;
    queryPara["executeCycle"]=executeCycle;

    return queryPara;
}

//初始化数据及分页
function initData(){
    var queryParams=getQueryPara();
    $("#content").bootstrapTable({
        columns:columns,
        pagination:true,
        sidePagination:'server',
        search:true,
        url:'/jarvis/api/job/getJobs',
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
        pageSize:1,
        pageSize:10,
        pageList:[5,10,25,50,100,200,500],
        paginationFirstText:'首页',
        paginationPreText:'上一页',
        paginationNextText:'下一页',
        paginationLastText:'末页'
    });
}








var columns=[{
    field: 'jobId',
    title: '任务id',
    switchable:true
}, {
    field: 'originJobId',
    title: '原始任务id',
    switchable:true,
    visible:false
}, {
    field: 'jobName',
    title: '任务名',
    switchable:true
}, {
    field: 'jobType',
    title: '任务类型',
    switchable:true
}, {
    field: 'jobStatus',
    title: '任务状态',
    switchable:true
}, {
    field: 'content',
    title: '任务内容',
    switchable:true
}, {
    field: 'params',
    title: '参数',
    switchable:true,
    visible:false
}, {
    field: 'submitUser',
    title: '提交人',
    switchable:true
}, {
    field: 'priority',
    title: '优先级',
    switchable:true
}, {
    field: 'appName',
    title: '应用名',
    switchable:true
}, {
    field: 'workerGroupId',
    title: 'worker组ID',
    switchable:true,
    visible:false
}, {
    field: 'rejectAttempts',
    title: '任务被Worker拒绝时的重试次数',
    switchable:true,
    visible:false
}, {
    field: 'rejectInterval',
    title: '任务被Worker拒绝时重试的间隔(秒)',
    switchable:true,
    visible:false
}, {
    field: 'failedAttempts',
    title: '任务运行失败时的重试次数',
    switchable:true,
    visible:false
}, {
    field: 'failedInterval',
    title: '任务运行失败时重试的间隔(秒)',
    switchable:true,
    visible:false
}];
