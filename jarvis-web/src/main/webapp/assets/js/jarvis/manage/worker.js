var workerStatusJson=null;
var workerGroupStatusJson=null;

$(function(){
    initWorkerData();
    initWorkerGroupData();

    $.ajaxSettings.async = false;
    $.getJSON(contextPath+"/assets/json/workerStatus.json",function(data){
        workerStatusJson=data;
    });

    $.getJSON(contextPath+"/assets/json/workerGroupStatus.json",function(data){
        workerGroupStatusJson=data;
    });
    $.ajaxSettings.async = true;

    $(".input-group select").select2({width:'100%'});
});



//获取查询参数
function getWorkerQueryPara(){
    var queryPara={};

    var workerGroupId=$("#workerGroupId").val();
    var ip=$("#ip").val();
    var port=$("#port").val();
    var workerStatus=$("#workerStatus").val();

    workerGroupId=workerGroupId=='all'?'':workerGroupId;
    ip=ip=='all'?'':ip;
    port=port=='all'?'':port;
    workerStatus=workerStatus=='all'?'':workerStatus;

    queryPara["workerGroupId"]=workerGroupId;
    queryPara["ip"]=ip;
    queryPara["port"]=port;
    queryPara["workerStatus"]=workerStatus;

    return queryPara;
}

//获取查询参数
function getWorkerGroupQueryPara(){
    var queryPara={};

    var name=$("#name").val();
    var creator=$("#creator").val();

    name=name=='all'?'':name;
    creator=creator=='all'?'':creator;

    queryPara["name"]=name;
    queryPara["creator"]=creator;

    return queryPara;
}

//初始化数据及分页
function initWorkerData(){
    var queryParams=getWorkerQueryPara();
    $("#workerContent").bootstrapTable({
        columns:workerColumns,
        pagination:true,
        sidePagination:'server',
        search:false,
        url:contextPath+'/api/worker/getWorkers',
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
        pageList:[5,10,20,50,100,200,500],
        paginationFirstText:'首页',
        paginationPreText:'上一页',
        paginationNextText:'下一页',
        paginationLastText:'末页',
        showExport:true,
        exportTypes:['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType:'all'
    });
}

//初始化数据及分页
function initWorkerGroupData(){
    var queryParams=getWorkerGroupQueryPara();
    $("#workerGroupContent").bootstrapTable({
        columns:workerGroupColumns,
        pagination:true,
        sidePagination:'server',
        search:false,
        url:contextPath+'/api/worker/getWorkerGroups',
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
        pageList:[5,10,20,50,100,200,500],
        paginationFirstText:'首页',
        paginationPreText:'上一页',
        paginationNextText:'下一页',
        paginationLastText:'末页',
        showExport:true,
        exportTypes:['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType:'all'
    });
}
//worker操作状态
function operateWorkerFormatter(value, row, index) {
    //console.log(row);
    var workerStatus=[{"id":"0","text":"下线"},{"id":"1","text":"上线"}];
    var id=row["id"];
    var ip=row["ip"];
    var port=row["port"];
    var operateFlag=row["status"];
    //console.log(jobId);
    var result= [
        ''
    ].join('');

    var operation='<div class="btn-group"> <button type="button" class="btn btn-xs btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">修改状态 <span class="caret"></span> </button>';
    operation=operation+'<ul class="dropdown-menu">';
    $(workerStatus).each(function(i,c){
        if(c["id"]!='all'&&c["id"]!=operateFlag){
            var li='<li><a href="javascript:void(0)" onclick="modifyWorkerStatus('+id+','+c["id"]+',\''+ip+'\','+port+')" >'+c["text"]+'</a></li>';
            operation=operation+li;
        }
    });
    operation=operation+'</ul></div>';

    //console.log(result);

    return result+operation;
}
//worker group操作状态
function operateWorkerGroupFormatter(value, row, index) {
    var workerGroupStatus=[{"id":"0","text":"无效"},{"id":"1","text":"有效"}];
    //console.log(row);
    var id=row["id"];
    var operateFlag=row["status"];
    var authKey=row["authKey"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="'+contextPath+'/manage/workerGroupAddOrEdit?id='+id+'" title="编辑WorkerGroup信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '

    ].join('');

    var operation='<div class="btn-group"> <button type="button" class="btn btn-xs btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">修改状态 <span class="caret"></span> </button>';
    operation=operation+'<ul class="dropdown-menu">';
    $(workerGroupStatus).each(function(i,c){
        if(c["id"]!='all'&&c["id"]!=operateFlag){
            var li='<li><a href="javascript:void(0)" onclick="modifyWorkerGroupStatus('+id+',\''+authKey+'\','+c["id"]+')" >'+c["text"]+'</a></li>';
            operation=operation+li;
        }
    });
    operation=operation+'</ul></div>';

    //console.log(result);

    return result+operation;
}



var workerColumns=[{
    field: 'id',
    title: 'Worker Id',
    switchable:true
}, {
    field: 'workerGroupId',
    title: 'Worker Group Id',
    switchable:true
}, {
    field: 'ip',
    title: 'IP',
    switchable:true
}, {
    field: 'port',
    title: 'PORT',
    switchable:true
}, {
    field: 'status',
    title: '状态',
    switchable:true,
    formatter:workerStatusFormatter
}, {
    field: 'createTime',
    title: '创建时间',
    switchable:true,
    formatter:formatDateTime
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable:true,
    formatter:formatDateTime
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateWorkerFormatter
}];


var workerGroupColumns=[{
    field: 'id',
    title: 'Worker Group id',
    switchable:true
}, {
    field: 'status',
    title: '状态',
    switchable:true,
    formatter:workerGroupStatusFormatter
},{
    field: 'name',
    title: '名称',
    switchable:true
}, {
    field: 'authKey',
    title: 'authKey',
    switchable:true
},  {
    field: 'createTime',
    title: '创建时间',
    switchable:true,
    formatter:formatDateTime
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable:true,
    formatter:formatDateTime
}, {
    field: 'updateUser',
    title: '更新人',
    switchable:true
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateWorkerGroupFormatter
}];

function workerStatusFormatter(value,row,index){
    return formatStatus(workerStatusJson,value);
}
function workerGroupStatusFormatter(value,row,index){
    return formatStatus(workerGroupStatusJson,value);
}

function searchWorker(){
    $("#workerContent").bootstrapTable('destroy','');
    initWorkerData();
}

function resetWorker(){
    $("#workerGroupId").val("all").trigger("change");
    $("#ip").val("all").trigger("change");
    $("#port").val("all").trigger("change");
    $("#workerStatus").val("all").trigger("change");
}

function searchWorkerGroup(){
    $("#workerGroupContent").bootstrapTable('destroy','');
    initWorkerGroupData();
}

function resetWorkerGroup(){
    $("#name").val("all").trigger("change");
    $("#creator").val("all").trigger("change");
}



//修改worker
function modifyWorkerStatus(workerId,status,ip,port){
    var data={workerId:workerId,status:status,ip:ip,port:port};
    requestRemoteRestApi("/api/worker/status","修改Worker Group状态",data);
}

//修改worker group状态
function modifyWorkerGroupStatus(workerGroupId,authKey,status){
    var data={workerGroupId:workerGroupId,authKey:authKey,status:status};
    requestRemoteRestApi("/api/workerGroup/status","修改Worker Group状态",data);
}

