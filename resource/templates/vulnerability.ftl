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
            <input id="search_string" type="text" class="form-control" placeholder="Search for...">
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
                <th>DepartmentID</th>
                <th>DepartmentName</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr deptId="{{deptId}}">
                <td>{{deptId}}</td>
                <td>{{deptName}}</td>
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
                <td colspan="3" align="right">
                    {{#if hasPrevPage}}<a id="prevPage" href="javascript:void(0)" style="margin-right:20px;">前一页</a>{{/if}}
                    {{#if hasNextPage}}<a id="nextPage" href="javascript:void(0)" style="margin-left:30px;">后一页</a>{{/if}}
                </td>
            </tr>
        </tfoot>
    </table>
</script>
<div style="display:none" id="update_dialog">
    <form>
        <label for="update-deptId">DepartmentID:</label>
        <input class="form-control" name="classTypeTxt" id="update-deptId">

        <label for="update-deptName">DepartmentName</label>
        <textarea id="update-deptName" class="form-control"></textarea>
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
    var updateDeptId = $("#update-deptId");
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
                                    });
                            }

                            if(action=='update'){
                                updateDeptName.val(e.data.item.deptName);
                                updateDeptId.val(e.data.item.deptId).attr('readonly',true);
                                updateBtn.html('Update').unbind();
                                updateBtn.click(e.data,function(){
                                    var deptName = updateDeptName.val();
                                    IsmsRequester.requestJson(
                                            "/api/dept/" + e.data.item.deptId,
                                            "PATCH",
                                            { deptName: deptName },
                                            function (response) {
                                                ui.find("[deptId='" + e.data.item.deptId + "'] td:eq(1)").html(deptName);
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
        updateDeptName.val('');
        updateDeptId.val('').attr('readonly',false);
        updateBtn.html('Create').unbind();
        updateBtn.click(function(){
            updateDeptName.find('+span').remove();
            updateDeptId.find('+span').remove();

            var deptName = updateDeptName.val();
            var deptId = updateDeptId.val();
            if(deptId.trim()==''){
                updateDeptId.after('<span class="help-block">DepartmentID cant empty.</span>');
                return;
            }
            if(deptName.trim()==''){
                updateDeptName.after('<span class="help-block">DepartmentName cant empty.</span>');
                return;
            }
            IsmsRequester.requestJson(
                    "/api/dept",
                    "POST",
                    { deptName: deptName,deptId:deptId },
                    function (response) {
                        var datas = [{ deptName: deptName,deptId:deptId }];
                        $("#standard_search_results tbody").prepend($(search_results_template({results:datas})).find("tbody tr:first"));
                        setupUI(pageResult, datas);
                        dialog.dialog('close');
                    });
        });
        dialog.dialog({modal: true,title:'Create Department'});
    });

    updateSearchResults('',0);
</script>
</body>
</html>