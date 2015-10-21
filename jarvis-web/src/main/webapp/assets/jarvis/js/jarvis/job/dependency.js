var dependencyUrl="/jarvis/api/job/getTwoDirectionTree?jobId=";
var url=dependencyUrl+jobVo.jobId;

$(function(){
    var url=dependencyUrl+jobVo.jobId;
    //console.log(url)

    var tree = CollapsibleTree("#dependTree");

    tree.init(url);
});



/*
function getTotalDepth(json){
    var parentsDepth=getDepth(json["parents"],"parents");
    var childrenDepth=getDepth(json["children"],"children");
    //console.log("parentsDepth:"+parentsDepth);
    //console.log("childrenDepth:"+childrenDepth);
    return parentsDepth+childrenDepth;
}

function getDepth(nodes,direction){
    var depth=0;
    if(nodes==undefined||nodes.length<=0){
        return depth;
    }
    nodes.forEach(function(c){
        depth= c.depth;
        var new_depth = getDepth(c[direction],direction);
        if(new_depth>depth){
            depth=new_depth;
        }
    });

    return depth;
}
*/



