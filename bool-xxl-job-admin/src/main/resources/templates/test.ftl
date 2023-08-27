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
                        <th name="age">age</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
    </div>
</div>


<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>

<script src="${request.contextPath}/static/js/test.js"></script>

</body>


</html>




