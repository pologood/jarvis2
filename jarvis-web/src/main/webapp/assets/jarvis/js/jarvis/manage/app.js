var appStatusJson=null;
var appTypeJson=null;
$(function(){
    $.ajaxSettings.async = false;
    $.getJSON("/assets/jarvis/json/appStatus.json",function(data){
        appStatusJson=data;
    });

    $.getJSON("/assets/jarvis/json/appType.json",function(data){
        appTypeJson=data;
    });
    $.ajaxSettings.async = true;

    initData();

    $(".input-group select").select2({width:'100%'});
});



//获取查询参数
function getQueryPara(){
    var queryPara={};

    var appName=$("#appName").val();
    var status=$("#status").val();

    appName=appName=='all'?'':appName;
    status=status=='all'?'':status;

    queryPara["appName"]=appName;
    queryPara["status"]=status;

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
        url:'/jarvis/api/app/getApps',
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

function modifyAppStatus(){

}


function operateFormatter(value, row, index) {
    var appStatus=[{"id":"0","text":"停用"},{"id":"1","text":"启用"}];
    var appId=row["appId"];
    var status=row["status"];
    var result= [
        '<a class="edit" href="/jarvis/manage/appAddOrEdit?appId='+appId+'" title="编辑应用信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '
    ].join('');

    var operation='<div class="btn-group"> <button type="button" class="btn btn-xs btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">修改状态 <span class="caret"></span> </button>';
    operation=operation+'<ul class="dropdown-menu">';
    $(appStatus).each(function(i,c){
        if(c["id"]!='all'&&c["id"]!=status){
            var li='<li><a href="javascript:void(0)" onclick="modifyAppStatus('+appId+'\','+c["id"]+')" >'+c["text"]+'</a></li>';
            operation=operation+li;
        }
    });
    operation=operation+'</ul></div>';

    //console.log(result);

    return result+operation;
}



var columns=[{
    field: 'appId',
    title: '应用id',
    switchable:true
}, {
    field: 'appName',
    title: '应用名称',
    switchable:true
}, {
    field: 'appKey',
    title: 'appkey',
    switchable:true
}, {
    field: 'status',
    title: '应用状态',
    switchable:true,
    formatter:appStatusFormatter
}, {
    field: 'createTime',
    title: '创建时间',
    switchable:true,
    formatter:formatDate
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable:true,
    formatter:formatDate
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateFormatter
}];
function appStatusFormatter(value,row,index){
    return formatStatus(appStatusJson,value);
}

function search(){
    $("#content").bootstrapTable('destroy','');
    initData();
}

function reset(){
    $("#appName").val("all").trigger("change");
    $("#status").val("all").trigger("change");
}