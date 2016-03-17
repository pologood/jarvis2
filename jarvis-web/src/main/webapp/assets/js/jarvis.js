function showMsg(type, title, text) {
    new PNotify({
        title: title,
        text: text,
        type: type,
        icon: true,
        styling: 'bootstrap3'
    });
}

//时间选择器
function createDatetimePickerById(tagId) {
    if (tagId == undefined || tagId == '') {
        return;
    }

    $("#" + tagId).datetimepicker({
        language: 'zh-CN',
        minView: 'month',
        format: 'yyyy-mm-dd',
        autoclose: true
    });
}

//通过后台请求远程rest api,根据请求结果返回flag
function requestRemoteRestApi(url, title, data, async) {
    var flag = true;
    var result = {};

    if (null == async) {
        async = false;
    }

    $.ajax({
        url: contextPath + '/remote/request',
        type: 'POST',
        async: async,
        data: {url: url, para: JSON.stringify(data)},
        success: function (data) {
            if (data.code == 0) {
                flag = true;
                if (data.msg == null || data.msg == '') {
                    showMsg('success', title, '操作成功');
                }
                else {
                    showMsg('success', title, data.msg);
                }
            }
            else {
                flag = false;
                if (data.msg == null || data.msg == '') {
                    showMsg('warning', title, '操作失败');
                }
                else {
                    showMsg('warning', title, data.msg);
                }
            }
            result["data"] = data;
        },
        error: function (jqXHR, exception) {
            flag = false;
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', title, msg);
        }
    });

    result["flag"] = flag;
    return result;
}


//时间戳转化成日期
function formatDate(date) {
    if (date == undefined || date == '') {
        return '-';
    }
    var theDate = new Date(date);
    var result = moment(theDate).format("YYYY-MM-DD");
    return result;
}
//时间戳转化成日期时间
function formatDateTime(dateTime) {
    if (dateTime == null || dateTime == '') {
        return '-';
    }
    var theDate = new Date(dateTime);
    var result = moment(theDate).format("YYYY-MM-DD HH:mm:ss");
    return result;
}

//时间戳转化成日期时间
function formatDateTimeWithoutYear(dateTime) {
    if (dateTime == null || dateTime == '') {
        return '-';
    }
    var theDate = new Date(dateTime);
    var result = moment(theDate).format("MM-DD HH:mm:ss");

    result = "<div style='white-space:nowrap;'>" + result + "</div>";

    return result;
}


//秒转化成对应的时间长度
function formatTimeInterval(timeInterval) {
    //console.log(timeInterval);
    if (timeInterval == undefined) {
        return "-";
    }
    if (parseInt(timeInterval) == 0) {
        return "0秒";
    }
    var result = "";

    var day = 24 * 3600;
    var hour = 3600;
    var minute = 60;

    var totalDay = parseInt(timeInterval / day);
    var totalHour = parseInt((timeInterval % day) / hour);
    var totalMinute = parseInt(((timeInterval % day) % hour) / minute);
    var totalSecond = (((timeInterval % day) % hour) % minute);

    if (totalDay > 0) {
        result = totalDay + "天";
    }
    if (totalHour > 0) {
        result += totalHour + "小时";
    }
    if (totalMinute > 0) {
        result += totalMinute + "分钟";
    }
    if (totalSecond > 0) {
        result += totalSecond + "秒";
    }

    return result;
}

//格式化状态
function formatStatus(dataArr, status) {
    if (dataArr == undefined || dataArr == null || status == undefined || status == null) {
        return "";
    }
    var statusStr = "";
    $(dataArr).each(function (i, c) {
        var id = c["id"];
        if (id == status) {
            statusStr = c["text"];
            return false;
        }
    });
    return statusStr;
}

function arrToString(arr) {
    if (null == arr) {
        return "";
    }
    var result = "";
    for (var i, len = arr.length; i < len; i++) {
        if ("" == result) {
            result = arr[i];
        }
        else {
            result += "," + arr[i];
        }
    }
}

function stringToArr(source) {
    if (null == source || "" == source) {
        return [];
    }
    var arr = source.trim().split(",");
    return arr;
}