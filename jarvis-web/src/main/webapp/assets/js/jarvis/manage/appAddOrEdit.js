$(function () {

    $.getJSON(contextPath + "/assets/json/appStatus.json", function (data) {
        $("#status").select2({
            data: data,
            width: '100%'
        });

        if (appstatus != undefined) {
            $("#status").val(appstatus).trigger("change");
        }
    });

    initOwner();
    initMember();
});

function initOwner() {
    var owner = $("#owner").attr("title");
    var data = new Array();

    var arr = owner.split(",");
    $(arr).each(function (i, c) {
        data.push(c);
    });
    $("#owner").select2({
        width: '100%'
    });
    $("#owner").val(data).trigger("change");
}

function initMember() {
    var member = $("#member").attr("title");
    var data = new Array();
    var arr = member.split(",");
    $(arr).each(function (i, c) {
        data.push(c);
    });
    $("#member").select2({
        width: '100%'
    });
    $("#member").val(data).trigger("change");
}


/**
 * 修改应用信息
 * */
function updateApp() {
    var appId = $("#appId").val();
    var applicationName = $("#appName").val();
    var status = $("#status").val();
    var maxConcurrency = $("#maxConcurrency").val();
    var flag = checkAppName();
    var owner = ArrToStr($("#owner").val());
    var member = ArrToStr($("#member").val());
    if (flag == false) {
        return;
    }

    var data = {appId: appId, applicationName: applicationName, status: status, maxConcurrency: maxConcurrency};
    requestRemoteRestApi("/api/app/edit", "修改应用", data);
}


function addApp() {
    var applicationName = $("#appName").val();
    //var status=$("#status").val();
    var flag = checkAppName();
    if (flag == false) {
        return;
    }
    var data = {applicationName: applicationName};
    requestRemoteRestApi("/api/app/add", "新增应用", data);
}

function ArrToStr(arr) {
    var result = "";
    $(arr).each(function (i, c) {
        if ('' == result) {
            result = c;
        }
        else {
            result += "," + c;
        }
    });
}

function checkAppName() {
    var appId = $("#appId").val();
    var appName = $("#appName").val();
    var flag = true;
    $.ajax({
        url: contextPath + '/manage/checkAppName',
        type: 'POST',
        async: false,
        data: {appId: appId, appName: appName},
        success: function (data) {
            if (data.code == 1) {
                new PNotify({
                    title: '保存应用',
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
                flag = false;
                $("#appName").focus();
            }
        }
    });

    return flag;
}