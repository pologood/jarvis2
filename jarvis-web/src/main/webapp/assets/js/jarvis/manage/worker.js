var workerStatus = null;
var workerGroupStatus = null;

$(function () {
    initWorkerData();
    initWorkerGroupData();

    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/api/worker/getWorkerStatus", function (data) {
        workerStatus = data;
    });

    $.getJSON(contextPath + "/api/workerGroup/getWorkerGroupStatus", function (data) {
        workerGroupStatus = data;
    });
    $.ajaxSettings.async = true;

    $(".input-group select").select2({width: '100%'});
});


//获取查询参数
function getWorkerQueryPara() {
    var queryPara = {};

    var workerGroupId = $("#workerGroupId").val();
    var ip = $("#ip").val();
    var port = $("#port").val();
    var workerStatus = $("#workerStatus").val();

    workerGroupId = workerGroupId == 'all' ? '' : workerGroupId;
    ip = ip == 'all' ? '' : ip;
    port = port == 'all' ? '' : port;
    workerStatus = workerStatus == 'all' ? '' : workerStatus;

    queryPara["workerGroupId"] = workerGroupId;
    queryPara["ip"] = ip;
    queryPara["port"] = port;
    queryPara["workerStatus"] = workerStatus;

    return queryPara;
}

//获取查询参数
function getWorkerGroupQueryPara() {
    var queryPara = {};

    var name = $("#name").val();

    name = name == 'all' ? '' : name;

    queryPara["name"] = name;
    return queryPara;
}

//初始化数据及分页
function initWorkerData() {
    var queryParams = getWorkerQueryPara();
    $("#workerContent").bootstrapTable({
        columns: workerColumns,
        pagination: true,
        sidePagination: 'server',
        search: false,
        url: contextPath + '/api/worker/getWorkers',
        queryParams: function (params) {
            for (var key in queryParams) {
                var value = queryParams[key];
                params[key] = value;
            }
            return params;
        },
        showColumns: true,
        showHeader: true,
        showToggle: true,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'all'
    });
}

//初始化数据及分页
function initWorkerGroupData() {
    var queryParams = getWorkerGroupQueryPara();
    $("#workerGroupContent").bootstrapTable({
        columns: workerGroupColumns,
        pagination: true,
        sidePagination: 'server',
        search: false,
        url: contextPath + '/api/workerGroup/getWorkerGroups',
        queryParams: function (params) {
            for (var key in queryParams) {
                var value = queryParams[key];
                params[key] = value;
            }
            return params;
        },
        showColumns: true,
        showHeader: true,
        showToggle: true,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'all'
    });
}
//worker操作状态
function operateWorkerFormatter(value, row, index) {
    var id = row["id"];
    var ip = row["ip"];
    var port = row["port"];
    var operateFlag = row["status"];

    var operation = "";

    $(workerStatus).each(function (i, c) {
        if (c["id"] != 'all' && c["id"] != operateFlag) {
            var style="";
            if(2==c["id"]){
                style="btn btn-xs btn-danger";
            }
            else if(1==c["id"]){
                style="btn btn-xs btn-success";
            }
            var item = '<a class="'+style+'" href="javascript:void(0)" onclick="modifyWorkerStatus(' + id + ',' + c["id"] + ',\'' + ip + '\',' + port + ')" >' + c["text"] + '</a>';
            operation = operation + item;
        }
    });

    //console.log(result);

    return operation;
}
//worker group操作状态
function operateWorkerGroupFormatter(value, row, index) {
    //console.log(row);
    var id = row["id"];
    var operateFlag = row["status"];
    var authKey = row["authKey"];
    //console.log(jobId);
    var result = [
        '<a class="edit" href="' + contextPath + '/manage/workerGroupAddOrEdit?id=' + id + '" title="编辑WorkerGroup信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '

    ].join('');

    var operation = '';

    $(workerGroupStatus).each(function (i, c) {
        if (c["id"] != 'all' && c["id"] != operateFlag) {
            var style="";
            //禁用
            if(2==c["id"]){
                style="btn btn-xs btn-danger";
            }
            //启用
            else if(1==c["id"]){
                style="btn btn-xs btn-success";
            }

            var item = '<a class="'+style+'" href="javascript:void(0)" onclick="modifyWorkerGroupStatus(' + id + ',\'' + authKey + '\',' + c["id"] + ')" >' + c["text"] + '</a>';
            operation = operation + item;
        }
    });

    //console.log(result);

    return result + operation;
}


var workerColumns = [{
    field: 'id',
    title: 'Worker Id',
    switchable: true,
    visible:false
}, {
    field: 'workerGroupId',
    title: 'Worker Group Id',
    switchable: true,
    visible:false
}, {
    field: 'ip',
    title: 'IP',
    switchable: true
}, {
    field: 'port',
    title: '端口',
    switchable: true
},{
    field: 'workerGroupName',
    title: 'WorkerGroup名',
    switchable: true
}, {
    field: 'status',
    title: '状态',
    switchable: true,
    formatter: workerStatusFormatter
}, {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    formatter: formatDateTime,
    visible:false
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable: true,
    formatter: formatDateTime,
    visible:false
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateWorkerFormatter
}];


var workerGroupColumns = [{
    field: 'id',
    title: 'Worker Group id',
    switchable: true,
    visible:false
}, {
    field: 'name',
    title: '名称',
    switchable: true
}, {
    field: 'authKey',
    title: 'authKey',
    switchable: true
},{
    field: 'status',
    title: '状态',
    switchable: true,
    formatter: workerGroupStatusFormatter
},  {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    formatter: formatDateTime,
    visible:false
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable: true,
    formatter: formatDateTime,
    visible:false
}, {
    field: 'updateUser',
    title: '更新人',
    switchable: true,
    visible:false
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateWorkerGroupFormatter
}];

function workerStatusFormatter(value, row, index) {
    var result="";
    //下线
    if(2==value){
        result="<i class='glyphicon glyphicon-remove text-danger'></i>";
    }
    //上线
    else if(1==value){
        result="<i class='glyphicon glyphicon-ok text-success'></i>";
    }

    return result;
}
function workerGroupStatusFormatter(value, row, index) {
    var result="";
    //禁用
    if(2==value){
        result="<i class='glyphicon glyphicon-remove text-danger'></i>";
    }
    //启用
    else if(1==value){
        result="<i class='glyphicon glyphicon-ok text-success'></i>";
    }
    else{
        result="<i class='glyphicon glyphicon-question-sign text-info'></i>";
    }

    return result;
}

function searchWorker() {
    $("#workerContent").bootstrapTable('destroy', '');
    initWorkerData();
}

function resetWorker() {
    $("#workerGroupId").val("all").trigger("change");
    $("#ip").val("all").trigger("change");
    $("#port").val("all").trigger("change");
    $("#workerStatus").val("all").trigger("change");
}

function searchWorkerGroup() {
    $("#workerGroupContent").bootstrapTable('destroy', '');
    initWorkerGroupData();
}

function resetWorkerGroup() {
    $("#name").val("all").trigger("change");
    $("#creator").val("all").trigger("change");
}


//修改worker
function modifyWorkerStatus(workerId, status, ip, port) {
    var data = {workerId: workerId, status: status, ip: ip, port: port};
    requestRemoteRestApi("/api/worker/status/set", "修改Worker Group状态", data);
    $("#workerContent").bootstrapTable("destroy");
    initWorkerData();

}

//修改worker group状态
function modifyWorkerGroupStatus(workerGroupId, authKey, status) {
    var data = {workerGroupId: workerGroupId, status: status};
    requestRemoteRestApi("/api/workerGroup/status/set", "修改Worker Group状态", data);
    $("#workerGroupContent").bootstrapTable("destroy");
    initWorkerGroupData();
}

