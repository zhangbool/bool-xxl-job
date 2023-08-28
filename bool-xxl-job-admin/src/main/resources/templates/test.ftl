<html>
<head>
    <#import "./common/common.macro.ftl" as netCommon>
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap/css/bootstrap.min.css">

</head>

<body>

<#list students as student>
    <div>name: ${student.name}</div>
</#list>

<div class="col-xs-2">
    <button class="btn btn-block btn-info" id="searchBtn">btn</button>
</div>


<div id="zhang">zhang</div>

<div class="row">
    <div class="col-xs-12">
        <div class="box">
            <#--<div class="box-header hide"><h3 class="box-title">调度日志</h3></div>-->
            <div class="box-body">
                <table id="students" class="table table-bordered table-striped display" width="100%" >
                    <thead>
                    <tr>
                        <th name="no">no</th>
                        <th name="name">name</th>
                        <th name="age"></th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
    </div>
</div>





<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>

<script src="${request.contextPath}/static/js/test.js"></script>



<!-- 模态框（Modal） -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">模态框（Modal）标题</h4>
            </div>
            <div class="modal-body" id="age">这里添加一些文本</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary">提交更改</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
</body>


</html>




