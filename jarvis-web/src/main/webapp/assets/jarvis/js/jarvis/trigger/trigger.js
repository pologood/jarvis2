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
        $("#reRunNext").jstree('destroy');
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
            $("#reRunNext").jstree({
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
    $("#reRunNext").removeAttr("checked");
}


function submit(){
    var originJobId=$("#originJobId").val();
    var startTime=$("#startTime").val();
    var endTime=$("#endTime").val();
    if(jobId==null||jobId==''){
        new PNotify({
            title: '重跑任务',
            text: "必须选择Job",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        return ;
    }
    if(startTime==null||startTime==''||endTime==null||endTime==''){
        new PNotify({
            title: '重跑任务',
            text: "开始日期与结束日期必须选择",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        return ;
    }

    var reRunJobs=$("#reRunNext").jstree().get_checked();

    $.ajax({
        url:'',
        type:'POST',
        data:{originJobId:originJobId,startTime:startTime,endTime:endTime,reRunJobs:JSON.stringify(reRunJobs)},
        success:function(data){

        }
    });
}