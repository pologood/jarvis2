var testNum=/^[0-9]*$/;


$(function(){

    //初始化作业类型内容
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        var new_data=new Array();
        $(data).each(function(i,c){
            if(this.id!='all'){
                new_data.push(this);
            }
        });

        $("#job_type").select2({
            data:new_data,
            width:'100%'
        });
        if(jobType!=undefined){
            $("#job_type").val(jobType).trigger("change");
        }
    });



    $.getJSON("/assets/jarvis/json/jobPriority.json",function(data){
        var new_data=new Array();
        $(data).each(function(i,c){
            if(this.id!='all'){
                new_data.push(this);
            }
        });

        $("#priority").select2({
            data:new_data,
            width:'100%'
        });
        if(jobPriority!=undefined){
            $("#priority").val(jobPriority).trigger("change");
        }
    });


    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});

    if(dependIds!=undefined&&dependIds!='[]'){
        $("#dependency_jobids").val(JSON.parse(dependIds)).trigger("change");
    }

    $('#start_time').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    $('#end_time').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });
});


function changeTextArea(thisTag,rows,cols){
    $(thisTag).prop("rows",rows);
    $(thisTag).prop("cols",cols);
}

//重置参数
function reset(){
    var inputs=$("#jobData .input-group>input,#jobData .input-group>textarea");
    var selects=$("#jobData .input-group>select");
    $(inputs).each(function(i,c){
        $(c).val("");
    });
    $(selects).each(function(i,c){
        if($(c).find("option").length>=1){
            var value =$($(c).find("option")[0]).val();
            $(c).val(value).trigger("change");
        }
    });

}


//校验某些属性是否为空
function checkEmpty(ids){
    var flag=true;
    $(ids).each(function(i,c){
        var value=$("#"+c).val();
        if(value==undefined||value==''){
            flag=false;
            var desc=$("#"+c).attr("desc");
            new PNotify({
                title: '提交任务',
                text: desc+'不能为空',
                type: 'warning',
                icon: true,
                styling: 'bootstrap3'
            });

            return false;
        }
    });
    return flag;
}

//获取参数
function getData(){
    var result={};

    var inputs=$("#jobData .input-group>input,#jobData .input-group>textarea");
    var selects=$("#jobData .input-group>select");
    $(inputs).each(function(i,c){
        var id=$(c).prop("id");
        var value=$(c).val();
        result[id]=value
    });
    $(selects).each(function(i,c){
        var id=$(c).prop("id");
        var value=$(c).val();
        if($(c).attr("multiple")!=null&&$(c).attr("multiple")!=''){
            result[id]=JSON.stringify(value);
        }
        else{
            result[id]=value;
        }

    });

    var user=$("#user").val();
    result["user"]=user;
    var app_key=$("#app_name").find("option:selected").attr("app_key");
    result["app_key"]=app_key;

    return result;
}

//提交任务
function submit(){
    var ids=["user","app_name","job_name","job_type","command","group_id"];
    var flag=checkEmpty(ids);
    if(flag==false){
        return ;
    }

    flag=checkJobName($("#job_name"));
    if(flag==false){
        return;
    }
    flag=checkActiveDate();
    if(flag==false){
        return;
    }

    var data=getData();
    $.ajax({
        url:'/api/job/submit',
        type:'POST',
        data:data,
        success:function(data){

        }
    });

}

//编辑任务
function edit(){
    var ids=["user","job_id","app_name","job_name","job_type","command","group_id"];
    var flag=checkEmpty(ids);
    if(flag==false){
        return ;
    }

    flag=checkJobName($("#job_name"));
    if(flag==false){
        return;
    }
    flag=checkActiveDate();
    if(flag==false){
        return;
    }

    var data=getData();

    var job_id=$("#job_id").val();
    data["job_id"]=job_id;

    $.ajax({
        url:'/api/job/edit',
        type:'POST',
        data:data,
        success:function(data){

        }
    });
}

//检查任务名是否重复
function checkJobName(thisTag){
    var jobId=$("#job_id").val();
    var jobName=$("#job_name").val();
    var flag=true;
    if(jobName==''){
        new PNotify({
            title: '提交任务',
            text: '任务名称不能为空,请先填写',
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        $(thisTag).focus();
        return;
    }


    $.ajax({
        url:'/jarvis/job/checkJobName',
        type:'POST',
        async:false,
        data:{jobId:jobId,jobName:jobName},
        success:function(data){
            if(data.code==1){
                new PNotify({
                    title: '提交任务',
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
                flag=false;
                $(thisTag).focus();
            }
        }
    });

    return flag;
}
//检查是否数字
function checkNum(thisTag){
    if($(thisTag).val()==''){
        return;
    }

    var flag=testNum.test($(thisTag).val());
    if(flag==false){
        new PNotify({
            title: '提交任务',
            text: $(thisTag).attr("desc")+"必须为数字,请修改！！！",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        $(thisTag).focus();
    }
}
//检查结束日期是否小于开始日期
function checkActiveDate(){
    var start_time=$("#start_time").val();
    var end_time=$("#end_time").val();

    var flag=true;
    if(start_time!=''&&end_time!=''){
        var start=new Date(start_time);
        var end=new Date(end_time);
        if(end<=start){
            new PNotify({
                title: '提交任务',
                text: "结束日期不能小于等于开始日期",
                type: 'warning',
                icon: true,
                styling: 'bootstrap3'
            });
            flag = false;
        }
    }
    return flag;
}
