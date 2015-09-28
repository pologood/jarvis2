$(function(){


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
    var appId=row["appId"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="/jarvis/manage/appAddOrEdit?appId='+appId+'" title="编辑应用信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '
    ].join('');

    //console.log(result);

    return result;
}



var columns=[{
    field: 'appId',
    title: '应用id',
    switchable:true
}, {
    field: 'appName',
    title: '应用名称',
    switchable:true,
    visible:false
}, {
    field: 'appKey',
    title: 'appkey',
    switchable:true
}, {
    field: 'statusStr',
    title: '应用状态',
    switchable:true
}, {
    field: 'createTimeStr',
    title: '创建时间',
    switchable:true
}, {
    field: 'updateTimeStr',
    title: '更新时间',
    switchable:true
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateFormatter
}];


function search(){
    $("#content").bootstrapTable('destroy','');
    initData();
}

function reset(){
    $("#appName").val("all").trigger("change");
    $("#status").val("all").trigger("change");
}