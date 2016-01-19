var bizGroup = null;

$(function () {
    initBizGroup();
    initUsers();
    initStatus();
});

//初始化业务标签
function initBizGroup() {
    $.ajaxSettings.async = false;
    if (null != id && '' != id) {
        $.getJSON(contextPath + "/api/bizGroup/getById", {id: id}, function (data) {
            if (data.code == 1000) {
                bizGroup = data.data;
                $("#name").val(bizGroup.name);
            }
            else {
                new PNotify({
                    title: '获取业务标签详情',
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
            }
        });
    }
    $.ajaxSettings.async = true;

}

//初始化内网用户
function initUsers() {
    $.getJSON(contextPath + "/api/common/getAllUser", function (data) {
        if (1000 == data.code) {
            var users = data.rows;
            var newData = new Array();

            $(users).each(function (i, c) {
                var item = {};
                item["id"] = c.uname;
                item["text"] = c.nick;
                newData.push(item);
            });

            $("#owner").select2({
                data: newData,
                width: '100%'
            });
            if (null != bizGroup) {
                var owner = bizGroup.owner;
                var arr = owner.trim().split(",");
                $("#owner").val(arr).trigger("change");
            }
        }
        else {
            new PNotify({
                title: '获取内网用户信息',
                text: data.msg,
                type: 'error',
                icon: true,
                styling: 'bootstrap3'
            });
        }
    })
}
//
function initStatus() {
    $.getJSON(contextPath + "/api/bizGroup/getBizGroupStatus", function (data) {
        $(data).each(function (i, c) {
            var input = '<input type="radio" name="status" value="' + c.id + '" />' + c.text + " ";
            $("#status").append(input);
        });

        if (null != bizGroup) {
            $("#status input[value=" + bizGroup.status + "]").click();
        }
    });
}


//获取配置信息
function getData() {
    var name = $("#name").val();
    var ownerArr = $("#owner").val();
    var status = $("#status input:checked").val();
    //检查维护人是否为空
    if (null == ownerArr) {
        new PNotify({
            title: '保存配置',
            text: "维护人不能为空",
            type: 'error',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }
    var hasName = false;
    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/api/bizGroup/getByName", {name: name}, function (data) {
        if (null == data.data) {
            hasName = true;
        }
        else {
            if (null == id || '' == id) {
                hasName = false;
            }
            else {
                if (id == data.data.id) {
                    hasName = true;
                }
                else {
                    hasName = false;
                }
            }
        }
    });
    $.ajaxSettings.async = true;

    if (!hasName) {
        new PNotify({
            title: '保存配置',
            text: "业务标签已经存在,不能重复添加",
            type: 'error',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }


    var owner = "";
    $(ownerArr).each(function (i, c) {
        if (owner == "") {
            owner = c;
        }
        else {
            owner += "," + c;
        }
    });

    if(undefined==status){
        new PNotify({
            title: '保存配置',
            text: "业务标签状态必须选择",
            type: 'error',
            icon: true,
            styling: 'bootstrap3'
        });
        return null;
    }

    var result = {};
    if (null != id && '' != id) {
        result["id"] = id;
    }
    result["name"] = name;
    result["owner"] = owner;
    result["status"] = status;

    return result;
}

//保存业务标签
function saveBizGroup() {

    var data = getData();
    if (null == data) {
        return null;
    }

    if (null != id && '' != id) {
        requestRemoteRestApi("/api/bizGroup/edit", "修改业务标签", data);

    }
    else {
        requestRemoteRestApi("/api/bizGroup/add", "新增业务标签", data);
    }
}