var app = null;
var appWorkerGroup = [];
$(function () {
    getAppDetail();
    initAppStatus();
    initAppOwner();
    initAppWorkerGroup();
});
//获取app详细信息
function getAppDetail() {
    if (null != appId && '' != appId) {
        $.ajax({
            url: contextPath + '/api/app/getByAppId',
            type: 'POST',
            async: false,
            data: {appId: appId},
            success: function (data) {
                if (data.code == 1000) {
                    app = data.data;

                    var appName = app.appName;
                    var maxConcurrency = app.maxConcurrency;
                    $("#appName").val(appName);
                    $("#maxConcurrency").val(maxConcurrency);
                }
                else {
                    new PNotify({
                        title: '获取app信息',
                        text: data.msg,
                        type: 'error',
                        icon: true,
                        styling: 'bootstrap3'
                    });
                }
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '获取报警详情', msg);
            }
        });
    }

}

function initAppStatus() {
    $.getJSON(contextPath + "/api/app/getAppStatus", function (data) {
        var newData = new Array();
        $(data).each(function (i, c) {
            if (c["id"] != 3) {
                var item = {};
                item["id"] = c["id"];
                item["text"] = c["text"];
                newData.push(item);
            }
        });

        $("#status").select2({
            data: newData,
            width: '100%'
        });

        if (app != undefined) {
            $("#status").val(app.status).trigger("change");
        }
    });
}

//初始化维护人
function initAppOwner() {
    $.getJSON(contextPath + "/api/common/getAllUser", function (data) {
        if (data.code == 1000) {
            var userData = new Array();
            $(data.rows).each(function (i, c) {
                var item = {};
                item["id"] = c.uname;
                item["text"] = c.nick;
                userData.push(item);
            });

            $("#owner").select2({
                data: userData,
                width: '100%'
            });

            if (null != app && '' != app.owner) {
                var owner = app.owner.trim().split(",");
                var newData = new Array();
                $(owner).each(function (i, c) {
                    newData.push(c);
                });
                $("#owner").val(newData).trigger("change");
            }
        }
        else {
            new PNotify({
                title: '获取内网用户信息',
                text: data.msg,
                type: 'error',
                icon: true,
                styling: 'bootstrap3'
            });
        }
    })


}

//初始化workerGroup
function initAppWorkerGroup() {
    $.getJSON(contextPath + "/api/workerGroup/getAllWorkerGroup", function (data) {
        var newData = new Array();
        $(data).each(function (i, c) {
            var item = {};
            item["id"] = c.id;
            item["text"] = c.name;
            newData.push(item);
        });

        $("#workerGroup").select2({
            data: newData,
            width: '100%'
        });

        if (null != appId && '' != appId) {
            $.getJSON(contextPath + "/api/workerGroup/getByAppId", {appId: appId}, function (data) {
                $(data.rows).each(function (i, c) {
                    appWorkerGroup.push(c.id);
                });
                $("#workerGroup").val(appWorkerGroup).trigger("change");
            });
        }
    });
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
    return result;
}

function getData() {
    var result = {};
    var appName = $("#appName").val();
    var maxConcurrency = $("#maxConcurrency").val();
    var status = $("#status").val();
    var ownerArr = $("#owner").val();

    var owner = "";
    if (null != ownerArr) {
        owner = ArrToStr(ownerArr);
    }

    result["appId"] = appId;
    result["applicationName"] = appName;
    result["maxConcurrency"] = maxConcurrency;
    result["status"] = status;
    result["owner"] = owner;

    return result;
}

//获取app与workerGroup组的操作数据
function getAppWorkerGroupData() {
    var currentWorkerGroup = $("workerGroup").val();
    var allWorkerGroup = {};    //暂存所有workerGroup
    var appWorkerGroupJson = {};
    var currentWorkerGroupJson = {};

    $(appWorkerGroup).each(function (i, c) {
        if (null == allWorkerGroup[c]) {
            allWorkerGroup[c] = c;
            appWorkerGroupJson[c] = c;
        }
    });
    $(currentWorkerGroup).each(function (i, c) {
        if (null == allWorkerGroup[c]) {
            allWorkerGroup[c] = c;
            currentWorkerGroupJson[c] = c;
        }
    });

    var result = {};
    var add = new Array();
    var subtract = new Array();
    for (key in allWorkerGroup) {
        if (appWorkerGroupJson[key] != null && currentWorkerGroupJson[key] == null) {
            var item = {};
            item["appId"] = appId;
            item["workerGroupId"] = key;
            subtract.push(item);
        }
        else if (appWorkerGroupJson[key] == null && currentWorkerGroupJson[key] != null) {
            var item = {};
            item["appId"] = appId;
            item["workerGroupId"] = key;
            add.push(item);
        }
    }
    result["add"] = add;
    result["subtract"] = subtract;
    return result;
}

//保存app
function saveApp() {
    var flag = checkAppName();
    if (false == flag) {
        return;
    }
    var data = getData();   //获取应用数据
    //新增
    if (null == appId || '' == appId) {
        var response = requestRemoteRestApi("/api/app/add", "新增应用", data);
        console.log(response);
        if (true == response.flag) {
            //appId=;
            //modifyAppWorkerGroup();
        }

    }
    //编辑
    else {
        var response = requestRemoteRestApi("/api/app/edit", "修改应用", data);
        //修改成功
        if (true == response.flag) {
            modifyAppWorkerGroup();
        }
    }
}
//绑定workerGroup(新增或删除)
function modifyAppWorkerGroup() {
    var workerGroupData = getAppWorkerGroupData();
    if (workerGroupData["add"].length > 0) {
        requestRemoteRestApi("/api/app/workerGroup/add", "新增WorkerGroup", workerGroupData["add"]);
    }
    if (workerGroupData["subtract"].length > 0) {
        requestRemoteRestApi("/api/app/workerGroup/delete", "删除WorkerGroup", workerGroupData["subtract"]);
    }
}
//检查app名是否重复
function checkAppName() {
    var appName = $("#appName").val();
    if (null == appName || "" == appName) {
        return false;
    }
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
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '检查应用名称', msg);
        }
    });

    return flag;
}