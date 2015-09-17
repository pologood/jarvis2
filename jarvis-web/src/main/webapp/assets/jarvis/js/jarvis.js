
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