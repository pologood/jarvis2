$(function(){


    var option = {
        title : {
            text: '任务依赖关系图',
            subtext: '虚构数据'
        },
        toolbox: {
            show : true,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        calculable : false,

        series : [
            {
                name:'树图',
                type:'tree',
                orient: 'vertical',  // vertical horizontal
                rootLocation: {x: 'center',y: 50}, // 根节点位置  {x: 100, y: 'center'}
                nodePadding: 50,
                roam:'move',
                clickable:true,
                direction:'reverse',
                itemStyle: {
                    normal: {
                        label: {
                            show: true,
                            position: 'bottom',
                            formatter: "{b}",
                            textStyle: {
                                color:'#000',
                                fontSize: 10
                            }
                        },
                        lineStyle: {
                            color: '#48b',
                            shadowColor: '#000',
                            shadowBlur: 3,
                            shadowOffsetX: 3,
                            shadowOffsetY: 5,
                            type: 'curve' // 'curve'|'broken'|'solid'|'dotted'|'dashed'

                        }
                    },
                    emphasis: {
                        label: {
                            show: false
                        }
                    }
                },

                data: [
                    {
                        name: '根节点',
                        value: 6,
                        children: [
                            {
                                name: '节点1',
                                value: 4,
                                children: [
                                    {
                                        name: '叶子节点1',
                                        value: 4
                                    },
                                    {
                                        name: '叶子节点2',
                                        value: 4
                                    },
                                    {
                                        name: '叶子节点3',
                                        value: 2
                                    },
                                    {
                                        name: '叶子节点4',
                                        value: 2
                                    },
                                    {
                                        name: '叶子节点5',
                                        value: 2
                                    },
                                    {
                                        name: '叶子节点6',
                                        value: 4
                                    }
                                ]
                            },
                            {
                                name: '节点2',
                                value: 4,
                                children: [{
                                    name: '叶子节点7',
                                    value: 4
                                },
                                    {
                                        name: '叶子节点8',
                                        value: 4
                                    }]
                            },
                            {
                                name: '节点3',
                                value: 1,
                                children: [
                                    {
                                        name: '叶子节点9',
                                        value: 4
                                    },
                                    {
                                        name: '叶子节点10',
                                        value: 4
                                    },
                                    {
                                        name: '叶子节点11',
                                        value: 2
                                    },
                                    {
                                        name: '叶子节点12',
                                        value: 2
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
    };

    var dependTree=echarts.init(document.getElementById('dependTree'));

    dependTree.setOption(option);

    dependTree.on('click', toDependency);
});

var dependencyUrl="/jarvis/job/dependency?jobId=";
function toDependency(e){
    console.log(e);
    alert("已经点击了"+ e.name);
}