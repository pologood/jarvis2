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
});


function submit(){

}

function reset(){
    $("#content").val(null).trigger("change");
    $("#jobStart").val('');
    $("#jobEnd").val('');
    $("#reRunNext").removeAttr("checked");
}