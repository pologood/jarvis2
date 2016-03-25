var testNum = /^[0-9]*$/;
var testChinese = /[\u4E00-\u9FA5]/;
var job = null;
var existAlarmList = undefined;
var dependJobs = {};
var dependIds = [];
var expressionId = null;

var contentTypePreviousValue = null;    //前一个_内容类型RadioButton
var jobContentBuffer = {};              //jobContent缓冲

var pageJobEdit = {curPerHour: 0};              //缓冲

$(function () {

    $("#jobType").on('change', function (e) {
        changeJobType();
    });
    $("input[name='contentType']").change(function (e) {
        changeContentType($(this));
    });
    $("#dependJobIds").on("change", function () {  //绑定修改依赖的事件
        generateStrategy();
    });

    formatDateTimePicker();         //格式化-时间选择器
    initJobData();

    //job标签页
    initJobType();              //初始化作业类型
    initContentType();          //初始化内容类型
    initJobParams();
    initJobPriority();          //初始化优先级
    initExpressionType();       //初始化表达式类型
    initWorkerGroup();          //初始化workGroup
    initBizGroupName();         //初始化业务标签

    //依赖标签页
    initCommonStrategy();       //初始化通用策略
    initDependJobs();           //初始化所有依赖job

    //报警标签页
    generateAlarmUsers();       //初始化报警的内网所有用户,并初始化报警信息

});

//初始化时间选择器
function formatDateTimePicker() {
    var ids = ["activeStartDate", "activeEndDate"];
    $(ids).each(function (i, c) {
        $('#' + c).datetimepicker({
            language: 'zh-CN',
            minView: 'month',
            format: 'yyyy-mm-dd',
            autoclose: true
        });
    });
}

//初始化job数据
function initJobData() {
    if (null != jobId && '' != jobId) {
        $.ajax({
            url: contextPath + '/api/job/getById',
            data: {jobId: jobId},
            async: false,
            success: function (data) {
                if (data.code != CONST.MSG_CODE.SUCCESS) {
                    alert("获取job数据出错(jobId=" + jobId + ").\n" + data.msg);
                    jobId = null;
                    return;
                }

                job = data.data;
                $("#jobName").val(job.jobName);
                $("#department").val(job.department);
                var startDate = job.activeStartDate;
                if (startDate != null && startDate != CONST.JOB_ACTIVE_DATE.MIN_DATE) {
                    $("#activeStartDate").val(moment(startDate).format("YYYY-MM-DD"));
                }
                var endDate = job.activeEndDate;
                if (endDate != null && endDate != CONST.JOB_ACTIVE_DATE.MAX_DATE) {
                    $("#activeEndDate").val(moment(endDate).format("YYYY-MM-DD"));
                }

                if (job.contentType == CONST.CONTENT_TYPE.SCRIPT) {
                    $("#scriptId").val(job.scriptId);
                    $("#scriptTitle").val(job.scriptTitle);
                    $("#jobContent").val(job.scriptContent);
                } else {
                    $("#jobContent").val(job.content);
                }
                jobContentBuffer[job.contentType] = $("#jobContent").val();

                $("#expression").val(job.expression);
                $("#failedAttempts").val(job.failedAttempts);
                $("#failedInterval").val(job.failedInterval);
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '获取任务信息', msg);
            }
        })

    }
}

//初始化-job类型
function initJobType() {
    //console.log("initJobType");
    var newData = [];
    var object = CONST.JOB_TYPE;
    for (var prop in object) {
        if (object.hasOwnProperty(prop)) {
            newData.push({id: object[prop], text: object[prop]});
        }
    }

    var jobTypeSelector = $("#jobType");
    $(jobTypeSelector).select2({
        data: newData,
        width: '100%'
    });
    var cur = job != null ? job.jobType : 'hive';
    $(jobTypeSelector).val(cur).trigger("change");

    console.log("initJobType end......");

}

(function ($) {
    $.fn.radioEnable = function (enable) {
        if (enable) {
            $(this).removeAttr("disabled").parent("label").removeClass("disabled");
        } else {
            $(this).attr("disabled", true).parent("label").addClass("disabled");
        }
    };
})(jQuery);


//改变-job类型
function changeJobType() {
    console.log("changeJobType");

    var data = $("#jobType").select2('data')[0];
    var curJobType = data.id;
    var text = $("#contentTypeText");
    var script = $("#contentTypeScript");
    var jar = ("#contentTypeJar");

    if (curJobType == CONST.JOB_TYPE.HIVE || curJobType == CONST.JOB_TYPE.SHELL) {
        $(text).radioEnable(true);
        $(script).radioEnable(true);
        $(jar).radioEnable(false);
        //if (!$(text).prop('checked') && !$(script).prop('checked')) {
        $(text).prop('checked', true).trigger("change");
        //}
    } else if (curJobType == CONST.JOB_TYPE.JAVA || curJobType == CONST.JOB_TYPE.MAPREDUCE) {
        $(text).radioEnable(false);
        $(script).radioEnable(false);
        $(jar).radioEnable(true);
        if (!$(jar).prop('checked')) {
            $(jar).prop('checked', true).trigger("change");
        }
    } else if (curJobType == CONST.JOB_TYPE.SPARK_LAUNCHER) {
        $(text).radioEnable(true);
        $(script).radioEnable(false);
        $(jar).radioEnable(false);
        //if (!$(text).prop('checked')) {
        $(text).prop('checked', true).trigger("change");
        //}
    } else if (curJobType == CONST.JOB_TYPE.DUMMY) {
        $(text).radioEnable(true);
        $(script).radioEnable(true);
        $(jar).radioEnable(true);
    }

    console.log("changeJobType end !!!!!!!!!!!");

}

//初始化-内容类型
function initContentType() {
    console.log("initContentType");
    var cur = job != null ? job.contentType : 1;
    var radio;
    if (cur == CONST.CONTENT_TYPE.TEXT) {
        radio = $("#contentTypeText");
    } else if (cur == CONST.CONTENT_TYPE.SCRIPT) {
        radio = $("#contentTypeScript");
    } else if (cur == CONST.CONTENT_TYPE.JAR) {
        radio = $("#contentTypeJar");
    } else if (cur == CONST.CONTENT_TYPE.EMPTY) {
        radio = $("#contentTypeEmpty")
    } else {
        return;
    }

    radio.prop('checked', true).trigger("change");

    console.log("initContentType end **************");

}


//改变-内容类型
function changeContentType(curRadio) {
    console.log("changeContentType " + curRadio.val());

    var curValue = $(curRadio).val();
    var jobType = $("#jobType").val();
    if (curValue == CONST.CONTENT_TYPE.TEXT) {    //文本
        $("#scriptItemDiv").hide();
        $("#jarItemDiv").hide();
        if (jobType == CONST.JOB_TYPE.SPARK_LAUNCHER) {
            $("#jobContent").attr("readonly", "readonly");
        } else {
            $("#jobContent").removeAttr("readonly");
        }
    } else if (curValue == CONST.CONTENT_TYPE.SCRIPT) {  //脚本
        $("#scriptItemDiv").show();
        $("#jarItemDiv").hide();
        $("#jobContent").attr("readonly", "readonly");
    } else if (curValue == CONST.CONTENT_TYPE.JAR) {  //jar
        $("#scriptItemDiv").hide();
        $("#jarItemDiv").show();
        $("#jobContent").removeAttr("readonly");
    } else if (curValue == CONST.CONTENT_TYPE.EMPTY) {    //dummy 情况
        $("#scriptItemDiv").hide();
        $("#jarItemDiv").hide();
        $("#jobContent").attr("readonly", "readonly");
    }

    //老文本保存
    if (contentTypePreviousValue != null) {
        jobContentBuffer[contentTypePreviousValue] = $("#jobContent").val();
    }

    //新文本导出
    if (curValue in jobContentBuffer) {
        $("#jobContent").val(jobContentBuffer[curValue]);
    } else {
        $("#jobContent").val("");
    }
    contentTypePreviousValue = curValue;
    console.log("changeContentType " + curValue + "end ^^^^^^^^^^^^^^^^");
}

//修改textarea大小
function changeTextArea(thisTag, rows, cols) {
    $(thisTag).prop("rows", rows);
    $(thisTag).prop("cols", cols);
}

//初始化-job参数
function initJobParams() {
    if (job != null) {
        var paramsArray = JSON.parse(job.params);
        //var key;
        //key = CONST.JOB_PARAMS_KEY.JAR_URL;
        //if (key in paramsArray) {
        //    $("#jarUrl").val(paramsArray[key]);
        //    delete paramsArray[key];
        //}
        $("#params").val(JSON.stringify(paramsArray));
    }
}

//初始化-job权重
function initJobPriority() {
    $.ajax({
        url: contextPath + "/assets/json/jobPriority.json",
        success: function (data) {
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
            if (null != job) {
                $("#priority").val(job.priority).trigger("change");
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化权重信息', msg);
        }
    })

}

//初始化-表达式类型
function initExpressionType() {
    $.ajax({
        url: contextPath + "/api/job/getExpressionType",
        success: function (data) {
            var newData = new Array();
            var no = {};
            no["id"] = "no";
            no["text"] = "无";
            newData.push(no);

            $(data).each(function (i, c) {
                newData.push(c);
            });

            $("#expressionType").select2({
                data: newData,
                width: '100%'
            });

            $("#expressionType").on("select2:select", function (e) {
                if ("no" == $("#expressionType").val()) {
                    $("#expression").val("");
                    $("#expression").attr("disabled", "disabled");
                }
                else {
                    $("#expression").removeAttr("disabled");
                    $("#expression").val("");
                }
            })
            if (job != null) {
                expressionId = job.expressionId;
                if (expressionId == null) {
                    $("#expressionType").val("no").trigger("change");
                    //$("#expression").attr("disabled", "disabled");
                }
                else {
                    $("#expressionType").val(job.expressionType).trigger("change");
                }
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化表达式类型', msg);
        }
    })

}


//初始化-业务类型
function initBizGroupName() {
    $.ajax({
        url: contextPath + "/api/bizGroup/getAllByCondition",
        data: {status: 1},
        success: function (data) {
            if (data.code == 1000) {
                var newData = new Array();
                $(data.data).each(function (i, c) {
                    var item = {};
                    item["id"] = c.id;
                    item["text"] = c.name;
                    newData.push(item);
                });

                $("#bizGroups").select2({
                    data: newData,
                    width: '100%'
                });
                if (job != null && job.bizGroups != "") {
                    $("#bizGroups").val(job.bizGroups.split(",")).trigger("change");
                }
            }
            else {
                new PNotify({
                    title: '获取业务标签类型',
                    text: data.msg,
                    type: 'error',
                    icon: true,
                    styling: 'bootstrap3'
                });
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化业务组信息', msg);
        }
    });
}

//初始化-WorkerGroup
function initWorkerGroup() {
    $.ajax({
        url: contextPath + "/api/workerGroup/getByAppId",
        data: {appId: appId},
        success: function (data) {
            var newData = new Array();
            $(data.rows).each(function (i, c) {
                var item = {};
                item["id"] = c.id;
                item["text"] = c.name;
                newData.push(item);
            });
            $("#workerGroupId").select2({
                data: newData,
                width: '100%'
            });

            if (null != job) {
                $("#workerGroupId").val(job.workerGroupId).trigger("change");
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化workerGroup', msg);
        }
    })
}

//高级参数-显示或隐藏
function AdvancedOptionsToggle(thisTag) {
    var toState = $(thisTag).find("i").attr("toState");
    var classStyle = "pull-right text-primary glyphicon glyphicon-chevron-" + toState;
    $(thisTag).find("i").attr("class", classStyle);
    toState = toState == "up" ? "down" : "up";
    $(thisTag).find("i").attr("toState", toState);
    $("#other").toggle();
}

//校验某些属性是否为空
function checkEmptyByIds(ids) {
    var flag = true;
    $(ids).each(function (i, c) {
        var value = $("#" + c).val();
        if (value != null) {
            value = value.trim();
        }
        if (value == undefined || value == '' || value == null) {
            flag = false;
            var desc = $("#" + c).attr("desc");
            showMsg('warning', '提交任务', desc + '不能为空');
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

//检查任务名是否重复
function checkJobName(thisTag) {
    var jobName = $(thisTag).val();
    var flag = true;
    if (jobName == '') {
        showMsg('warning', '提交任务', '任务名称不能为空,请先填写');
        return;
    }
    $.ajax({
        url: contextPath + '/job/checkJobName',
        type: 'POST',
        async: false,
        data: {jobId: jobId, jobName: jobName},
        success: function (data) {
            if (CONST.MSG_CODE.SUCCESS !== data.code) {
                showMsg('warning', '提交任务', data.msg);
                flag = false;
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '检查任务名', msg);
        }
    });
    return flag;
}

//检查-内容类型
function checkContentTypeAndContent() {

    var contentType = $("input[name='contentType']:checked").val();
    if (contentType == undefined) {
        showMsg('warning', '提交任务', "内容类型为空,请选择内容类型");
        return false;
    }
    if (contentType == CONST.CONTENT_TYPE.SCRIPT) { //脚本类型
        if ($("#scriptId").val() == '') {
            showMsg('warning', '提交任务', "脚本内容为空,请选择脚本.");
            return false;
        }
    }
    //if (contentType == CONST.CONTENT_TYPE.JAR) { //jar包类型
    //    if ($("#uploadJarInput")[0].files.length == 0) {
    //        showMsg('warning', '提交任务', "jar文件未选择,请选择jar文件");
    //        return false;
    //    }
    //}

    if (contentType == CONST.CONTENT_TYPE.EMPTY) { //空
        showMsg('warning', '提交任务', "内容类型未选择,请选择");
    }

    if (!checkEmptyByIds(["jobContent"])) {
        return false;
    }
    return true;
}

//检查有效日期 (结束日期是否小于开始日期)
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

//计算表达式
function getScheduleExpressionList() {
    var scheduleExpressionList = [];
    var operatorMode = null;
    var newExpressionType = $("#expressionType").val();
    newExpressionType = (newExpressionType == "no") ? null : newExpressionType;
    var newExpression = $("#expression").val();
    if (expressionId == null) {
        if (newExpressionType != null && newExpression != '') {
            operatorMode = CONST.OPERATE_MODE.ADD;
        }
    } else {
        if (newExpressionType == null || newExpression == '') {
            operatorMode = CONST.OPERATE_MODE.DELETE;
        } else if (job.expressionType != newExpressionType || job.expression != newExpression) {
            operatorMode = CONST.OPERATE_MODE.EDIT;
        }
    }
    if (operatorMode != null) {
        var entry = {
            "operatorMode": operatorMode,
            "expressionId": expressionId,
            "expressionType": newExpressionType,
            "expression": newExpression
        };
        scheduleExpressionList.push(entry);
    }
    return scheduleExpressionList;
}


//获取job数据
function getJobDataFromPage() {
    var result = {};
    var inputs = $("#baseInfo .input-group>input,#baseInfo .input-group>textarea");
    var selects = $("#baseInfo .input-group>select");
    $(inputs).each(function (i, c) {
        if ($(c).prop("name") == 'contentType') {
            return;
        }
        var id = $(c).prop("id");
        if (id == null || id == '') {
            return;
        }
        var value = $(c).val();
        if (typeof value == 'string') {
            value = value.trim();
        }

        if (value != '' && testNum.test(value)) {
            value = parseInt(value);
        }

        if (id == 'activeStartDate') {
            if (value != '') {
                value = (new Date(value + " 00:00:00")).getTime();
            }
            else {
                value = 0;
            }
        }

        if (id == 'activeEndDate') {
            if (value != '') {
                value = (new Date(value + " 23:59:59")).getTime();
            }
            else {
                value = 0;
            }
        }


        //jobContent的名称转换.
        if (id == 'jobContent') {
            id = 'content';
        }
        result[id] = value;

    });
    $(selects).each(function (i, c) {
        if ($(c).attr("id") == 'dependJobIds' || $(c).attr("id") == 'alarm' || $(c).attr("name") == 'commonStrategy') {
            return;
        }

        var id = $(c).prop("id");
        var value = $(c).val();

        if ($(this).prop("multiple")) { //多选框
            result[id] = value == null ? "" : value.join(",");
            if (id == "bizGroups") {
                result[id] = result[id] != "" ? result[id] : "," + result[id] + ",";
            }
        } else {
            if (value != '' && testNum.test(value)) {
                value = parseInt(value);
            }
            result[id] = value;
        }
    });

    var contentType = $("input[name='contentType']:checked").val();
    result['contentType'] = contentType;
    //if (contentType == CONST.CONTENT_TYPE.JAR) {
    //    var paramsArray = JSON.parse(result['params']);
    //    paramsArray[CONST.JOB_PARAMS_KEY.JAR_URL] = $('#jarUrl').val();
    //    result['params'] = JSON.stringify(paramsArray);
    //}

    if (contentType == CONST.CONTENT_TYPE.SCRIPT) {
        result['content'] = result['scriptId'];
    }

    //表达式类型与表达式内容
    result["scheduleExpressionList"] = getScheduleExpressionList();
    var appId = $("#appId").val();
    result["appId"] = appId;
    return result;
}


//保存job
function saveJob() {
    //必填的参数
    var ids = ["jobName", "jobType", "workerGroupId"];
    if (!checkEmptyByIds(ids) || !checkContentTypeAndContent()
        || !checkJobName($("#jobName")) || !checkActiveDate()) {
        return;
    }

    if (!uploadJarFile()) {
        return;
    }

    var data = getJobDataFromPage();
    if (null != jobId && '' != jobId) {
        data["jobId"] = jobId;
        var response1 = requestRemoteRestApi("/api/job/edit", "编辑任务", data, false, true);

        var flag2 = true;
        if (data["scheduleExpressionList"].length > 0) {
            var response2 = requestRemoteRestApi("/api/job/scheduleExp/set", "修改表达式", data, false, true);
            flag2 = response2.flag;
        }

        if (response1.flag && flag2) {
            window.setTimeout(function () {
                window.location.reload();
            }, 500);
        }

    }
    else {
        var response = requestRemoteRestApi("/api/job/submit", "新增任务", data, false, true);
        if (response.flag == true) {
            window.setTimeout(function () {
                window.location.href = window.location.href + "?jobId=" + response.data.data.jobId;
            }, 1000);
        }
    }
}
//重置job
function resetJob() {
    var ids = ["jobName", "activeStartDate", "activeEndDate", "jobContent", "params", "expression", "failedAttempts", "failedInterval"];
    var selectIds = ["jobType", "workerGroupId", "expressionType", "priority"];

    $(ids).each(function (i, c) {
        var defaultValue = $("#" + c).attr("defaultValue");
        $("#" + c).val(defaultValue);
    });
    $(selectIds).each(function (i, c) {
        $("#" + c).val(null).trigger("change");
    });
}

//------------------------------  2.job依赖相关 ----------------------------------

//初始化通用策略
function initCommonStrategy() {
    $.ajax({
        url: contextPath + "/api/job/getCommonStrategy",
        success: function (data) {
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
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化通用策略', msg);
        }
    });
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

            var dependJob = dependJobs[c];
            if (dependJob != undefined) {
                $(pattern).find("select[name=commonStrategy] option[value=" + dependJob.commonStrategy + "]").attr("selected", "selected");
                $(pattern).find("input[name=offsetStrategy]").val(dependJob.offsetStrategy);
            }

            $(dd).append(pattern);
            $("#strategyList").append(dd);
        }
    });
}

//初始化依赖任务，如果是编辑则初始化已经依赖job
function initDependJobs() {
    $.ajax({
        url: contextPath + "/api/job/getAllJobIdAndName",
        success: function (data) {
            var newData = new Array();
            $(data).each(function (i, c) {
                var item = {};
                item["id"] = c.jobId;
                item["text"] = c.jobName;
                newData.push(item);
            });
            $("#dependJobIds").select2({
                data: newData,
                width: '100%'
            });
            if (null != jobId && '' != jobId) {
                $.ajax({
                    url: contextPath + "/api/job/getParentsById",
                    async: false,
                    data: {jobId: jobId},
                    success: function (data) {
                        var newData = new Array();
                        $(data.data).each(function (i, c) {
                            dependJobs[c.jobId] = c;
                            newData.push(c.jobId);
                            dependIds.push(c.jobId);
                        });

                        $("#dependJobIds").val(newData).trigger("change");
                    },
                    error: function (jqXHR, exception) {
                        var msg = getMsg4ajaxError(jqXHR, exception);
                        showMsg('warning', '获取父任务', msg);
                    }
                })
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化依赖', msg);
        }
    });
}

//显示说明
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

//隐藏说明
function hideDescription(thisTag) {
    $(thisTag).popover('hide');
}

//根据原始依赖与新依赖确定操作类型
function calculateOperator(source, afterChange) {
    var dependencyList = new Array();
    var myDependIds = {};
    if (source != null) {
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
    return dependencyList;
}

//获取依赖任务信息
function getDependData() {
    if (null == jobId) {
        new PNotify({
            title: '保存依赖信息',
            text: '请先保存任务,再添加依赖',
            type: 'info',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }


    //前置任务信息，after代表用户修改后的
    var afterDependIds = $("#dependJobIds").val();
    afterDependIds = afterDependIds == undefined ? [] : afterDependIds;

    var dependencyList = calculateOperator(dependIds, afterDependIds);

    return dependencyList;
}

//保存依赖
function saveDepend() {
    var dependJobData = getDependData();
    if (null == dependJobData) {
        return;
    }
    var data = {};
    data["jobId"] = jobId;
    data["dependencyList"] = dependJobData;

    var response = requestRemoteRestApi("/api/job/dependency/set", "修改依赖信息", data);
}

//重置依赖
function resetDepend() {
    $("#dependJobIds").val(null).trigger("change");
}


//-------------------------------  3.报警设置相关 --------------------------------

//初始化报警用户
function generateAlarmUsers() {
    $.ajax({
        url: contextPath + '/api/common/getAllUser',
        type: 'POST',
        data: {},
        success: function (data) {
            var selectData = new Array();
            $(data.rows).each(function (i, c) {
                var user = {};
                var id = c.nick;
                var text = c.nick;
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

            //初始化报警类型
            initAlarmType();
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '获取内网用户', msg);
        }
    });
}

//初始化报警类型
function initAlarmType() {
    $.ajax({
        url: contextPath + "/api/alarm/getAlarmType",
        success: function (data) {
            $(data).each(function (i, c) {
                var input = $('<input name="alarmType" type="checkbox" value="' + c.id + '" />');
                $("#alarmType").append(input);
                $("#alarmType").append(c.text + "&nbsp;&nbsp;");
            });

            if (null != jobId && '' != jobId) {
                $.getJSON(contextPath + "/api/alarm/getByJobId", {jobId: jobId}, function (data) {
                    //不存在的时候返回的是null,所以需要排除
                    if (data.data.jobId != null) {
                        var alarmType = data.data.alarmType;
                        var receiver = data.data.receiver;
                        var alarmTypes = alarmType.split(",");
                        var status = data.data.status;

                        $("#alarmStatus input[value=" + status + "]").click();

                        var inputs = $("#alarmType input[name=alarmType]");
                        $(inputs).each(function (i, c) {
                            $(alarmTypes).each(function (innerIndex, innerContent) {
                                if ($(c).val() == innerContent) {
                                    $(c).click();
                                    return false;
                                }
                            });
                        });

                        $("#alarm").val(stringToArr(receiver)).trigger("change");
                    }
                });
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化报警类型', msg);
        }
    });
}

//报警类型[全选]
function clickAlarmTypeAll(thisTag) {
    var thisTagValue = thisTag.checked;
    var siblings = $(thisTag).siblings();
    $(siblings).each(function (i, c) {
        c.checked = thisTagValue;
    });
}

//获取报警配置信息
function getAlarmFromPage() {
    var receiver = $("#alarm").val();
    var alarmTypeInputs = $("#alarmType input[name=alarmType]:checked");
    var status = $("#alarmStatus input:checked").val();

    var alarmType = "";
    $(alarmTypeInputs).each(function (i, c) {
        var value = $(c).val();
        if ("" == alarmType) {
            alarmType = value;
        }
        else {
            alarmType += "," + value;
        }
    });
    if (null == receiver) {
        new PNotify({
            title: '保存报警信息',
            text: '接收人必选填写',
            type: 'info',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }
    var newReceiver = "";
    $(receiver).each(function (i, c) {
        if (newReceiver == "") {
            newReceiver = c;
        }
        else {
            newReceiver += "," + c;
        }
    });

    if ("" == alarmType) {
        new PNotify({
            title: '保存报警信息',
            text: '报警类型请至少选择一种',
            type: 'info',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }
    if (null == jobId || "" == jobId) {
        new PNotify({
            title: '保存报警信息',
            text: '请先保存任务，否则无法提交报警信息',
            type: 'info',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }
    var alarm = {};
    alarm["jobId"] = jobId;
    alarm["alarmType"] = alarmType;
    alarm["receiver"] = newReceiver;
    alarm["status"] = status;

    return alarm;
}

//保存报警信息
function saveAlarm() {
    //获取报警信息
    var alarmData = getAlarmFromPage();
    if (null == alarmData) {
        return;
    }
    $.ajax({
        url: contextPath + "/api/alarm/getByJobId",
        data: {jobId: jobId},
        success: function (data) {
            var response;
            if (data.code == 1000) {
                //代表不存在，需要新增
                if (null == data.data.jobId) {
                    response = requestRemoteRestApi("/api/alarm/add", "新增报警信息", alarmData);
                }
                //修改
                else {
                    response = requestRemoteRestApi("/api/alarm/edit", "更新报警信息", alarmData);
                }
            }
            else {
                new PNotify({
                    title: '检查报警信息',
                    text: data.msg,
                    type: "warning",
                    icon: true,
                    styling: "bootstrap3"
                })
            }
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '获取任务详情', msg);
        }
    });
}
//重置报警
function resetAlarm() {
    $("#alarm").val(null).trigger("change");
    $("#alarmType input").removeAttr("checked");
    $("#alarmStatus input[value=1]").click();
}


////重置参数
//function reset() {
//    var inputs = $("#jobData .input-group>input,#jobData .input-group>textarea");
//    var selects = $("#jobData .input-group>select");
//    $(inputs).each(function (i, c) {
//        $(c).val("");
//    });
//    $(selects).each(function (i, c) {
//        if ($(c).find("option").length >= 1) {
//            var value = $($(c).find("option")[0]).val();
//            $(c).val(value).trigger("change");
//        }
//    });
//    $("#dependJobIds").val(null).trigger("change");
//}

////修改报警人触发
//function changeAlarm(e) {
//    var chosenUser = $("#alarm").val();
//    var existAlarms = new Array();
//    if (existAlarmList != undefined) {
//        existAlarms = JSON.parse(existAlarmList);
//    }
//    var alarmUserJson = {};
//    //数组构造成json，方便后续获取某个用户报警信息
//    $(existAlarms).each(function (i, c) {
//        alarmUserJson[c.receiver] = c;
//    });
//
//    //先保存原来的报警信息
//    var tempSelect = $("#alarmList").find("select");
//    $(tempSelect).each(function (i, c) {
//        var value = $(c).val();
//        $(c).find('option[value=' + value + ']').attr("selected", "selected");
//    });
//    var storage = $("#alarmList").clone();
//
//
//    $("#alarmList").empty();
//    $(chosenUser).each(function (i, c) {
//        var alarmUser = alarmUserJson[c];
//        var div;
//        if (storage.find('div.row[nick=' + c + ']').length > 0) {
//            div = storage.find('div.row[nick=' + c + ']');
//        }
//        else {
//            div = $("#alarmPattern").children().clone();
//            $(div).find("span[name=alarm-user]").text(c);
//            $(div).attr("nick", c);
//
//            $(alarmType).each(function (i, c) {
//                var option = $("<option></option>");
//                option.attr("value", c.id);
//                option.text(c.text);
//
//                if (alarmUserJson[c] != null && alarmUserJson[c].alarmType == c.id) {
//                    option.attr("selected", selected);
//                }
//
//                $(div).find("select[name=alarm-type]").append(option);
//            });
//        }
//
//        $("#alarmList").append(div);
//    });
//}
//
////添加报警
//function addAlarm(thisTag) {
//    var self = $(thisTag).closest(".row").clone();
//    $($(thisTag).closest(".row")).after(self);
//
//}
//
////删除报警
//function deleteAlarm(thisTag) {
//    $(thisTag).closest(".row").remove();
//}