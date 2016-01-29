var dependencyUrl = contextPath + "/api/job/getTwoDirectionTree?jobId=";
var url = dependencyUrl + jobVo.jobId;

$(function () {
    if (undefined != jobVo.jobId) {
        initJobData();     //初始化job详细信息
        var tree = CollapsibleTree("#dependTree");
        tree.init(url);
    }
});

//初始化job基本信息
function initJobData() {
    if (undefined != jobVo.jobId) {
        $.getJSON(contextPath + "/api/job/getById", {jobId: jobVo.jobId}, function (data) {
            var newData = data.data;
            console.log(newData);
            for (var key in newData) {
                var value = generateValue(key, newData[key]);
                $("#" + key).text(value);
            }
        })
    }
}

//格式化显示数据
function generateValue(key, source) {
    var formatter = {
        "status": function (sourceData) {
            var result = "";
            $.ajaxSettings.async = false;
            $.getJSON(contextPath + "/api/job/getJobStatus", function (data) {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].id == sourceData) {
                        result = data[i].text;
                        break;
                    }
                }
            });
            $.ajaxSettings.async = true;
            return result;
        },
        "appId": function (sourceData) {
            var result = "";
            $.ajaxSettings.async = false;
            $.getJSON(contextPath + "/api/app/getByAppId", {appId: sourceData}, function (data) {
                if (null != data.appId) {
                    result = data.appName;
                }
            });
            $.ajaxSettings.async = true;
            return result;
        },
        "workerGroupId": function (sourceData) {
            var result = "";
            $.ajaxSettings.async = false;
            $.getJSON(contextPath + "/api/workerGroup/getById", {id: sourceData}, function (data) {
                if (null != data.id) {
                    result = data.name;
                }
            });
            $.ajaxSettings.async = true;
            return result;
        },
        "bizGroupId": function (sourceData) {
            var result = "";
            $.ajaxSettings.async = false;
            $.getJSON(contextPath + "/api/bizGroup/getById", {id: sourceData}, function (data) {
                if (null != data.id) {
                    result = data.name;
                }
            });
            $.ajaxSettings.async = true;
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


