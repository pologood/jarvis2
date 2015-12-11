$(function(){
    $(".input-group select").select2({width:'100%'});
});



function updateApp(){
    var appId=$("#appId").val();
    var applicationName=$("#appName").val();
    //var status=$("#status").val();
    var flag=checkAppName();
    if(flag==false){
        return;
    }

    var data={appId:appId,applicationName:applicationName};
    requestRemoteRestApi("/api/app/update","修改应用",data);
}

function addApp(){
    var applicationName=$("#appName").val();
    //var status=$("#status").val();
    var flag=checkAppName();
    if(flag==false){
        return;
    }
    var data={applicationName:applicationName};
    requestRemoteRestApi("/api/app/add","新增应用",data);
}




function checkAppName(){
    var appId=$("#appId").val();
    var appName=$("#appName").val();
    var flag=true;
    $.ajax({
        url:contextPath+'/manage/checkAppName',
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