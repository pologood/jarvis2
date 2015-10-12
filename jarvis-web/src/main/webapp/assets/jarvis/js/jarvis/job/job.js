$(function(){
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        $("#jobType").select2({
            data:data,
            width:'100%'
        });
    });
    /*
    $.getJSON("/assets/jarvis/json/executeCycle.json",function(data){
        $("#executeCycle").select2({
            data:data,
            width:'100%'
        });
    });
    */

    $.getJSON("/assets/jarvis/json/jobFlag.json",function(data){
        $("#jobFlag").select2({
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
    $("#content").bootstrapTable('destroy','');
    initData();
}
//重置参数
function reset(){
    $("#jobId").val("all").trigger("change");
    $("#jobName").val("all").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#submitUser").val("all").trigger("change");
    //$("#executeCycle").val("all").trigger("change");
    $("#jobFlag").val("all").trigger("change");
    $("#jobPriority").val("all").trigger("change");
}


//获取查询参数
function getQueryPara(){
    var queryPara={};

    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
    var jobType=$("#jobType").val();
    var jobFlag=$("#jobFlag").val();
    var jobPriority=$("#jobPriority").val();
    var submitUser=$("#submitUser").val();
    //var executeCycle=$("#executeCycle").val();


    jobId=jobId=='all'?'':jobId;
    jobName=jobName=='all'?'':jobName;
    jobType=jobType=='all'?'':jobType;
    jobFlag=jobFlag=='all'?'':jobFlag;
    jobPriority=jobPriority=='all'?'':jobPriority;
    submitUser=submitUser=='all'?'':submitUser;
    //executeCycle=executeCycle=='all'?'':executeCycle;

    queryPara["jobId"]=jobId;
    queryPara["jobName"]=jobName;
    queryPara["jobType"]=jobType;
    queryPara["jobFlag"]=jobFlag;
    queryPara["priority"]=jobPriority;
    queryPara["submitUser"]=submitUser;
    //queryPara["executeCycle"]=executeCycle;

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
        paginationLastText:'末页',
        showExport:true,
        exportTypes:['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType:'all'
    });
}




function operateFormatter(value, row, index) {
    //console.log(row);
    var jobId=row["jobId"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="/jarvis/job/addOrEdit?jobId='+jobId+'" title="编辑任务信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  ',
        '<a class="edit" href="/jarvis/job/dependency?jobId='+jobId+'" title="查看任务依赖" target="_blank">',
        '<i class="glyphicon glyphicon-eye-open"></i>',
        '</a>  '
    ].join('');

    //console.log(result);

    return result;
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
    field: 'createTimeStr',
    title: '创建时间',
    switchable:true
},{
    field: 'updateTimeStr',
    title: '更新时间',
    switchable:true,
    visible:false
},{
    field: 'activeStartDateStr',
    title: '开始日期',
    switchable:true
},{
    field: 'activeEndDateStr',
    title: '结束日期',
    switchable:true
}, {
    field: 'workerGroupId',
    title: 'worker组ID',
    switchable:true,
    visible:false
}, {
    field: 'rejectAttempts',
    title: '被Worker拒绝时重试次数',
    switchable:true,
    visible:false
}, {
    field: 'rejectInterval',
    title: '被Worker拒绝时重试间隔(秒)',
    switchable:true,
    visible:false
}, {
    field: 'failedAttempts',
    title: '运行失败时重试次数',
    switchable:true,
    visible:false
}, {
    field: 'failedInterval',
    title: '运行失败时重试间隔(秒)',
    switchable:true,
    visible:false
}, {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateFormatter
}];


