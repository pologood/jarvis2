//时间选择器
function createDatetimePickerById(tagId){
    if(tagId==undefined||tagId==''){
        return;
    }

    $("#"+tagId).datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });
}
//通过后台请求远程rest api,根据请求结果返回flag
function requestRemoteRestApi(url,title,para){
    var flag=true;
    var data={};
    for(var key in para){
        //var value=encodeURIComponent(para[key]);
        var value=para[key];
        //var value=value.replace(/\?/g,"%3F");
        data[key]= value;
    }



    $.ajax({
        url:'/jarvis/remote/request',
        type:'POST',
        async:false,
        data:{url:url,para:JSON.stringify(data)},
        success:function(data){
            if(data.code==0){
                flag=true;
                new PNotify({
                    title: title,
                    text: data.msg,
                    type: 'success',
                    icon: true,
                    styling: 'bootstrap3'
                });
            }
            else{
                flag=false;
                new PNotify({
                    title: title,
                    text: data.msg,
                    type: 'warning',
                    icon: true,
                    styling: 'bootstrap3'
                });
            }
        }
    });

    return flag;
}


//时间戳转化成日期
function formatDate(date){
    var theDate=new Date(date);
    var result=moment(theDate).format("YYYY-MM-DD");
    return result;
}
//时间戳转化成日期时间
function formatDateTime(dateTime){
    var theDate=new Date(dateTime);
    var result=moment(theDate).format("YYYY-MM-DD HH:mm:ss");
    return result;
}
//格式化状态
function formatStatus(dataArr,status){
    if(dataArr==undefined||dataArr==null||status==undefined||status==null){
        return "";
    }
    var statusStr="";
    $(dataArr).each(function(i,c){
        var id=c["id"];
        if(id==status){
            statusStr=c["text"];
            return false;
        }
    });
    return statusStr;
}