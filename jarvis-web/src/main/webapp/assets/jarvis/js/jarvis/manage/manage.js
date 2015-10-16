$(function(){

});

function updateSystemStatus(status){
    var data={status:status};
    requestRemoteRestApi("/system/status","修改系统状态",data);
}