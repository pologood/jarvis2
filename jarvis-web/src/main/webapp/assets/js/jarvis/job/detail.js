var dependencyUrl = contextPath + "/api/job/getDepend?jobId=";
var url = dependencyUrl + jobVo.jobId;
var detailUrl = contextPath + "/job/detail?jobId=";


$(function () {
    if (undefined != jobVo.jobId) {
        initJobData();     //初始化job详细信息

        $.ajax({
            url: contextPath + "/assets/json/jobStatusColor.json",
            async: false,
            success: function (data) {
                stautsColor = data;
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '初始化状态颜色', msg);
            }
        })
        var level = 1;
        var size = 5;
        var parentLevel = 0;
        var childLevel = 0;
        $.ajax({
            url: url,
            async: false,
            success: function (data) {
                var children = data.children;
                var parents = data.parents;

                parentLevel = parents.length % size == 0 ? parents.length / size : parseInt(parents.length / size) + 1;
                childLevel = children.length % size == 0 ? children.length / size : parseInt(children.length / size) + 1;

                level = level + parentLevel + childLevel;
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '初始化执行用户', msg);
            }
        });

        var y = 0;
        var width = 1140;
        var height = 100;
        if (level > 1) {
            height = (level - 1) * 120 + 200;
        }
        else {
            height = 140;
        }

        if (parentLevel == 0 && childLevel != 0) {
            y = 50;
        }
        else if (parentLevel != 0 && childLevel == 0) {
            y = height - 50;
        }
        else if (parentLevel == 0 && childLevel == 0) {
            y = height / 2;
        }
        else {
            y = height * parentLevel / (level - 1)+20;
        }
        //console.log(y);
        //console.log(height);


        var tree = CollapsibleTree("#dependTree", width, height,y);
        tree.init(url);
    }
});

function jumpToNode(d) {
    if (!d.rootFlag) {
        window.location.href = detailUrl + d.jobId;
    }
}
//初始化job基本信息
function initJobData() {
    if (undefined != jobVo.jobId) {
        $.ajax({
            url: contextPath + "/api/job/getById",
            data: {jobId: jobVo.jobId},
            success: function (data) {
                var newData = data.data;
                for (var key in newData) {
                    var value = generateValue(key, newData[key]);
                    $("#" + key).text(value);
                }
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '初始化任务信息', msg);
            }
        })

    }
}

//格式化显示数据
function generateValue(key, source) {
    var formatter = {
        "status": function (sourceData) {
            var result = "";

            $.ajax({
                url: contextPath + "/api/job/getJobStatus",
                async: false,
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].id == sourceData) {
                            result = data[i].text;
                            break;
                        }
                    }
                },
                error: function (jqXHR, exception) {
                    var msg = getMsg4ajaxError(jqXHR, exception);
                    showMsg('warning', '初始化显示颜色信息', msg);
                }
            })
            return result;
        },
        "appId": function (sourceData) {
            var result = "";
            $.ajax({
                url: contextPath + "/api/app/getByAppId",
                data: {appId: sourceData},
                async: false,
                success: function (data) {
                    if (null != data.appId) {
                        result = data.appName;
                    }
                },
                error: function (jqXHR, exception) {
                    var msg = getMsg4ajaxError(jqXHR, exception);
                    showMsg('warning', '获取应用信息', msg);
                }
            })
            return result;
        },
        "workerGroupId": function (sourceData) {
            var result = "";

            $.ajax({
                url: contextPath + "/api/workerGroup/getById",
                async: false,
                data: {id: sourceData},
                success: function (data) {
                    if (null != data.id) {
                        result = data.name;
                    }
                },
                error: function (jqXHR, exception) {
                    var msg = getMsg4ajaxError(jqXHR, exception);
                    showMsg('warning', '获取workerGroup信息', msg);
                }
            })
            return result;
        },
        "bizGroupId": function (sourceData) {
            var result = "";
            $.ajax({
                url: contextPath + "/api/bizGroup/getById",
                data: {id: sourceData},
                async: false,
                success: function (data) {
                    if (null != data.id) {
                        result = data.name;
                    }
                },
                error: function (jqXHR, exception) {
                    var msg = getMsg4ajaxError(jqXHR, exception);
                    showMsg('warning', '获取业务组信息', msg);
                }
            })

            return result;
        },
        "activeStartDate": function (sourceData) {
            return moment(sourceData).format("YYYY-MM-DD");
        },
        "activeEndDate": function (sourceData) {
            return moment(sourceData).format("YYYY-MM-DD");
        },
        "expiredTime": function (sourceData) {
            return sourceData + "秒";
        }
    };
    if (key in formatter) {
        var result = formatter[key](source);
        return result;
    }
    else {
        return source;
    }
}


//计算填充颜色
function getColor(d) {
    return stautsColor[d.status];
}


function showNoteInfo(thisTag, d) {
    var options = {};

    var content = getContent(d);

    options["title"] = d.jobName;
    options["content"] = content;
    options["template"] = '<div class="popover" role="tooltip" style="width:100%"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>';
    options["animation"] = true;
    options["placement"] = "bottom";
    options["container"] = $("#popoverContainer");

    $(thisTag).popover(options);
    var result = $(thisTag).popover('show');
    var popId = $(result).attr("aria-describedby");
    $("#" + popId).find("h3").append($('<a class="close" onclick="clickHideNoteInfo(\'' + popId + '\')"><span aria-hidden="true">&times;</span></a>'));
    $("#" + popId).find(".popover-content").html(content);

}

function getContent(d) {

    var content = $("<div></div>");
    var single = $("#pattern").children().clone();

    $(single).find("[name=jobId]").text(d.jobId);
    $(single).find("[name=submitUser]").text(d.submitUser);
    $(single).find("[name=jobType]").text(d.jobType);
    $(single).find("[name=status]").text(d.status);
    $(single).find("[name=priority]").text(d.priority);
    $(single).find("[name=bizGroupName]").text(d.bizGroupName);

    content.append(single);

    return content;
}

function hideNoteInfo(thisTag) {
    $(thisTag).popover('hide');
}

function clickHideNoteInfo(tagId) {
    $("#" + tagId).popover('hide');
}

function chooseTask(d) {
    var content = getContent(d);
    $("#toTaskModal .modal-body").html(content);
    $("#toTaskModal").modal("show");
}