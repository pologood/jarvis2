$(function(){
    $('#startTime').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    $('#endTime').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});
    $("#originJobId").on("change", function (e) {
        $("#reRunJobs").jstree('destroy');
        var id=$(e.target).val();
        if(id!=null&&id!=''){
            buildTree(id);
        }
    });


});

function buildTree(jobId){

    $.ajax({
        url:'/jarvis/api/job/getTreeDependedONJob',
        data:{jobId:jobId},
        success:function(data){
            $("#reRunJobs").jstree({
                'core':{
                    data:data
                },
                "types": {
                    "default": {"icon": "fa fa-users icon-green", "valid_children": []}
                },
                plugins : [
                    'checkbox','types'
                ]
            });
        }
    });


}

function reset(){
    $("#content").val(null).trigger("change");
    $("#jobStart").val('');
    $("#jobEnd").val('');
    $("#reRunJobs").removeAttr("checked");
}


function submit(){
    var originJobId=$("#originJobId").val();
    var appName=$("#originJobId option:selected").attr("appName");
    var appKey=$("#originJobId option:selected").attr("appKey");
    var startTime=$("#startTime").val();
    var endTime=$("#endTime").val();
    if(originJobId==null||originJobId==''){
        new PNotify({
            title: '重跑任务',
            text: "必须选择Job",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        return ;
    }

    if(startTime!=''&&endTime!=''&&((new Date(startTime))>(new Date(endTime)))){
        new PNotify({
            title: '重跑任务',
            text: "开始日期必须小于结束日期",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        return ;
    }
    
    var reRunJobs=$("#reRunJobs").jstree().get_checked();

    if(reRunJobs.length<=0){
        reRunJobs.push(originJobId);
    }
    else{
        var flag = false;
        for(var i=0;i<reRunJobs.length;i++){
            if(reRunJobs[i]==originJobId){
                flag=true;
                break;
            }
        }
        //重跑的任务里没有选择的原始任务，则加进去统一处理
        if(!flag){
            reRunJobs.push(originJobId);
        }
    }
    //console.log(reRunJobs);
    var data={originJobId:originJobId,appName:appName,appKey:appKey,startTime:startTime,endTime:endTime,reRunJobs:JSON.stringify(reRunJobs)};
    requestRemoteRestApi("/api/job/rerun","重跑任务",data);

}