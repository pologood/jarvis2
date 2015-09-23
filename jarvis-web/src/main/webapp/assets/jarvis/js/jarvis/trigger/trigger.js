$(function(){
    $('#jobStart').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    $('#jobEnd').datetimepicker({
        language:'zh-CN',
        minView:'month',
        format: 'yyyy-mm-dd',
        autoclose:true
    });

    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});
    $("#content").on("change", function (e) {
        $("#reRunNext").jstree('destroy');
        var id=$(e.target).val();
        if(id!=null&&id!=''){
            buildTree(id);
        }
    });


});

function buildTree(jobId){

    $.ajax({
        url:'/jarvis/api/job/getTreeDependedONJob',
        data:{jobId:jobId},
        success:function(data){
            $("#reRunNext").jstree({
                'core':{
                    data:data
                },
                "types": {
                    "default": {"icon": "fa fa-users icon-green", "valid_children": []}
                },
                plugins : [
                    'checkbox','types'
                ]
            });
        }
    });


}

function reset(){
    $("#content").val(null).trigger("change");
    $("#jobStart").val('');
    $("#jobEnd").val('');
    $("#reRunNext").removeAttr("checked");
}


function submit(){
    var checked=$("#reRunNext").jstree().get_checked();
}