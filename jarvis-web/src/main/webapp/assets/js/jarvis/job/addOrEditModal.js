//------------------------------ 1. 选择脚本  --------------------------------

$(function () {

    initSearchScriptModal();        //初始化搜索Script模式框
    initJobScheduleModal();         //初始化任务计划模式框

    //--针对多个modal显示时,第二个modal会被第一个modal覆盖住.(文件上传modal,java任务参数modal)
    $('.modal').on('show.bs.modal', function (event) {
        var idx = $('.modal:visible').length;
        $(this).css('z-index', 1040 + (10 * idx));
    });
    $('.modal').on('shown.bs.modal', function (event) {
        var idx = ($('.modal:visible').length) - 1; // raise backdrop after animation.
        $('.modal-backdrop').not('.stacked').css('z-index', 1039 + (10 * idx));
        $('.modal-backdrop').not('.stacked').addClass('stacked');
    });
    //--end

});

//初始化——选择脚本-模态框
function initSearchScriptModal() {

    $('#searchScriptList').btsListFilter('#searchScriptInput', {
        resetOnBlur: false,
        minLength: 0,
        sourceTmpl: '<a href="javascript:void(0);" data-id="{id}" data-title="{title}" class="list-group-item">{title} &nbsp|&nbsp{creator}</a>',
        sourceData: function (text, callback) {
            return $.getJSON(contextPath + "/api/script/queryScript?name=" + text, function (json) {
                callback(json.data);
            });
        }
    });

    $("#searchScriptList").on("dblclick", "a", function () {
        $.ajax({
            url: contextPath + "/api/script/getScriptById?id=" + $(this).attr("data-id"),
            async: false,
            success: function (result) {
                if (result.code == 1000 && result.data != null) {
                    var script = result.data;
                    $("#scriptId").val(script.id);
                    $("#scriptTitle").val(script.title);
                    $("#jobContent").val(script.content);
                    $("#searchScriptModal").modal("hide");
                } else {
                    alert(result.msg);
                }
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '初始化脚本', msg);
            }
        })
    });

}

//显示-选择脚本-模态框
function showSearchScriptModal() {
    $("#searchScriptInput").val("");
    $("#searchScriptList").empty();
    $("#searchScriptModal").modal("show");
}


//------------------------------ 2 上传jar文件  ------------------------------


//显示-选择脚本-模态框
function showUploadJarModal(target) {
    $("#localFile").val("").attr("data-target",target);
    $("#uploadJarModal").modal("show");
}

function confirmUploadJar() {
    var formData = new FormData();
    formData.append('file', $('#localFile')[0].files[0]);
    formData.append('title', '');
    $.ajax({
        url: contextPath + '/api/file/uploadJar',
        type: 'POST',
        async: false,
        data: formData,
        processData: false,  // tell jQuery not to process the data
        contentType: false,  // tell jQuery not to set contentType
        success: function (json) {
            if (json.code == CONST.MSG_CODE.SUCCESS) {
                var uploadUrl = json.data;
                var target = $('#localFile').attr("data-target");
                var targetCtl = $("#" + target);
                if(target == 'classpath'){  //classpath是以,分开的多重文件
                    var oldValue = $(targetCtl).val();
                    if(oldValue.indexOf(uploadUrl) < 0){
                        var separator = oldValue.length > 0 ? ',' : '';
                        $(targetCtl).val(oldValue + separator + uploadUrl);
                    }
                }else {
                    $(targetCtl).val(uploadUrl)
                }
                $('#uploadJarModal').modal('hide');
            } else {
                showMsg('warning', '上传jia包', json.msg);
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '上传jar包', msg);
        }
    });

    //$.ajax({
    //    url : 'upload.php',
    //    type : 'POST',
    //    data : formData,
    //    processData: false,  // tell jQuery not to process the data
    //    contentType: false,  // tell jQuery not to set contentType
    //    success : function(data) {
    //        console.log(data);
    //        alert(data);
    //    }
    //});

}


//------------------------------ 2 任务参数  ------------------------------

//------------------------------ 2.1 常规任务参数  ------------------------------
//显示-任务参数-模态框
function showParaModal() {
    var jobType = $("#jobType").val();
    if (jobType == CONST.JOB_TYPE.SPARK_LAUNCHER) {
        showSparkLauncherParasModal();
    } else if (jobType == CONST.JOB_TYPE.JAVA) {
        showJavaParasModal();
    } else {
        showCommonJobParaModal()
    }
}

//显示-任务参数-模态框
function showCommonJobParaModal() {
    var trBody = $("#parasTable tbody");
    var trPattern = $("#pattern tr");
    $(trBody).empty();
    var params = $("#params").val();
    if (params != null && params != '' && params.indexOf("}") > 0) {
        var existParas = JSON.parse(params);
        for (var key in existParas) {
            var value = existParas[key];
            var tr = $(trPattern).clone();
            $($(tr).find("input[name=key]").first()).val(key);
            $($(tr).find("input[name=value]").first()).val(value);
            $(trBody).append(tr);
        }
    }
    $("#paraModal").modal("show");
}

///确认参数选择
function ensurePara() {
    var trs = $("#parasTable tbody tr");
    var paras = {};
    var flag = true;
    $(trs).each(function (i, c) {
        var key = $($(c).find("input[name=key]").first()).val();
        var value = $($(c).find("input[name=value]").first()).val();
        if (key == '') {
            flag = false;
            return false;
        }
        if (testChinese.test(key)) {
            flag = false;
            return false;
        }
        paras[key] = value;
    });

    if (flag == false) {
        showMsg('warning', '修改参数', "key不能为空,且不能为中文,请修改");
        return;
    }
    $("#params").val(JSON.stringify(paras));
    $("#paraModal").modal("hide");
}

//添加参数
function addPara(thisTag) {
    var tr = $("#pattern tr").clone();
    if (thisTag == null) {
        $("#parasTable tbody").append(tr);
    }
    else {
        $(thisTag).parent().parent().after(tr);
    }
}
//删除参数
function deletePara(thisTag) {
    $(thisTag).parent().parent().remove();
}

//------------------------------ 2.2 sparkLauncher任务参数  ------------------------------

//显示-SparkLauncher任务参数-模态框
function showSparkLauncherParasModal() {
    var existParas = {};
    var params = $("#params").val();
    if (params != null && params != '' && params.indexOf("}") > 0) {
        existParas = JSON.parse(params);
    }

    $("#sparkLauncherParasModalBody input, #sparkLauncherParasModalBody textarea").each(function (i, c) {
        var key = $(this).attr("name");
        if (key in existParas) {
            $(this).val(existParas[key]);
        } else {
            $(this).val($(this).attr("data-defaultValue"));
        }
    });

    $("#sparkLauncherParasModal").modal("show");
}

///确认参数选择
function confirmSparkLauncherParas() {
    var paras = {};
    var flag = true;
    $("#sparkLauncherParasModalBody input, #sparkLauncherParasModalBody textarea").each(function (i, c) {
        var key = $(this).attr("name");
        var val = $(this).val();
        var required = $(this).hasClass("required");
        var desc = $(this).attr("data-desc");
        //参数验证检查
        if (!validSparkLauncherParas(key, val, desc, required)) {
            flag = false;
        }
        paras[key] = val;
    });

    if (flag == false) {
        return;
    }

    var paramsStr = JSON.stringify(paras);
    $("#params").val(paramsStr);
    $("#jobContent").val(CONST.SPARK_LAUNCHER_JOB.COMMAND + " " + paramsStr);
    $("#sparkLauncherParasModal").modal("hide");
}

function validSparkLauncherParas(key, val, desc, required) {

    //为空检查
    if (required && (val == null || val.trim() == "")) {
        showMsg('warning', 'sparkLauncher参数', desc + "不能为空");
        return false;
    }

    switch (key) {
        case CONST.SPARK_LAUNCHER_JOB.PARAMS_KEY.driverCores:    //driver核数
            if (!$.isNumeric(val) || val < 1 || val > 4) {
                showMsg('warning', 'sparkLauncher参数', desc + "不对,请输入1-4之间数字.");
                return false;
            }
            break;
        case CONST.SPARK_LAUNCHER_JOB.PARAMS_KEY.driverMemory :  //driver内存
            if (!/^\d?g$/i.test(val)) {
                showMsg('warning', 'sparkLauncher参数', desc + "不对,请输入'数字+G',比如'4G'.");
                return false;
            }
            break;
        case CONST.SPARK_LAUNCHER_JOB.PARAMS_KEY.executorCores:  //executor核数
            if (!$.isNumeric(val) || val < 1 || val > 4) {
                showMsg('warning', 'sparkLauncher参数', desc + "不对,请输入1-4之间数字.");
                return false;
            }
            break;
        case CONST.SPARK_LAUNCHER_JOB.PARAMS_KEY.executorMemory :    //executor内存
            if (!/^\d?g$/i.test(val)) {
                showMsg('warning', 'sparkLauncher参数', desc + "不对,请输入'数字+G',比如'4G'.")
                return false;
            }
            break;
        case CONST.SPARK_LAUNCHER_JOB.PARAMS_KEY.executorNum:    //executor数目
            if (!$.isNumeric(val) || val < 0) {
                showMsg('warning', 'sparkLauncher参数', desc + "不对,请大于0的数字.")
                return false;
            }
            break;
    }
    return true;
}


//------------------------------ 2.3 java任务参数  ------------------------------
//显示-java任务参数-模态框
function showJavaParasModal() {
    var existParas = {};
    var params = $("#params").val();
    if (params != null && params != '' && params.indexOf("}") > 0) {
        existParas = JSON.parse(params);
    }

    $("#javaParasModalBody input, #javaParasModalBody textarea").each(function (i, c) {
        var key = $(this).attr("name");
        if (key in existParas) {
            $(this).val(existParas[key]);
        } else {
            $(this).val($(this).attr("data-defaultValue"));
        }
    });

    $("#javaParasModal").modal("show");
}

///确认参数选择
function confirmJavaParas() {
    var paras = {};
    var flag = true;
    $("#javaParasModalBody input, #javaParasModalBody textarea").each(function (i, c) {
        var key = $(this).attr("name");
        var val = $(this).val();
        var required = $(this).hasClass("required");
        var desc = $(this).attr("data-desc");
        //参数验证检查
        if (!validJavaParas(key, val, desc, required)) {
            flag = false;
        }
        paras[key] = val;
    });

    if (flag == false) {
        return;
    }

    var paramsStr = JSON.stringify(paras);
    $("#params").val(paramsStr);
    $("#jobContent").val("java");
    $("#javaParasModal").modal("hide");
}

function validJavaParas(key, val, desc, required) {

    //为空检查
    if (required && (val == null || val.trim() == "")) {
        showMsg('warning', 'java参数', desc + "不能为空");
        return false;
    }
    return true;
}


//------------------------------ 3 执行计划  ------------------------------
function initJobScheduleModal() {
    initPerMonthSelect();
    initPerDaySelect();
    initPerWeekSelect();

    $("#perHour,#perMinute,#perSecond").change(function () {
        var max = parseInt($(this).attr('max'));
        var min = parseInt($(this).attr('min'));
        if ($(this).val() > max) {
            $(this).val(max);
        }
        else if ($(this).val() < min) {
            $(this).val(min);
        }
    });

    $("input[name='scheduleType']").change(function (e) {
        changeScheduleType($(this));
    });

    $('#cronSecond').restrict(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', ',', '*', '/']);
    $('#cronMinute').restrict(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', ',', '*', '/']);
    $('#cronHour').restrict(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', ',', '*', '/']);
    $('#cronDay').restrict(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', ',', '*', '/', '?']);
    $('#cronMonth').restrict(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', ',', '*', '/']);
    $('#cronWeek').restrict(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', ',', '*', '/', '?']);

}

//初始化-perDay
function initPerMonthSelect() {
    var newData = [];
    for (var i = 1; i <= 12; i++) {
        newData.push({id: i, text: i + "月"});
    }
    var selector = $("#perMonth");
    $(selector).select2({
        data: newData,
        width: '100%'
    });
}


//初始化-perDay
function initPerDaySelect() {
    var newData = [];
    for (var i = 1; i <= 31; i++) {
        newData.push({id: i, text: i + "日"});
    }
    var selector = $("#perDay");
    $(selector).select2({
        data: newData,
        width: '100%'
    });
}

//初始化-perWeek
function initPerWeekSelect() {
    var newData = [{id: 1, text: "星期一"}, {id: 2, text: "星期二"}, {id: 3, text: "星期三"}
        , {id: 4, text: "星期四"}, {id: 5, text: "星期五"}, {id: 6, text: "星期六"}, {id: 7, text: "星期天"}];
    var selector = $("#perWeek");
    $(selector).select2({
        data: newData,
        width: '100%'
    });
}

(function ($) {
    $.fn.hourEnable = function (enable) {
        if (!$(this).hasClass("disabled") == enable) {    //判断状态是否改变
            return;
        }
        if (enable) {
            $(this).removeAttr("disabled").removeClass("disabled").val(pageJobEdit.curPerHour);
        } else {
            pageJobEdit.curPerHour = $(this).val();
            $(this).attr("disabled", true).addClass("disabled").val("");
        }
    };
})(jQuery);

//改变-计划类型
function changeScheduleType(curRadio) {

    var curValue = $(curRadio).val();
    if (curValue == CONST.SCHEDULE_TYPE.PER_DAY) {    //每天
        $("#perHour").hourEnable(true);
        $("#perMonthDiv").hide();
        $("#perDayDiv").hide();
        $("#perWeekDiv").hide();
    } else if (curValue == CONST.SCHEDULE_TYPE.PER_HOUR) {  //每小时
        $("#perHour").hourEnable(false);
        $("#perMonthDiv").hide();
        $("#perDayDiv").hide();
        $("#perWeekDiv").hide();
    } else if (curValue == CONST.SCHEDULE_TYPE.PER_WEEK) {  //每周
        $("#perHour").hourEnable(true);
        $("#perMonthDiv").hide();
        $("#perDayDiv").hide();
        $("#perWeekDiv").show();
    } else if (curValue == CONST.SCHEDULE_TYPE.PER_MONTH) {  //每月
        $("#perHour").hourEnable(true);
        $("#perMonthDiv").hide();
        $("#perDayDiv").show();
        $("#perWeekDiv").hide();
    } else if (curValue == CONST.SCHEDULE_TYPE.PER_YEAR) {  //每年
        $("#perHour").hourEnable(true);
        $("#perMonthDiv").show();
        $("#perDayDiv").show();
        $("#perWeekDiv").hide();
    }
}

//显示-任务计划-模态框
function showJobScheduleModal() {
    $("#jobScheduleModal").modal("show");
}


//高级参数-显示或隐藏
function toggleCronHelpDiv(thisTag) {
    var oldState = $(thisTag).find("i").attr("data-state");
    var newState = oldState == "up" ? "down" : "up";
    $(thisTag).find("i").removeClass("glyphicon-chevron-" + oldState)
        .addClass("glyphicon-chevron-" + newState)
        .attr("data-state", newState);
    $("#cronHelpDiv").toggle();
}

$.fn.restrict = function (chars) {
    return this.keypress(function (e) {
        var found = false, i = -1;
        while (chars[++i] != null && !found) {
            found = chars[i] == String.fromCharCode(e.keyCode).toLowerCase() ||
                chars[i] == e.which;
        }
        found || e.preventDefault();
    });
};

function validCronTab() {

    var cronExp = "";
    var flag = true;

    $('#cronTable .cronInput').each(function (i, c) {

        var val = $(c).val();
        var desc = $(c).attr("data-desc");

        cronExp = cronExp + $(c).val();
        if (val == null || val.trim() == '') {
            showMsg("warning", "cron参数输入", desc + "不能为空");
            flag = false;
            return
        }
        if (!/^[\d\-\*\/,\?#L]+$/i.test(val)) {
            showMsg("warning", "cron参数输入", desc + "输入字符不对,请输入 0-9数字,以及特殊字符'-,*?/#L'");
            flag = false;
        }
    });

    if (flag == false) {
        return flag;
    }

    //$.ajax({
    //    url: contextPath + '/remote/',
    //    type: 'POST',
    //    async: false,
    //    data: {cronExp: cronExp},
    //    success: function (data) {
    //        if (data.code == 0) {
    //            flag = true;
    //        } else {
    //            flag = false;
    //            showMsg('warning', title, (data.msg == null || data.msg == '') ? '操作失败' : data.msg);
    //        }
    //    },
    //    error: function (jqXHR, exception) {
    //        flag = false;
    //        var msg = getMsg4ajaxError(jqXHR, exception);
    //        showMsg('warning', title, msg);
    //    }
    //});

    return flag;

}


function cronExpression2desc(expression) {
    var a = later.parse.cron("1 2 3 4 5 6 7", true);
}

///确认任务计划
function confirmJobSchedule() {

    if ($("#cronTab").hasClass("active")) {
        if (!validCronTab()) return;

    }

    $("#expression").val();
    $("#expressionType").val();
    $("#jobSchedule").val();

    $("#jobScheduleModal").modal("hide");
}

