<%--
  Created by IntelliJ IDEA.
  User: muming
  Date: 16/3/8
  Time: 下午1:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- 选择脚本-弹出框 -->
<div id="searchScriptModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">选择脚本</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <input id="searchScriptInput" class="form-control" type="search"
                           placeholder="回车搜索"/>
                </div>
                <div><b>脚本一览</b><span style="color: gray">&nbsp&nbsp*双击选中*</span></div>
                <div id="searchScriptList" class="list-group"
                     style="height:400px; overflow:auto">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">返回
                </button>
            </div>
        </div>
    </div>
</div>


<!--任务参数——模式框 -->
<div id="paraModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">任务参数</h4>
            </div>
            <div class="modal-body">
                <table id="pattern" style="display: none">
                    <tr>
                        <td>
                            <input name="key" class="form-control" placeholder="请输入属性的key">
                        </td>
                        <td>
                            <input name="value" class="form-control"
                                   placeholder="请输入属性的value">
                        </td>
                        <td>
                            <a class="glyphicon glyphicon-plus" href="javascript:void(0)"
                               onclick="addPara(this)"></a>
                            <a class="glyphicon glyphicon-minus" href="javascript:void(0)"
                               onclick="deletePara(this)"></a>
                        </td>
                    </tr>
                </table>

                <table id="parasTable" class="table table-bordered">
                    <thead>
                    <tr>
                        <th>key</th>
                        <th>value</th>
                        <th>操作</th>
                    </tr>

                    </thead>
                    <tbody>

                    </tbody>
                </table>
                <a class="glyphicon glyphicon-plus" href="javascript:void(0)"
                   onclick="addPara(null)"></a>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消
                </button>
                <button type="button" class="btn btn-primary" onclick="ensurePara()">确定
                </button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<!--sparkLauncher任务参数——模式框 -->
<div id="sparkLauncherParasModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">SparkLauncher任务参数</h4>
            </div>

            <div id="sparkLauncherParasModalBody" class="modal-body">
                <!-- main函数 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">main函数
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <input name="mainClass" class="form-control required" data-desc="main函数" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- 文件路径 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">文件路径
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <textarea name="taskJar" class="form-control required" data-desc="文件路径"
                                      rows="3" placeholder=""></textarea>
                        </div>
                    </div>
                </div>
                <!-- 应用参数 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">应用参数</span>
                            <input name="applicationArguments" class="form-control" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- driver核数 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">driver核数
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <input name="driverCores" class="form-control required" data-defaultValue="1"
                                   data-desc="driver核数" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- driver内存 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">driver内存
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <input name="driverMemory" class="form-control required" data-defaultValue="4g"
                                   data-desc="driver内存" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- executor核数 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">executor核数
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <input name="executorCores" class="form-control required" data-defaultValue="1"
                                   data-desc="executor核数" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- executor内存 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">executor内存
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <input name="executorMemory" class="form-control required" data-defaultValue="4g"
                                   data-desc="executor内存" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- executor数目 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">executor数目
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                            <input name="executorNum" class="form-control required" data-defaultValue="6"
                                   data-desc="executor数目" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- spark执行参数 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">spark执行参数</span>
                            <input name="sparkSubmitProperties" class="form-control" placeholder=""/>
                        </div>
                    </div>
                </div>
                <!-- spark版本 -->
                <div class="row top-buffer">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="input-group" style="width:100%">
                            <span class="input-group-addon" style="width:30%">spark版本</span>
                            <input name="sparkVersion" class="form-control"
                                   data-defaultValue="spark-1.6.0" data-desc="spark版本" placeholder=""/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消
                </button>
                <button type="button" class="btn btn-primary" onclick="confirmJobParas4SparkLauncher()">确定
                </button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->






