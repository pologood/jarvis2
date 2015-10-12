$(function(){
    $(".input-group select").select2({width:'100%'});
});



function updateApp(){
    var appId=$("#appId").val();
    var appName=$("#appName").val();
    var status=$("#status").val();
    var flag=checkAppName();
    if(flag==false){
        return;
    }
    $.ajax({
        url:'/jarvis/aa',
        type:'POST',
        data:{appId:appId,appName:appName,status:status},
        success:function(data){

        }
    });
}

function addApp(){
    var appName=$("#appName").val();
    var status=$("#status").val();
    var flag=checkAppName();
    if(flag==false){
        return;
    }
    $.ajax({
        url:'/jarvis/aa',
        type:'POST',
        data:{appName:appName,status:status},
        success:function(data){

        }
    });
}




function checkAppName(){
    var appId=$("#appId").val();
    var appName=$("#appName").val();
    var flag=true;
    $.ajax({
        url:'/jarvis/manage/checkAppName',
        type:'POST',
        async:false,
        data:{appId:appId,appName:appName},
        success:function(data){
            if(data.code==1){
                new PNotify({
                    title: '保存应用',
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
                flag=false;
                $("#appName").focus();
            }
        }
    });

    return flag;
}