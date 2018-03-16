<html>
<head>
    <meta charset="utf-8">
    <title>权限管理</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">

    <br/>
    <div id="standard_search_results"></div>
</div>

<script id="table-template" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:30%;min-width:200px;text-align:center;">功能名称</th>
                <th style="width:70%;min-width:200px;text-align:center;">功能路径</th>
            </tr>
        </thead>
        <tbody>
            {{#each this}}
            <tr>
                <td align="center">{{menuName}}</td>
                <td align="center">{{menuUrl}}</td>
            </tr>
            {{/each}}
        </tbody>

    </table>
</script>

<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_permission").addClass("active");
    var search_results_template = Handlebars.compile($("#table-template").html());


    IsmsRequester.requestJson(
            "/api/menus",
            "GET",
            {}
            ,
            function (response) {
                $("#standard_search_results").html(search_results_template(response));
            }
    );
</script>
</body>
</html>