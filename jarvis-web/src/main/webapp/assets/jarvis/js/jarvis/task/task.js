$(function(){
    createDatetimePickerById("executeDate");
    createDatetimePickerById("dataDate");
    createDatetimePickerById("executeStartTime");
    createDatetimePickerById("executeEndTime");


    //初始化作业类型内容
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        $("#jobType").select2({
            data:data,
            width:'100%'
        });
    });

    //初始化任务状态
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
    $("#content").bootstrapTable("destroy");
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

    var executeDate=$("#executeDate").val();
    var dataDate=$("#dataDate").val();
    var executeStartTime=$("#executeStartTime").val();
    var executeEndTime=$("#executeEndTime").val();
    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
    var jobType=$("#jobType").val();
    var submitUser=$("#submitUser").val();

    var taskStatus=new Array();
    var inputs=$("#taskStatus").find("input:checked");
    $(inputs).each(function(i,c){
        var value=$(c).val();
        if(value!='all'&&value!=''){
            taskStatus.push(value);
        }
    });

    jobId=jobId=="all"?'':jobId;
    jobName=jobName=='all'?'':jobName;
    jobType=jobType=='all'?'':jobType;
    submitUser=submitUser=="all"?'':submitUser;

    queryPara["executeDate"]=executeDate;
    queryPara["dataDate"]=dataDate;
    queryPara["startTime"]=executeStartTime;
    queryPara["endTime"]=executeEndTime;
    queryPara["jobId"]=jobId;
    queryPara["jobName"]=jobName;
    queryPara["jobType"]=jobType;
    queryPara["submitUser"]=submitUser;
    queryPara["taskStatusArrStr"]=JSON.stringify(taskStatus);


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
        url:'/jarvis/api/task/getTasks',
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
    field: 'taskId',
    title: '执行ID',
    switchable:true
}, {
    field: 'attemptId',
    title: '最后尝试ID',
    switchable:true,
    visible:false
}, {
    field: 'jobId',
    title: '任务ID',
    switchable:true,
    visible:false
}, {
    field: 'jobContent',
    title: '任务内容',
    switchable:true
}, {
    field: 'jobParams',
    title: '任务参数',
    switchable:true,
    visible:false
}, {
    field: 'dataYmd',
    title: '数据日期',
    switchable:true
}, {
    field: 'status',
    title: '执行状态',
    switchable:true
}, {
    field: 'executeUser',
    title: '执行用户',
    switchable:true
}, {
    field: 'executeStartTime',
    title: '开始执行时间',
    switchable:true
}, {
    field: 'executeEndTime',
    title: '执行结束时间',
    switchable:true
}];





