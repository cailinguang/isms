<html>
<head>
    <meta charset="utf-8">
    <title>权限管理</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <button id="backup_btn" class="btn btn-default" type="button">备份</button>
    <br/>
    <div id="standard_search_results"></div>
</div>

<script id="table-template" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:30%;min-width:200px;text-align:center;">备份</th>
                <th style="width:70%;min-width:200px;text-align:center;">备份路径</th>
                <th style="width:20%;min-width:200px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#each this}}
            <tr>
                <td align="center">{{date}}</td>
                <td align="center">{{path}}</td>
                <td>
                    <button class="btn btn-default" type="button" onclick="restore('{{date}}')">还原</button>
                    <button class="btn btn-default" type="button" onclick="download('{{date}}')">下载</button>
                    <button class="btn btn-default" type="button" onclick="deleteR('{{date}}')">删除</button>
                </td>
            </tr>
            {{/each}}
        </tbody>

    </table>
</script>

<#include "common_js.ftl">
<script type="text/javascript">
    var search_results_template = Handlebars.compile($("#table-template").html());


    function loadPage() {
        IsmsRequester.requestJson( "/api/backups", "POST", {} ,
                function (response) {
                    $("#standard_search_results").html(search_results_template(response));
                }
        );
    }

    $("#backup_btn").click(function () {
        IsmsRequester.requestJson("/api/backup","POST",{},function (response) {
            IsmsErrorReporter.reportError("备份成功！");
            loadPage();

        });
    });

    function restore(file) {
        IsmsRequester.requestJson("/api/restore/"+file,"POST",{},function (response) {
            IsmsErrorReporter.reportError("还原成功，请等待系统重新启动。");
        });
    }

    function deleteR(file) {
        if(!confirm("确认删除？")){
            return false;
        }
        IsmsRequester.requestJson("/api/restore/"+file,"DELETE",{},function (response) {
            IsmsErrorReporter.reportError("删除成功。");
            loadPage();
        });
    }

    function download(file) {
        location.href = "/api/down_load_restore/"+file;
    }


    loadPage();
</script>
</body>
</html>