var dependencyAPIUrl = contextPath + "/api/plan/getDependDetail?taskId=";
var dependencyUrl = contextPath + "/plan/dependency?taskId=";
var url = dependencyAPIUrl + taskId;


$(function () {
    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/assets/json/taskStatusColor.json", function (data) {
        stautsColor = data;
    });
    $.ajaxSettings.async = true;
    var tree = CollapsibleTree("#dependTree");
    tree.init(url);
});

//计算填充颜色
function getColor(d) {
    //不为null，代表为task
    if (d.status != null) {
        return stautsColor[d.status];
    }
    //代表是job，需要根据task计算显示颜色
    else {
        var taskList = d.taskList;
        //没有对应task，显示白色
        if (taskList.length <= 0) {
            return "white";
        }
        //有对应task，根据task状态计算显示颜色
        else {
            var totalStatus = {};
            var statusArr = new Array();
            //获取所有task状态
            $(taskList).each(function (i, c) {
                var status = c.status;
                if (totalStatus[status] == null) {
                    totalStatus[status] = status;
                    statusArr.push(status);
                }
            });
            //
            if (statusArr.length == 1) {
                return stautsColor[statusArr[0]];
            }
            else {
                return "gray";
            }
        }
    }
}


function showTasks(thisTag, d) {
    var options = {};

    var content = getContent(d);


    options["title"] = "任务ID:" + d.jobId + " 任务名:" + d.jobName;
    options["content"] = content;
    options["template"] = '<div class="popover" role="tooltip" style="width:100%"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>';
    options["animation"] = true;
    options["placement"] = "bottom";
    options["container"] = $("#popoverContainer");

    //console.log(options);

    $(thisTag).popover(options);
    var result = $(thisTag).popover('show');
    var popId = $(result).attr("aria-describedby");
    $("#" + popId).find("h3").append($('<a class="close" onclick="clickHideTasks(\'' + popId + '\')"><span aria-hidden="true">&times;</span></a>'));
    $("#" + popId).find(".popover-content").html(content);
}
function getContent(d) {
    var taskList = d.taskList;
    var content = $("<div></div>");

    if (d.rootFlag == true) {
        taskList = new Array();
        taskList.push(d);
    }
    //console.log(taskList);

    for (var i = 0, len = taskList.length; i < len; i++) {
        var task = taskList[i];
        var jobId = task.jobId;
        var taskId = task.taskId;
        var executeUser = task.executeUser;
        var scheduleTime = formatDateTime(task.scheduleTime);
        var executeStartTime = formatDateTime(task.executeStartTime);
        var executeEndTime = formatDateTime(task.executeEndTime);
        var executeTime = formatTimeInterval(task.executeTime);
        var status = task.status;

        var color = stautsColor[task.status];
        var single = $("#pattern").children().clone();
        var newUrl = dependencyUrl + taskId;

        //executeTime="1天23小时59分59秒";
        //jobId="1000000";
        //taskId="1000000";
        //executeUser="qqqqqqqqqq";

        $(single).find("[name=status]").attr("href", newUrl);
        $(single).find("[name=status] i").css("color", color);
        $(single).find("[name=jobId]").text(jobId);
        $(single).find("[name=taskId]").text(taskId);
        $(single).find("[name=executeUser]").text(executeUser);
        $(single).find("[name=executeStartTime]").text(executeStartTime);
        $(single).find("[name=executeEndTime]").text(executeEndTime);
        $(single).find("[name=executeTime]").text(executeTime);
        $(single).find("[name=scheduleTime]").text(scheduleTime);

        content.append(single);
    }

    return content;
}

function hideTasks(thisTag) {
    $(thisTag).popover('hide');
}

function clickHideTasks(tagId) {
    $("#" + tagId).popover('hide');
}

function chooseTask(d) {
    var content = getContent(d);
    $("#toTaskModal .modal-body").html(content);
    $("#toTaskModal").modal("show");
}


