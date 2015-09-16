$(function(){

    $('#taskDate').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    $('#taskDate').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    //初始化作业类型内容
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        var new_data=new Array();
        $(data).each(function(i,c){
            if(this.id!='all'){
                new_data.push(this);
            }
        });

        $("#jobType").select2({
            data:new_data,
            width:'100%'
        });
    });


    //初始化作业状态
    $.getJSON("/assets/jarvis/json/jobFlag.json",function(data){
        $("#jobFlag").select2({
            data:data,
            width:'100%'
        });
    });

    $.getJSON("/assets/jarvis/json/jobPriority.json",function(data){
        var new_data=new Array();
        $(data).each(function(i,c){
            if(this.id!='all'){
                new_data.push(this);
            }
        });

        $("#jobPriority").select2({
            data:new_data,
            width:'100%'
        });
    });


    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});
});


//重置参数
function reset(){
    $("#jobName").val("");
    $("#content").val("").trigger("change");
    $("#crontab").val("");
    $("#jobType").val("hive_script").trigger("change");
    $("#jobFlag").val("1").trigger("change");
    $("#jobPriority").val("4").trigger("change");
    $("#jobDependency").val(null).trigger("change");

}