$(function(){
    $('#taskDate').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });
    $('#dataDate').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });
    $('#executeDate').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    //初始化执行周期内容
    $.getJSON("/assets/jarvis/json/executeCycle.json",function(data){
        $("#executeCycle").select2({
            data:data,
            width:'100%'
        });
    });
    //初始化作业类型内容
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        $("#jobType").select2({
            data:data,
            width:'100%'
        });
    });

    //初始化作业来源内容
    $.getJSON("/assets/jarvis/json/jobSource.json",function(data){
        $("#jobSource").select2({
            data:data,
            width:'100%'
        });
    });

    //初始化任务类型内容
    $.getJSON("/assets/jarvis/json/taskStatus.json",function(data){
        $(data).each(function(index,content){
            var value=content.id;
            var text=content.text;
            var input =$("<input type='checkbox' name='taskStatus'/>");
            $(input).attr("value",value);

            if(value=='all'){
                $(input).click(function(){
                    if(this.checked){
                        $($("#taskStatus input")).each(function(){
                            this.checked=true;
                        });
                    }
                    else{
                        $($("#taskStatus input")).each(function(){
                            this.checked=false;
                        });
                    }
                });
            }

            $("#taskStatus").append(input);
            $("#taskStatus").append(text);
            $("#taskStatus").append('  ');
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
    $("#taskDate").val("");
    $("#dataDate").val("");
    $("#executeDate").val("");
    $("#executeCycle").val("all").trigger("change");
    $("#jobId").val("all").trigger("change");
    $("#jobName").val("all").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#jobSource").val("all").trigger("change");
    $("#taskStatus input").each(function(i,c){
        this.checked=false;
    });
    $("#submitUser").val("all").trigger("change");
}


//获取查询参数
function getQueryPara(){
    var queryPara={};

    var taskDate=$("#taskDate").val();
    var dataDate=$("#dataDate").val();
    var executeDate=$("#executeDate").val();
    var executeCycle=$("#executeCycle").val();
    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
    var jobType=$("#jobType").val();
    var jobSource=$("#jobSource").val();

    var taskStatus=new Array();
    var inputs=$("#taskStatus").find("input:checked");
    $(inputs).each(function(i,c){
        var value=$(c).val();
        taskStatus.push(value);
    });

    var submitUser=$("#submitUser").val();

    queryPara["taskDate"]=taskDate;
    queryPara["dataDate"]=dataDate;
    queryPara["executeDate"]=executeDate;
    queryPara["executeCycle"]=executeCycle;
    queryPara["jobId"]=jobId;
    queryPara["jobName"]=jobName;
    queryPara["jobType"]=jobType;
    queryPara["jobSource"]=jobSource;
    queryPara["taskStatus"]=taskStatus;
    queryPara["submitUser"]=submitUser;

    return queryPara;
}

//初始化数据及分页
function initData(){
    var queryPara=getQueryPara();
    $("#content").bootstrapTable({
        columns:columns,
        data:data,
        pagination:true,
        sidePagination:'client',
        search:true,
        searchText:'',
        showColumns:true,
        showHeader:true,
        showToggle:true,
        paginationFirstText:'首页',
        paginationPreText:'上一页',
        paginationNextText:'下一页',
        paginationLastText:'末页'
    });
}








var columns=[{
    field: 'id',
    title: 'Item ID',

    switchable:true
}, {
    field: 'name',
    title: 'Item Name',

    switchable:true
}, {
    field: 'price',
    title: 'Item Price',

    switchable:true
}];

var data=[
    {
        "id": 0,
        "name": "Item 0",
        "price": "$0"
    },
    {
        "id": 1,
        "name": "Item 1",
        "price": "$1"
    },
    {
        "id": 2,
        "name": "Item 2",
        "price": "$2"
    },
    {
        "id": 3,
        "name": "Item 3",
        "price": "$3"
    },
    {
        "id": 4,
        "name": "Item 4",
        "price": "$4"
    },
    {
        "id": 5,
        "name": "Item 5",
        "price": "$5"
    },
    {
        "id": 6,
        "name": "Item 6",
        "price": "$6"
    },
    {
        "id": 7,
        "name": "Item 7",
        "price": "$7"
    },
    {
        "id": 8,
        "name": "Item 8",
        "price": "$8"
    },
    {
        "id": 9,
        "name": "Item 9",
        "price": "$9"
    },
    {
        "id": 10,
        "name": "Item 10",
        "price": "$10"
    },
    {
        "id": 11,
        "name": "Item 11",
        "price": "$11"
    },
    {
        "id": 12,
        "name": "Item 12",
        "price": "$12"
    },
    {
        "id": 13,
        "name": "Item 13",
        "price": "$13"
    },
    {
        "id": 14,
        "name": "Item 14",
        "price": "$14"
    },
    {
        "id": 15,
        "name": "Item 15",
        "price": "$15"
    },
    {
        "id": 16,
        "name": "Item 16",
        "price": "$16"
    },
    {
        "id": 17,
        "name": "Item 17",
        "price": "$17"
    },
    {
        "id": 18,
        "name": "Item 18",
        "price": "$18"
    },
    {
        "id": 19,
        "name": "Item 19",
        "price": "$19"
    },
    {
        "id": 20,
        "name": "Item 20",
        "price": "$20"
    }
];




