$(function () {

});

function updateSystemStatus(status) {
    var data = {status: status};
    requestRemoteRestApi("/api/system/status", "修改系统状态", data);
}