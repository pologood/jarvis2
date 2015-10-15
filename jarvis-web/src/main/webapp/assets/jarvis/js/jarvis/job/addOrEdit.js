var testNum=/^[0-9]*$/;


$(function(){

    //初始化作业类型内容
    $.getJSON("/assets/jarvis/json/jobType.json",function(data){
        var newData=new Array();
        $(data).each(function(i,c){
            if(this.id!='all'){
                newData.push(this);
            }
        });

        $("#jobType").select2({
            data:newData,
            width:'100%'
        });
        if(jobType!=undefined){
            $("#jobType").val(jobType).trigger("change");
        }
    });



    $.getJSON("/assets/jarvis/json/jobPriority.json",function(data){
        var newData=new Array();
        $(data).each(function(i,c){
            if(this.id!='all'){
                newData.push(this);
            }
        });

        $("#priority").select2({
            data:newData,
            width:'100%'
        });
        if(jobPriority!=undefined){
            $("#priority").val(jobPriority).trigger("change");
        }
    });


    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});

    if(dependIds!=undefined&&dependIds!='[]'){
        $("#dependJobIds").val(JSON.parse(dependIds)).trigger("change");
    }

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
                text: desc+'不能为空:'+c,
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
    var appKey=$("#appName").find("option:selected").attr("appKey");
    result["appKey"]=appKey;

    var newDependIds =calculateOperator(dependIds,result["dependJobIds"]);
    result["dependJobIds"]=JSON.stringify(newDependIds);

    return result;
}

//根据原始依赖与新依赖确定操作类型
function calculateOperator(sourceStr,afterChangeStr){
    var myDependIds = {};
    if(sourceStr!=null){
        //console.log("sourceStr:"+sourceStr);
        var source=JSON.parse(sourceStr);
        var afterChange=JSON.parse(afterChangeStr);


        if(afterChange==null){
            for(var i=0;i<source.length;i++){
                myDependIds[source[i]]="delete";
            }
        }
        else{
            for(var i=0;i<source.length;i++){
                myDependIds[source[i]]="";
            }
            for(var i=0;i<afterChange.length;i++){
                myDependIds[afterChange[i]]="";
            }

            for(var key in myDependIds){
                var operator="no";
                var source_flag=false;
                var afterChange_flag=false;
                for(var i=0;i<source.length;i++){
                    if(source[i]==key){
                        source_flag=true;
                        break;
                    }
                }
                for(var i=0;i<afterChange.length;i++){
                    if(afterChange[i]==key){
                        afterChange_flag=true;
                        break;
                    }
                }
                if(source_flag==true&&afterChange_flag==true){
                    operator="no";
                }
                if(source_flag==true&&afterChange_flag==false){
                    operator="delete";
                }
                if(source_flag==false&&afterChange_flag==true){
                    operator="add";
                }
                //不可能
                if(source_flag==false&&afterChange_flag==false){
                    operator="no possible";
                }
                myDependIds[key]=operator;
            }
        }
    }
    else{
        var afterChange=JSON.parse(afterChangeStr);
        if(afterChange!=null){
            for(var i=0;i<afterChange.length;i++){
                myDependIds[afterChange[i]]="add";
            }
        }
    }

    //console.log("result:"+JSON.stringify(myDependIds));
    return myDependIds;
}

//提交任务
function submit(){
    var ids=["user","appName","jobName","jobType","content","groupId"];
    var flag=checkEmpty(ids);
    if(flag==false){
        return ;
    }

    flag=checkJobName($("#jobName"));
    if(flag==false){
        return;
    }
    flag=checkActiveDate();
    if(flag==false){
        return;
    }

    var data=getData();
    var resultFlag=requestRemoteRestApi("/job/submit","新增任务",data);

}

//编辑任务
function edit(){
    var ids=["user","jobId","appName","jobName","jobType","content","groupId"];
    var flag=checkEmpty(ids);
    if(flag==false){
        return ;
    }

    flag=checkJobName($("#jobName"));
    if(flag==false){
        return;
    }
    flag=checkActiveDate();
    if(flag==false){
        return;
    }

    var data=getData();

    var jobId=$("#jobId").val();
    data["jobId"]=jobId;

    var resultFlag=requestRemoteRestApi('/job/edit',"编辑任务",data);
}

//检查任务名是否重复
function checkJobName(thisTag){
    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
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
    var startTime=$("#startTime").val();
    var endTime=$("#endTime").val();

    var flag=true;
    if(startTime!=''&&endTime!=''){
        var start=new Date(startTime);
        var end=new Date(endTime);
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

function showParaModel(){
    $("#paras tbody").empty();
    var parameters=$("#parameters").val();
    if(parameters!=null&&parameters!=''&&parameters.indexOf("}")>0){
        var existParas=JSON.parse(parameters);
        for(var key in existParas){
            var value=existParas[key];
            var tr=$("#pattern tr").clone();
            $($(tr).find("input[name=key]").first()).val(key);
            $($(tr).find("input[name=value]").first()).val(value);
            $("#paras tbody").append(tr);
        }
    }

    $("#paraModal").modal("show");
}


var testChinese=/[\u4E00-\u9FA5]/;

function ensurePara(){
    var trs =$("#paras tbody tr");
    var paras={};
    var flag=true;
    $(trs).each(function(i,c){
        var key=$($(c).find("input[name=key]").first()).val();
        var value=$($(c).find("input[name=value]").first()).val();
        if(key==''){
            flag=false;
            return false;
        }
        if(testChinese.test(key)){
            flag=false;
            return false;
        }

        paras[key]=value;
    });

    if(flag==false){
        new PNotify({
            title: '修改参数',
            text: "key不能为空,且不能为中文,请修改",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });

        return ;
    }


    $("#parameters").val(JSON.stringify(paras));
    $("#paraModal").modal("hide");
}

function addPara(thisTag){
    var tr=$("#pattern tr").clone();
    if(thisTag==null){
        $("#paras tbody").append(tr);
    }
    else{
        $(thisTag).parent().parent().after(tr);
    }
}
function deletePara(thisTag){
    $(thisTag).parent().parent().remove();
}

