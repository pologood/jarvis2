//查找
function search() {
  $("#content").bootstrapTable("destroy");
  initData();
}

//重置参数
function reset() {
  $("#startOperDate").val("");
  $("#endOperDate").val("");
  $("#operateName").val("");
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


$(function () {
  createDatetimePickerById("startOperDate");
  createDatetimePickerById("endOperDate");

  //select采用select2 实现
  $(".input-group select").select2({width: '100%'});

  $.ajaxSettings.async = false;
  $.getJSON(contextPath + "/assets/json/jobType.json", function (data) {
    jobTypeJson = data;
    $("#jobType").select2({
      data: data,
      width: '100%',
      tags: true
    });
  });

  $.getJSON(contextPath + "/api/job/getJobStatus", function (data) {
    jobStatus = data;
    $("#jobStatus").select2({
      data: data,
      width: '100%'
    });
  });

  $.getJSON(contextPath + "/assets/json/jobPriority.json", function (data) {
    jobPriorityJson = data;
    $("#jobPriority").select2({
      data: data,
      width: '100%'
    });
  });
  $.ajaxSettings.async = true;

  $("#title").select2({
    ajax: {
      url: contextPath + "/api/operation/getSimilarOperationTitles",
      dataType: 'json',
      delay: 250,
      data: function (params) {
        return {
          q: params.term, // search term
          page: params.page
        };
      },
      processResults: function (data, page) {
        return {
          results: data.items
        };
      },
      cache: true
    },
    escapeMarkup: function (markup) {
      return markup;
    },
    minimumInputLength: 1,
    templateResult: formatResult,
    templateSelection: formatResultSelection,

    width: '100%'
  });

  initData();
});


//获取查询参数
function getQueryPara() {
  var queryPara = {};

  var startOperDate = $("#startOperDate").val();
  var endOperDate = $("#endOperDate").val();
  var titleList = $("#title").val();

  titleList = titleList == "all" ? undefined : titleList;
  titleList = titleList == null ? undefined : titleList;
  queryPara["titleList"] = JSON.stringify(titleList);
  queryPara["startOperDate"] = startOperDate;
  queryPara["endOperDate"] = endOperDate;

  return queryPara;
}


//字段配置
var columns = [{
  field: 'title',
  title: '任务名称',
  switchable: true,
  visible: true
}, {
  field: 'operator',
  title: '操作者',
  switchable: true,
  visible: true
},{
  field: 'detail',
  title: '详情',
  switchable: true,
  visible: true
}, {
  field: 'opeDate',
  title: '时间',
  switchable: true,
  visible: true
}];

//初始化数据及分页
function initData() {
  var queryParams = getQueryPara();
  $("#content").bootstrapTable({
    columns: columns,
    pagination: true,
    sidePagination: 'server',
    search: false,
    url: contextPath + '/api/operation/getOperations',
    queryParams: function (params) {
      for (var key in queryParams) {
        var value = queryParams[key];
        params[key] = value;
      }
      return params;
    },
    showColumns: true,
    showHeader: true,
    showToggle: true,
    pageSize: 20,
    pageList: [10, 20, 50, 100, 200, 500],
    paginationFirstText: '首页',
    paginationPreText: '上一页',
    paginationNextText: '下一页',
    paginationLastText: '末页',
    showExport: true,
    exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
    exportDataType: 'all'
  });
}

//格式化结果
function formatResult(result) {
  return result.text;
}
//格式化结果选择框
function formatResultSelection(result) {
  return result.id;
}
