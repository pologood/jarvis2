$(function(){


    initWorkerData();
    initWorkerGroupData();

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
        url:'/jarvis/api/worker/getWorkers',
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

//初始化数据及分页
function initWorkerGroupData(){
    var queryParams=getWorkerGroupQueryPara();
    $("#workerGroupContent").bootstrapTable({
        columns:workerGroupColumns,
        pagination:true,
        sidePagination:'server',
        search:false,
        url:'/jarvis/api/worker/getWorkerGroups',
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


function operateWorkerFormatter(value, row, index) {
    //console.log(row);
    var appId=row["id"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="/jarvis/manage/workerAddOrEdit?id='+appId+'" title="编辑Worker信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '
    ].join('');

    //console.log(result);

    return result;
}
function operateWorkerGroupFormatter(value, row, index) {
    //console.log(row);
    var appId=row["id"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="/jarvis/manage/workerGroupAddOrEdit?id='+appId+'" title="编辑WorkerGroup信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '
    ].join('');

    //console.log(result);

    return result;
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
    switchable:true
}, {
    field: 'createTime',
    title: '创建时间',
    switchable:true
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable:true
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
    field: 'name',
    title: '名称',
    switchable:true
}, {
    field: 'key',
    title: 'key',
    switchable:true
}, {
    field: 'createTime',
    title: '创建时间',
    switchable:true
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable:true
}, {
    field: 'creator',
    title: '创建者',
    switchable:true
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateWorkerGroupFormatter
}];


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

