var testNum = /^[0-9]*$/;

//var alarmType = null;
$(function () {
    //初始化报警类型
    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/api/alarm/getAlarmType", function (data) {
        $(data).each(function (i, c) {
            var input = $('<input name="alarmType" type="checkbox" value="' + c.id + '" />');
            $("#alarmType").append(input);
            $("#alarmType").append(c.text + "&nbsp;&nbsp;");
        });
    });
    $.ajaxSettings.async = true;

    //初始化内网所有用户
    generateUsers();

    //初始化作业类型内容
    $.getJSON(contextPath + "/assets/json/jobType.json", function (data) {
        var newData = new Array();
        $(data).each(function (i, c) {
            if (this.id != 'all') {
                newData.push(this);
            }
        });

        $("#jobType").select2({
            data: newData,
            width: '100%'
        });
        if (jobType != undefined) {
            $("#jobType").val(jobType).trigger("change");
        }
    });


    $.getJSON(contextPath + "/assets/json/jobPriority.json", function (data) {
        var newData = new Array();
        $(data).each(function (i, c) {
            if (this.id != 'all') {
                newData.push(this);
            }
        });

        $("#priority").select2({
            data: newData,
            width: '100%'
        });
        if (jobPriority != undefined) {
            $("#priority").val(jobPriority).trigger("change");
        }
    });


    //初始化表达式类型
    $.getJSON(contextPath + "/api/job/getExpressionType", function (data) {
        var newData = new Array();
        $(data).each(function (i, c) {
            if (this.id != 'all') {
                newData.push(this);
            }
        });

        $("#expressionType").select2({
            data: newData,
            width: '100%'
        });
        if (expressionType != undefined) {
            $("#expressionType").val(expressionType).trigger("change");
        }
    });


    //select采用select2 实现
    $(".input-group select[name!=commonStrategy][name!=alarm-type]").select2({width: '100%'});


    $('#activeStartTime').datetimepicker({
        language: 'zh-CN',
        minView: 'month',
        format: 'yyyy-mm-dd',
        autoclose: true
    });

    $('#activeEndTime').datetimepicker({
        language: 'zh-CN',
        minView: 'month',
        format: 'yyyy-mm-dd',
        autoclose: true
    });

    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/api/job/getCommonStrategy", function (data) {
        var newData = new Array();
        $(data).each(function (i, c) {
            if (this.id != 'all') {
                newData.push(this);
            }
        });


        $(newData).each(function (i, c) {
            var option = $("<option></option>");
            option.attr("value", c.id);
            option.text(c.text);
            $("#strategyPattern select[name=commonStrategy]").append(option);
        });
    });
    $.ajaxSettings.async = true;

    $("#dependJobIds").on("change", function () {
        generateStrategy();
    });

    if (dependIds != undefined && dependIds != '[]') {
        $("#dependJobIds").val(JSON.parse(dependIds)).trigger("change");
    }
});

//初始化内网用户
function generateUsers() {
    $.ajax({
        url: contextPath + '/api/job/getAllUser',
        type: 'POST',
        data: {},
        success: function (data) {
            var selectData = new Array();

            $(data).each(function (i, c) {
                var user = {};
                var id = c.nickname;
                var text = c.nickname;
                user["id"] = id;
                user["text"] = text;
                selectData.push(user);
            });
            //生成select2的选择框
            $("#alarm").select2({
                data: selectData,
                width: '100%'
            });

            if (undefined != existAlarmList) {
                //构造已经存在的报警人的数据
                var selectAlarm = new Array();
                var existAlarmListJson = JSON.parse(existAlarmList);
                $(existAlarmListJson).each(function (i, c) {
                    selectAlarm.push(c.receiver);
                });
                //设置已经存在的报警人得选项
                $("#alarm").val(selectAlarm).trigger("change");
            }
        }
    });
}


function changeAll(thisTag) {
    var thisTagValue = thisTag.checked;
    var siblings = $(thisTag).siblings();
    $(siblings).each(function (i, c) {
        c.checked = thisTagValue;
    });
}


//修改报警人触发
function changeAlarm(e) {
    var chosenUser = $("#alarm").val();
    var existAlarms = new Array();
    if (existAlarmList != undefined) {
        existAlarms = JSON.parse(existAlarmList);
    }
    var alarmUserJson = {};
    //数组构造成json，方便后续获取某个用户报警信息
    $(existAlarms).each(function (i, c) {
        alarmUserJson[c.receiver] = c;
    });

    //先保存原来的报警信息
    var tempSelect = $("#alarmList").find("select");
    $(tempSelect).each(function (i, c) {
        var value = $(c).val();
        $(c).find('option[value=' + value + ']').attr("selected", "selected");
    });
    var storage = $("#alarmList").clone();


    $("#alarmList").empty();
    $(chosenUser).each(function (i, c) {
        var alarmUser = alarmUserJson[c];
        var div;
        if (storage.find('div.row[nick=' + c + ']').length > 0) {
            div = storage.find('div.row[nick=' + c + ']');
        }
        else {
            div = $("#alarmPattern").children().clone();
            $(div).find("span[name=alarm-user]").text(c);
            $(div).attr("nick", c);

            $(alarmType).each(function (i, c) {
                var option = $("<option></option>");
                option.attr("value", c.id);
                option.text(c.text);

                if (alarmUserJson[c] != null && alarmUserJson[c].alarmType == c.id) {
                    option.attr("selected", selected);
                }

                $(div).find("select[name=alarm-type]").append(option);
            });
        }

        $("#alarmList").append(div);
    });
}

function addAlarm(thisTag) {
    var self = $(thisTag).closest(".row").clone();
    $($(thisTag).closest(".row")).after(self);

}
function deleteAlarm(thisTag) {
    $(thisTag).closest(".row").remove();
}


//生成策略表单
function generateStrategy() {
    var dependIds = $("#dependJobIds").val();
    var dds = {};

    //暂存
    $("#strategyList>dd").each(function (i, c) {
        var jobId = $(c).attr("value");
        var tempSelect = $(c).find("select");
        $(tempSelect).each(function (i, c) {
            var value = $(c).val();
            $(c).find('option[value=' + value + ']').attr("selected", "selected");
        });


        var dd = $(c).clone();
        dds[jobId] = dd;
    });
    //清空
    $("#strategyList").empty();
    var dependJobJson = {};
    if (dependJobs != undefined && dependJobs != '') {
        dependJobJson = JSON.parse(dependJobs);
    }
    //重新生成
    $(dependIds).each(function (i, c) {
        var dd = dds[c];
        if (dd != undefined) {
            $("#strategyList").append(dd);
        }
        else {
            dd = $("<dd value=\'" + c + "\'></dd>");
            var pattern = $($("#strategyPattern").html()).clone();

            var jobName = $("#dependJobIds").find("option[value=" + c + "]").text();
            $(pattern).find("span[name=dependJob]").text(jobName);

            var dependJob = dependJobJson[c];
            if (dependJob != undefined) {
                $(pattern).find("select[name=commonStrategy] option[value=" + dependJob.commonStrategy + "]").attr("selected", "selected");
                $(pattern).find("input[name=offsetStrategy]").val(dependJob.offsetStrategy);
            }

            $(dd).append(pattern);
            $("#strategyList").append(dd);
        }
    });

}


function changeTextArea(thisTag, rows, cols) {
    $(thisTag).prop("rows", rows);
    $(thisTag).prop("cols", cols);
}

//重置参数
function reset() {
    var inputs = $("#jobData .input-group>input,#jobData .input-group>textarea");
    var selects = $("#jobData .input-group>select");
    $(inputs).each(function (i, c) {
        $(c).val("");
    });
    $(selects).each(function (i, c) {
        if ($(c).find("option").length >= 1) {
            var value = $($(c).find("option")[0]).val();
            $(c).val(value).trigger("change");
        }
    });
    $("#dependJobIds").val(null).trigger("change");
}


//校验某些属性是否为空
function checkEmpty(ids) {
    var flag = true;
    $(ids).each(function (i, c) {
        var value = $("#" + c).val();
        if (value == undefined || value == '') {
            flag = false;
            var desc = $("#" + c).attr("desc");
            new PNotify({
                title: '提交任务',
                text: desc + '不能为空:' + c,
                type: 'warning',
                icon: true,
                styling: 'bootstrap3'
            });

            return false;
        }
    });
    return flag;
}


//获取配置的报警信息
function getAlarm() {
    var jobId = $("#jobId").val();
    var receiver = $("#alarm").val();  //报警人

    if (null == receiver) {
        return null;
    }

    var inputs = $("#alarmType input[name=alarmType]:checked");
    if (inputs.length <= 0) {
        return null;
    }

    var alarmType = new Array();
    $(inputs).each(function (i, c) {
        var type = $(c).val();
        alarmType.push(parseInt(type));
    });
    var status = $("#alarmStatus input[name=alarmStatus]:checked").val();

    var data = {};
    data["jobId"] = parseInt(jobId);
    data["alarmType"] = alarmType;
    data["receiver"] = receiver;
    data["status"] = parseInt(status);

    return data;
}

//计算表达式
function getScheduleExpressionEntry() {
    var scheduleExpressionEntry = {};

    var newExpressionType = $("#expressionType").val();
    var newExpression = $("#expression").val();


    if ((expressionType == undefined || expressionType == '') && newExpressionType != null && newExpression != '') {
        var operatorMode = 1;
        scheduleExpressionEntry["expressionType"] = newExpressionType;
        scheduleExpressionEntry["expression"] = newExpression;
        scheduleExpressionEntry["operatorMode"] = operatorMode;
    }
    if ((expressionType != undefined && expressionType != '') && newExpressionType != null && newExpression != '') {
        var operatorMode = 2;
        scheduleExpressionEntry["expressionType"] = newExpressionType;
        scheduleExpressionEntry["expression"] = newExpression;
        scheduleExpressionEntry["operatorMode"] = operatorMode;
    }
    if ((expressionType != undefined && expressionType != '') && (newExpressionType == null || newExpression == '')) {
        var operatorMode = 3;
        scheduleExpressionEntry["expressionType"] = newExpressionType;
        scheduleExpressionEntry["expression"] = newExpression;
        scheduleExpressionEntry["operatorMode"] = operatorMode;
    }


    return scheduleExpressionEntry;
}


//根据原始依赖与新依赖确定操作类型
function calculateOperator(sourceStr, afterChangeStr) {
    var dependencyList = new Array();
    var myDependIds = {};
    if (sourceStr != null) {
        //console.log("sourceStr:"+sourceStr);
        var source = JSON.parse(sourceStr);
        console.log(afterChangeStr);
        var afterChange = JSON.parse(afterChangeStr);

        if (afterChange == null) {
            for (var i = 0; i < source.length; i++) {
                myDependIds[source[i]] = "delete";
            }
        }
        else {
            for (var i = 0; i < source.length; i++) {
                myDependIds[source[i]] = "";
            }
            for (var i = 0; i < afterChange.length; i++) {
                myDependIds[afterChange[i]] = "";
            }

            for (var key in myDependIds) {
                var operator = 1;
                var source_flag = false;
                var afterChange_flag = false;
                for (var i = 0; i < source.length; i++) {
                    if (source[i] == key) {
                        source_flag = true;
                        break;
                    }
                }
                for (var i = 0; i < afterChange.length; i++) {
                    if (afterChange[i] == key) {
                        afterChange_flag = true;
                        break;
                    }
                }
                if (source_flag == true && afterChange_flag == true) {
                    operator = 2;
                }
                if (source_flag == true && afterChange_flag == false) {
                    operator = 3;
                }
                if (source_flag == false && afterChange_flag == true) {
                    operator = 1;
                }

                var dependency = {};
                dependency["preJobId"] = key;
                dependency["operatorMode"] = operator;
                dependency["commonStrategy"] = $("#strategyList dd[value=" + key + "]").find("select[name=commonStrategy]").val();
                dependency["offsetStrategy"] = $("#strategyList dd[value=" + key + "]").find("input[name=offsetStrategy]").val();

                dependencyList.push(dependency);
            }
        }
    }
    else {
        var dds = $("#strategyList dd");
        $(dds).each(function (i, c) {
            var preJobId = $(c).attr("value");
            var commonStrategy = $(c).find("select[name=commonStrategy]").val();
            var offsetStrategy = $(c).find("input[name=offsetStrategy]").val();
            var operatorMode = 1;

            var dependency = {};
            dependency["preJobId"] = preJobId;
            dependency["operatorMode"] = operatorMode;
            dependency["commonStrategy"] = commonStrategy;
            dependency["offsetStrategy"] = offsetStrategy;

            dependencyList.push(dependency);
        });
    }

    //console.log("result:"+JSON.stringify(myDependIds));
    return dependencyList;
}

//提交任务
function submit() {
    var ids = ["jobName", "jobType", "content", "workerGroupId"];
    var flag = checkEmpty(ids);
    if (flag == false) {
        return;
    }

    flag = checkJobName($("#jobName"));
    if (flag == false) {
        return;
    }
    flag = checkActiveDate();
    if (flag == false) {
        return;
    }

    var data = getData();
    var resultFlag = requestRemoteRestApi("/api/job/submit", "新增任务", data);

}

//获取参数
function getData() {
    var result = {};
    var inputs = $("#jobData .input-group>input,#jobData .input-group>textarea");
    var selects = $("#jobData .input-group>select");
    $(inputs).each(function (i, c) {
        var id = $(c).prop("id");
        if (id == null || id == '') {
            return;
        }
        var value = $(c).val();

        if (value != '' && testNum.test(value)) {
            value = parseInt(value);
        }
        if (id == 'parameters') {
            value = JSON.parse(value);
        }
        if (id == 'activeStartTime' || id == 'activeEndTime') {
            if (value != '') {
                value = (new Date(value)).getTime();
            }
            else {
                value = 0;
            }
        }

        result[id] = value

    });
    $(selects).each(function (i, c) {
        if ($(c).attr("id") == 'dependJobIds' || $(c).attr("id") == 'alarm' || $(c).attr("name") == 'commonStrategy') {
            return;
        }

        var id = $(c).prop("id");
        var value = $(c).val();

        if (value != '' && testNum.test(value)) {
            value = parseInt(value);
        }
        result[id] = value;

    });
    //前置任务信息
    var afterDependIds = result["dependJobIds"];
    afterDependIds = afterDependIds == undefined ? "[]" : afterDependIds;

    var dependencyList = calculateOperator(dependIds, afterDependIds);
    result["dependencyList"] = dependencyList;

    //表示式类型与表达式内容
    var scheduleExpressionEntry = getScheduleExpressionEntry();
    result["scheduleExpressionEntry"] = scheduleExpressionEntry;

    var appId = $("#appId").val();
    result["appId"] = appId;

    return result;
}


//编辑任务
function edit() {
    var ids = ["user", "jobId", "jobName", "jobType", "content", "groupId"];
    var flag = checkEmpty(ids);
    if (flag == false) {
        return;
    }

    flag = checkJobName($("#jobName"));
    if (flag == false) {
        return;
    }
    flag = checkActiveDate();
    if (flag == false) {
        return;
    }

    var data = getData();

    var jobId = $("#jobId").val();
    data["jobId"] = jobId;

    var resultFlag = requestRemoteRestApi('/api/job/edit', "编辑任务", data);
}

//检查任务名是否重复
function checkJobName(thisTag) {
    var jobId = $("#jobId").val();
    var jobName = $("#jobName").val();
    var flag = true;
    if (jobName == '') {
        new PNotify({
            title: '提交任务',
            text: '任务名称不能为空,请先填写',
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        $(thisTag).focus();
        return;
    }


    $.ajax({
        url: contextPath + '/job/checkJobName',
        type: 'POST',
        async: false,
        data: {jobId: jobId, jobName: jobName},
        success: function (data) {
            if (data.code == 1) {
                new PNotify({
                    title: '提交任务',
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
                flag = false;
                $(thisTag).focus();
            }
        }
    });

    return flag;
}
//检查是否数字
function checkNum(thisTag) {
    if ($(thisTag).val() == '') {
        return;
    }

    var flag = testNum.test($(thisTag).val());
    if (flag == false) {
        new PNotify({
            title: '提交任务',
            text: $(thisTag).attr("desc") + "必须为数字,请修改！！！",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });
        $(thisTag).focus();
    }
}
//检查结束日期是否小于开始日期
function checkActiveDate() {
    var startTime = $("#startTime").val();
    var endTime = $("#endTime").val();

    var flag = true;
    if (startTime != '' && endTime != '') {
        var start = new Date(startTime);
        var end = new Date(endTime);
        if (end <= start) {
            new PNotify({
                title: '提交任务',
                text: "结束日期不能小于等于开始日期",
                type: 'warning',
                icon: true,
                styling: 'bootstrap3'
            });
            flag = false;
        }
    }
    return flag;
}

function showParaModel() {
    $("#paras tbody").empty();
    var parameters = $("#parameters").val();
    if (parameters != null && parameters != '' && parameters.indexOf("}") > 0) {
        var existParas = JSON.parse(parameters);
        for (var key in existParas) {
            var value = existParas[key];
            var tr = $("#pattern tr").clone();
            $($(tr).find("input[name=key]").first()).val(key);
            $($(tr).find("input[name=value]").first()).val(value);
            $("#paras tbody").append(tr);
        }
    }

    $("#paraModal").modal("show");
}


var testChinese = /[\u4E00-\u9FA5]/;

function ensurePara() {
    var trs = $("#paras tbody tr");
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
        new PNotify({
            title: '修改参数',
            text: "key不能为空,且不能为中文,请修改",
            type: 'warning',
            icon: true,
            styling: 'bootstrap3'
        });

        return;
    }


    $("#parameters").val(JSON.stringify(paras));
    $("#paraModal").modal("hide");
}

function addPara(thisTag) {
    var tr = $("#pattern tr").clone();
    if (thisTag == null) {
        $("#paras tbody").append(tr);
    }
    else {
        $(thisTag).parent().parent().after(tr);
    }
}
function deletePara(thisTag) {
    $(thisTag).parent().parent().remove();
}


function showDescription(thisTag) {
    var options = {};

    var content = "通用策略:<br/>表示对前置任务的所有执行依赖策略<br/><br/>";
    content += "偏移策略:<br/>";
    content += "c:current,d:天,";
    content += "m:分钟,";
    content += "h:小时,";
    content += "w:周,";
    content += "M:月,";
    content += "y:年<br/>";
    content += "例如:<br/>按天偏移:cd,d(-n),d(n),d(-n,n),n>0<br/>";
    content += "按周偏移:cw,w(-n),w(n),w(-n,n),n>0<br/>";


    options["content"] = content;
    options["template"] = '<div class="popover" role="tooltip" style="width:100%"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>';
    options["animation"] = true;
    options["placement"] = "right";

    //console.log(options);

    $(thisTag).popover(options);
    $(thisTag).popover('show');
    $(".popover-content").html(content);
}

function hideDescription(thisTag) {
    $(thisTag).popover('hide');
}