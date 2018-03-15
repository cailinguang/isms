<html>
<head>
    <meta charset="utf-8">
    <title>审计日志</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div >
        <div class="input-group">
            <input id="search_string" type="text" class="form-control" placeholder="Search for Name">
            <span class="input-group-btn">
                <button id="search_button" class="btn btn-default" type="button">Search</button>
                <button id="export_button" class="btn btn-default" type="button">Export</button>
            </span>
        </div>
    </div>
    <br/>
    <div id="standard_search_results"></div>
</div>

<script id="depts" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:30%;min-width:150px;">时间</th>
                <th style="width:30%;min-width:150px;">用户</th>
                <th style="width:40%;min-width:150px;">状态</th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr>
                <td>{{operationDate}}</td>
                <td>{{userName}}</td>
                <td>{{operation}}</td>
            </tr>
            {{/each}}
        </tbody>
    </table>
</script>

<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_audit_log").addClass("active");
    var search_results_template = Handlebars.compile($("#depts").html());

    function updateSearchResults(namePattern, pageNumber) {
        IsmsRequester.requestJson(
                "/api/auditLogs",
                "POST",
                {
                    deptName: namePattern,
                    itemPerPage: 10,
                    pageNumber: pageNumber
                },
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    $("#standard_search_results").html(search_results_template(response));

                    var prevPage = $("#prevPage");
                    if (prevPage.length && response.pageNumber > 0) {
                        prevPage.click(function () {
                            updateSearchResults(namePattern, pageNumber - 1);
                        });
                    }
                    var nextPage = $("#nextPage");
                    if (nextPage.length && response.hasNextPage) {
                        nextPage.click(function () {
                            updateSearchResults(namePattern, pageNumber + 1);
                        });
                    }
                }
        );
    }

    $("#search_button").click(function () {
        var namePattern = $("#search_string").prop("value");
        updateSearchResults(namePattern, 0);
    });


    updateSearchResults('',0);
</script>
</body>
</html>