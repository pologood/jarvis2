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
    $("#originJobId").select2({
        ajax: {
            url: contextPath+"/api/job/getJobBySimilarNames",
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term, // search term
                    page: params.page
                };
            },
            processResults: function (data, page) {
                return {
                    results: data.items
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) { return markup; },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,

        width:'100%'
    });


    $("#originJobId").on("change", function (e) {
        $("#reRunJobs div[name=children]").jstree('destroy');
        $("#reRunJobs").empty();
        var jobIds=$(e.target).val();
        if(jobIds!=null&&jobIds!=''){
            buildTree(jobIds);
        }
    });


});

function buildTree(jobIds){

    $(jobIds).each(function(i,c){
        var jobId=c;
        var id="jobId"+jobId
        var childrenTree=$('<div name="children" id="'+id+'"></div><hr/>');
        $("#reRunJobs").append(childrenTree);


        $.ajax({
            url:contextPath+'/api/job/getTreeDependedONJob',
            data:{jobId:jobId},
            success:function(data){
                $("#"+id).jstree({
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
    });

}

function reset(){
    $("#originJobId").val({}).trigger("change");
    $("#startTime").val('');
    $("#endTime").val('');
    $("input[name=runChild]").removeAttr("checked");
    $("input[name=runChild][value=false]").click();
    $("#reRunJobs").removeAttr("checked");
}


function submit(){
    var originJobId=$("#originJobId").val();
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

    if(''==startTime||''==endTime){
        new PNotify({
            title: '重跑任务',
            text: "开始日期与结束日期必须填写",
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

    if(new Date(endTime)<=(new Date())){
        new PNotify({
            title: '重跑任务',
            text: "结束日期必须大于今天",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        return ;
    }


    var jobIdCache={};
    var reRunJobs=new Array();
    $(originJobId).each(function(i,c){
        reRunJobs.push(parseInt(c));
        jobIdCache[c]=c;
    });

    var divTrees=$("#reRunJobs>div");
    $(divTrees).each(function(i,c){
        var treeJobIds=$(c).jstree().get_checked();
        $(treeJobIds).each(function(i,c){
            if(jobIdCache[c]==null){
                reRunJobs.push(parseInt(c));
                jobIdCache[c]=c;
            }
        });
    });

    //
    var runChild=$("input[name=runChild]:checked").val();
    if(runChild='true'){
        runChild=true;
    }
    else{
        runChild=false;
    }

    var startDate=(new Date(startTime)).getTime();
    var endDate=(new Date(endTime)).getTime();
    var data={runChild:runChild,startDate:startDate,endDate:endDate,jobIdList:reRunJobs};
    requestRemoteRestApi("/api/task/rerun","重跑任务",data);

}



function formatResult(result){
    return result.text;
}
function formatResultSelection(result){
    return result.text;
}
