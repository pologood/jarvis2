$(function () {
    initChart();
    window.setTimeout(function () {
        initLog();
    }, 3000);
});

function initChart(){
    var xAxis = new Array();
    var data = new Array();
    for (var i = 0; i < taskVoList.length; i++) {
        var task = taskVoList[i];
        var theDate = new Date(task["executeStartTime"]);
        var result = moment(theDate).format("YYYY-MM-DD HH:mm:ss");
        xAxis.push(result);
        data.push(task["executeTime"]);
    }
    if(data.length<1){
        return;
    }

    var myChart = echarts.init(document.getElementById('container'));
    var option = {
        title: {
            text: '最近30次成功执行所用时间',
            subtext: '单位(秒)'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['执行用时']
        },
        toolbox: {
            show: true,
            feature: {
                mark: {show: true},
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: ['line', 'bar']},
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        calculable: true,
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                data: xAxis
            }
        ],
        yAxis: [
            {
                type: 'value',
                axisLabel: {
                    formatter: '{value} 秒'
                }
            }
        ],
        series: [
            {
                name: '执行用时',
                type: 'line',
                data: data,
                markPoint: {
                    data: [
                        {type: 'max', name: '最大值'},
                        {type: 'min', name: '最小值'}
                    ]
                },
                markLine: {
                    data: [
                        {type: 'average', name: '平均值'}
                    ]
                }
            }
        ]
    };

    // 为echarts对象加载数据
    myChart.setOption(option);
}


function initLog() {
    var data = {};
    data["taskId"] = taskId;
    data["jobId"] = jobId;
    data["attemptId"] = attemptId;
    data["offset"] = 0;
    data["lines"] = 1000;

    var url;
    if(page.jobType == CONST.JOB_TYPE.HIVE){
        url = "/api/log/readExecuteLog";
    }else{
        url = "/api/log/readResult";
    }

    var result = requestRemoteRestApi(url, "读取执行日志", data);
    if (result.flag == true) {
        var log = result.data.data.log.toString();
        $("#log").html(log);
    }
}

