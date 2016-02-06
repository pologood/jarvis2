var apiUrl = contextPath + "/api/task/getDepend?taskId=";
var taskUrl = contextPath + "/task/dependency?taskId=";

$(function () {
    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/assets/json/taskStatusColor.json", function (data) {
        stautsColor = data;
    });
    $.ajaxSettings.async = true;
    var tree = CollapsibleTree("#dependTree");
    tree.init(apiUrl + taskDependQo.taskId);
});

function jumpToNode(d) {
    if (!d.rootFlag) {
        window.location.href = taskUrl + d.taskId;
    }
}

//计算填充颜色
function getColor(d) {
    if (d.status != null) {
        return stautsColor[d.status];
    }
}

function getNodeName(d){
    return d.jobName + "_" + d.taskId;
}


function showTaskInfo(thisTag, d) {
    var options = {};

    var content = getContent(d);
    options["title"] = d.jobName + "_" + d.taskId;
    options["content"] = content;
    options["template"] = '<div class="popover" role="tooltip" style="width:100%"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>';
    options["animation"] = true;
    options["placement"] = "bottom";
    options["container"] = $("#popoverContainer");

    //console.log(options);

    $(thisTag).popover(options);
    var result = $(thisTag).popover('show');
    var popId = $(result).attr("aria-describedby");
    $("#" + popId).find("h3").append($('<a class="close" onclick="clickHideTaskInfo(\'' + popId + '\')"><span aria-hidden="true">&times;</span></a>'));
    $("#" + popId).find(".popover-content").html(content);
}
function getContent(d) {
    var content = $("<div></div>");
    var single = $("#pattern").children().clone();
    $(single).find("[name=jobId]").text(d.jobId);
    $(single).find("[name=taskId]").text(d.taskId);
    $(single).find("[name=executeUser]").text(d.executeUser);
    $(single).find("[name=executeStartTime]").text(formatDateTime(d.executeStartTime));
    $(single).find("[name=executeEndTime]").text(formatDateTime(d.executeEndTime));
    $(single).find("[name=executeTime]").text(formatTimeInterval(d.executeTime));
    $(single).find("[name=scheduleTime]").text(formatDateTime(d.scheduleTime));
    content.append(single);
    return content;
}

function hideTaskInfo(thisTag) {
    $(thisTag).popover('hide');
}

function clickHideTaskInfo(tagId) {
    $("#" + tagId).popover('hide');
}

function chooseTask(d) {
    var content = getContent(d);
    $("#toTaskModal .modal-body").html(content);
    $("#toTaskModal").modal("show");
}


