var dependencyUrl = contextPath + "/api/job/getTwoDirectionTree?jobId=";
var url = dependencyUrl + jobVo.jobId;

$(function () {
    var tree = CollapsibleTree("#dependTree");

    tree.init(url);
});

function generateForceData(data) {

}




