<html>
<head>
    <meta charset="utf-8">
    <title>Department</title>
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
                <button id="create_button" class="btn btn-default" type="button">Create</button>
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
                <th style="width:40%;min-width:150px;">DepartmentName</th>
                <th style="width:40%;min-width:150px;">DepartmentDesc</th>
                <th style="width:20%;min-width:200px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr deptId="{{deptId}}">
                <td>{{deptName}}</td>
                <td>{{deptDesc}}</td>
                <#if !readonly>
                    <td style="padding-right:0px;width:200px;">
                        <button class="btn btn-default" evidence="{{this.id}}" action="update">Update</button>
                        <button class="btn btn-default" evidence="{{this.id}}" action="delete">Delete</button>
                    </td>
                </#if>
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
<div style="display:none" id="update_dialog">
    <form>
        <label for="update-deptId">DepartmentName:</label>
        <input class="form-control" name="classTypeTxt" id="update-deptName">

        <label for="update-deptName">DepartmentDesc</label>
        <textarea id="update-deptDesc" class="form-control"></textarea>
    </form>
    <div align="right">
        <button class="btn btn-primary" id="update-btn">Update</button>
    </div>
</div>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_dept").addClass("active");
    var search_results_template = Handlebars.compile($("#depts").html());

    var pageResult = $("#standard_search_results");
    var dialog = $("#update_dialog");
    var updateDeptName = $("#update-deptName");
    var updateDeptDesc = $("#update-deptDesc");
    var updateBtn = $("#update-btn");

    function setupUI(ui, depts) {
        for (var i = 0; i < depts.length; ++i) {
            var item = depts[i];
            var row = ui.find("[deptId='" + item.deptId + "']");
            var action = row.find("[action]");
            if (action.length) {
                action.unbind();
                action.click(
                        { item: item},
                        function (e) {
                            var action = $(this).attr('action');
                            if(action=='delete'){
                                IsmsRequester.requestJson(
                                    "/api/dept/" + e.data.item.deptId,
                                    "DELETE",
                                    { },
                                    function (response) {
                                        ui.find("[deptId='" + e.data.item.deptId + "']").remove();
                                        window._response.count--;
                                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
                                    });
                            }

                            if(action=='update'){
                                updateDeptName.val(e.data.item.deptName);
                                updateDeptDesc.val(e.data.item.deptDesc);

                                updateBtn.html('Update').unbind();
                                updateBtn.click(e.data,function(){
                                    var deptName = updateDeptName.val();
                                    var deptDesc = updateDeptDesc.val();
                                    IsmsRequester.requestJson(
                                            "/api/dept/" + e.data.item.deptId,
                                            "PATCH",
                                            { deptName: deptName,deptDesc: deptDesc},
                                            function (response) {
                                                ui.find("[deptId='" + e.data.item.deptId + "'] td:eq(0)").html(deptName);
                                                ui.find("[deptId='" + e.data.item.deptId + "'] td:eq(1)").html(deptDesc);
                                                dialog.dialog('close');
                                            });
                                });
                                dialog.attr('title','Update Department');
                                dialog.dialog({modal: true,title:'Update Department'});
                            }

                        });
            }
        }
    }
    window._response = null;
    function updateSearchResults(namePattern, pageNumber) {
        IsmsRequester.requestJson(
                "/api/depts",
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
                    pageResult.html(search_results_template(response));
                    window._response = response;
                    setupUI(pageResult, response.results);
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

    $("#create_button").click(function () {
        updateDeptName.val('').attr('readonly',false);
        updateDeptDesc.val('');
        updateBtn.html('Create').unbind();
        updateBtn.click(function(){
            updateDeptName.find('+span').remove();

            var deptName = updateDeptName.val();
            var deptDesc = updateDeptDesc.val();
            if(deptName.trim()==''){
                updateDeptName.after('<span class="help-block">DepartmentName cant empty.</span>');
                return;
            }
            IsmsRequester.requestJson(
                    "/api/dept",
                    "POST",
                    { deptName: deptName,deptDesc:deptDesc },
                    function (response) {
                        var datas = [response];
                        $("#standard_search_results tbody").prepend($(search_results_template({results:datas})).find("tbody tr:first"));
                        setupUI(pageResult, datas);
                        dialog.dialog('close');
                        window._response.count++;
                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
                    });
        });
        dialog.dialog({modal: true,title:'Create Department'});
    });

    updateSearchResults('',0);
</script>
</body>
</html>