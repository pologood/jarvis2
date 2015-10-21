$(function(){
    $(".input-group select").select2({width:'100%'});
});

//同一id与端口只能存在一个worker
function checkWorkerExist(){
    var workerId=$("#workerId").val();
    var ip=$("#ip").val();
    var port=$("#port").val();
    var flag=true;
    $.ajax({
        url:'/jarvis/manage/checkWorkerExist',
        type:'POST',
        data:{id:workerId,ip:ip,port:port},
        success:function(data){
            if(data.code==1){
                new PNotify({
                    title: '保存worker',
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
                flag=false;
            }
        }
    });

    return flag;
}

function addWorker(){
    var ip=$("#ip").val();
    var port=$("#port").val();
    var flag=checkWorkerExist();
    if(flag==false){
        return ;
    }
    requestRemoteRestApi("/api/worker/add","增加Worker",data);
}

function updateWorker(){
    var workerId=$("#workerId").val();
    var ip=$("#ip").val();
    var port=$("#port").val();
    var flag=checkWorkerExist();
    if(flag==false){
        return ;
    }
    requestRemoteRestApi("/api/worker/update","更新Worker",data);
}

function modifyWorkerStatus(workerGroupId,status){
    var data={workerGroupId:workerGroupId,status:status}
    requestRemoteRestApi("/api/worker/status","修改worker group 状态",data);

}