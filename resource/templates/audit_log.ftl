<html>
<head>
    <meta charset="utf-8">
    <title>审计日志</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">

    <form class="form-inline">
        <div class="form-group">
            <label for="startDate">Start Date</label>
            <input type="text" class="form-control" id="startDate" placeholder="Start Date">
        </div>
        <div class="form-group">
            <label for="endDate">End Date</label>
            <input type="email" class="form-control" id="endDate" placeholder="End Date">
        </div>
        <div class="form-group">
            <label for="userName">UserName</label>
            <input type="email" class="form-control" id="userName" placeholder="UserName">
        </div>
        <button id="search_button" type="button" class="btn btn-default">Search</button>
        <button id="export_button" class="btn btn-default" type="button">Export</button>
    </form>

    <br/>
    <div id="standard_search_results"></div>
</div>

<script id="depts" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:30%;min-width:150px;text-align:center;">时间</th>
                <th style="width:30%;min-width:150px;text-align:center;">用户</th>
                <th style="width:40%;min-width:150px;text-align:center;">状态</th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr>
                <td align="center">{{operationDate}}</td>
                <td align="center">{{userName}}</td>
                <td align="center">{{operation}}</td>
            </tr>
            {{/each}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="3">
                <div style="float:left;" id="pageInfo">{{page-info this}}</div>
                <div style="float:right;">
                    {{#if hasPrevPage}}<a id="prevPage" href="javascript:void(0)" style="margin-right:20px;">前一页</a>{{/if}}
                    {{#if hasNextPage}}<a id="nextPage" href="javascript:void(0)" style="margin-left:30px;">后一页</a>{{/if}}
                </div>
            </td>
        </tr>
        </tfoot>
    </table>
</script>

<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_audit_log").addClass("active");
    var search_results_template = Handlebars.compile($("#depts").html());

    $("#startDate,#endDate").datetimepicker({
        format:'yyyy-mm-dd',
        autoclose:true,
        todayBtn:true,
        minView:3
    });

    function updateSearchResults(search, pageNumber) {
        IsmsRequester.requestJson(
                "/api/auditLogs",
                "POST",
                $.extend({
                    itemPerPage: 10,
                    pageNumber: pageNumber
                },search)
                ,
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    $("#standard_search_results").html(search_results_template(response));

                    var prevPage = $("#prevPage");
                    if (prevPage.length && response.pageNumber > 0) {
                        prevPage.click(function () {
                            updateSearchResults(search, pageNumber - 1);
                        });
                    }
                    var nextPage = $("#nextPage");
                    if (nextPage.length && response.hasNextPage) {
                        nextPage.click(function () {
                            updateSearchResults(search, pageNumber + 1);
                        });
                    }
                }
        );
    }

    $("#search_button").click(function () {
        var search = {
            startDate:$("#startDate").val(),
            endDate:$("#endDate").val(),
            userName:$("#userName").val()
        };
        updateSearchResults(search, 0);
    });


    updateSearchResults({},0);
</script>
</body>
</html>