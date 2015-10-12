$(function(){
    $(".input-group select").select2({width:'100%'});
});

//检查worker group名称
function checkWorkerGroupName(){
    var workerGroupId=$("#workerGroupId").val();
    var name=$("#name").val();
    var flag=true;
    $.ajax({
        url:'/jarvis/manage/checkWorkerGroupName',
        type:'POST',
        data:{id:workerGroupId,name:name},
        success:function(data){
            if(data.code==1){
                new PNotify({
                    title: '保存worker group',
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

function updateWorkerGroup(){
    var workerGroupId=$("#workerGroupId").val();
    var name=$("#name").val();
    var flag=checkWorkerGroupName();
    if(flag==false){
        return;
    }
    $.ajax({
        url:'',
        type:'POST',
        data:{id:workerGroupId,name:name},
        success:function(data){

        }
    });
}

function addWorkerGroup(){
    var workerGroupId=$("#workerGroupId").val();
    var name=$("#name").val();
    var flag=checkWorkerGroupName();
    if(flag==false){
        return;
    }

    $.ajax({
        url:'',
        type:'POST',
        data:{name:name},
        success:function(data){

        }
    });
}