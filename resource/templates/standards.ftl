<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <title>Standard Libraries</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div>
        <div class="col-lg-9">
            <div class="input-group">

                <input id="standard_search_string" type="text" class="form-control" style="width:75%">
                <select id="search_standard_type" class="form-control" style="width:15%">
                        <option value="" selected="selected">所有标准</option>
                        <option value="vda">VDA</option>
                        <option value="iso27001">ISO27001</option>
                </select>
                <select id="search_archived" class="form-control" style="width:10%">
                    <option value="active" selected="selected">未归档</option>
                    <option value="archived">已归档</option>
                </select>

                <div class="input-group-btn">
                    <button id="search_button" type="button" class="btn btn-default">查找</button>
                    <#if isEvaluation == 0>
                        <#if !readonly>
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                aria-haspopup="true" aria-expanded="false">
                            新建
                            <span style="margin-left: 10px;" class="caret"></span>
                            <span class="sr-only">Toggle Dropdown</span>
                        </button>
	                        <ul class="dropdown-menu dropdown-menu-right">
	                            <li><a href="/create_standard?type=vda">VDA标准</a></li>
	                            <li><a href="/create_standard?type=iso27001">ISO27001标准</a></li>
	                        </ul>
                        </#if>
                    </#if>
                </div>
            </div>
        </div>
        <div id="standard_search_results"></div>
    </div>
</div>
<script id="standards" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th>标准</th>
                <th>类型</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
        {{#each results}}
        <tr>
            <td>{{this.name}}</td>
            <td>{{this.standardType}}</td>
            <td align="center">
                <#if isEvaluation == 0>
                	<a class="btn btn-default" href="/edit_standard?id={{this.standardId}}&readonly=true">查看</a>
                	<#if !readonly>
	                    <a class="btn btn-default" href="/edit_standard?id={{this.standardId}}">修改</a>
	                    <a class="btn btn-default" href="/create_evaluation?id={{this.standardId}}">创建评审</a>
	                    <button class="btn btn-default" standard="{{this.standardId}}" action="archive"></button>
	                </#if>
                <#else>
                    <a class="btn btn-default" href="/edit_evaluation?id={{this.standardId}}&readonly=true">查看</a>
                    <#if !readonly>
                    	<a class="btn btn-default" href="/edit_evaluation?id={{this.standardId}}">修改</a>
                    	<button class="btn btn-default" standard="{{this.standardId}}" action="archive"></button>
                    </#if>
                </#if>
            </td>
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
    var search_results_template = Handlebars.compile($("#standards").html());

    function setupArchiveAction(result) {
        var search_results = $("#standard_search_results");
        var action = search_results.find("[standard='" + result.standardId + "'][action='archive']");
        if (result.archived) {
            action.html("取消归档");
        } else {
            action.html("归档");
        }
        action.unbind();
        action.click(function() {
            result.archived = !result.archived;
            IsmsRequester.requestJson(
                    "/api/standards/" + result.standardId + "/archive",
                    "PATCH",
                    {archived: result.archived},
                    function(response) {
                        setupArchiveAction(result);
                    }
            );
        });
    }

    function updateSearchResults(namePattern, standardType, archived, pageNumber) {
        IsmsRequester.requestJson(
                "${libraryUri}",
                "POST",
                {
                    namePattern: namePattern,
                    standardType: standardType,
                    pageNumber: pageNumber,
                    itemPerPage: 5,
                    archived: archived
                },
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    $("#standard_search_results").html(search_results_template(response));
                    var prevPage = $("#prevPage");
                    if (prevPage.length && response.pageNumber > 0) {
                        prevPage.click(function () {
                            updateSearchResults(namePattern, standardType, archived, pageNumber - 1);
                        });
                    }
                    var nextPage = $("#nextPage");
                    if (nextPage.length && response.hasNextPage) {
                        nextPage.click(function () {
                            updateSearchResults(namePattern, standardType, archived, pageNumber + 1);
                        });
                    }
                    for (var i = 0; i < response.results.length; ++i) {
                        setupArchiveAction(response.results[i]);
                    }
                }
        );
    }

    $("#search_button").click(function () {
        var namePattern = $("#standard_search_string").prop("value");
        var standardType = $("#search_standard_type").prop("value");
        var archivedStatus = $("#search_archived").prop("value");
        var archived = false;
        if (archivedStatus == "archived") {
            archived = true;
        }
        updateSearchResults(namePattern, standardType, archived, 0);
    });

    updateSearchResults("", "", false, 0);
</script>
</body>
</html>