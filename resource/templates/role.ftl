<html>
<head>
    <meta charset="utf-8">
    <title>Role</title>
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
                <#if !readonly>
                <button id="create_button" class="btn btn-default" type="button">Create</button>
                </#if>
            </span>
        </div>
    </div>
    <br/>
    <div id="standard_search_results"></div>
</div>

<script id="roles" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:40%;min-width:150px;">RoleID</th>
                <th style="width:40%;min-width:150px;">RoleName</th>
                <#if !readonly>
                <th style="width:20%;min-width:300px;"></th>
                </#if>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr roleId="{{roleId}}">
                <td>{{roleId}}</td>
                <td>{{roleName}}</td>
                <#if !readonly>
                    <td style="padding-right:0px;width:200px;">
                        <button class="btn btn-default" evidence="{{this.id}}" action="update">Update</button>
                        <button class="btn btn-default" evidence="{{this.id}}" action="delete">Delete</button>
                        <button class="btn btn-default" evidence="{{this.id}}" action="permissions">Permissions</button>
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
        <label for="update-roleId">RoleID:</label>
        <input class="form-control" name="classTypeTxt" id="update-roleId">

        <label for="update-roleName">RoleName</label>
        <textarea id="update-roleName" class="form-control"></textarea>
    </form>
    <div align="right">
        <button class="btn btn-primary" id="update-btn">Update</button>
    </div>
</div>

<div title="Grant Role Menus" style="display:none" id="grant_dialog">
    <div style="margin:10px;" id="menu-content"></div>
    <div align="right">
        <button class="btn btn-primary" id="grant-menu-btn">Save</button>
    </div>
</div>

<#include "common_js.ftl">
<script id="menu-template" type="text/x-handlebars-template">
    <ul class="list-unstyled">
        {{#each this}}
        <li>
            <input type="checkbox" id="menu-{{menuId}}" menuId="{{menuId}}"/>
            <label for="menu-{{menuId}}">{{menuName}}</label>
        </li>
        {{/each}}
    </ul>
</script>
<script type="text/javascript">
    var search_results_template = Handlebars.compile($("#roles").html());
    var menu_template = Handlebars.compile($("#menu-template").html());

    var pageResult = $("#standard_search_results");
    var dialog = $("#update_dialog");
    var updateRoleName = $("#update-roleName");
    var updateRoleId = $("#update-roleId");
    var updateBtn = $("#update-btn");
    var grantDialog = $("#grant_dialog");
    var grantMenuBtn = $("#grant-menu-btn");

    function setupUI(ui, roles) {
        for (var i = 0; i < roles.length; ++i) {
            var item = roles[i];
            var row = ui.find("[roleId='" + item.roleId + "']");
            var action = row.find("[action]");
            if (action.length) {
                action.unbind();
                action.click(
                        { item: item},
                        function (e) {
                            var action = $(this).attr('action');
                            if(action=='delete'){
                                IsmsRequester.requestJson(
                                    "/api/role/" + e.data.item.roleId,
                                    "DELETE",
                                    { },
                                    function (response) {
                                        ui.find("[roleId='" + e.data.item.roleId + "']").remove();
                                        window._response.count--;
                                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
                                    });
                            }

                            if(action=='update'){
                                updateRoleName.val(e.data.item.roleName);
                                updateRoleId.val(e.data.item.roleId).attr('readonly',true);
                                updateBtn.html('Update').unbind();
                                updateBtn.click(e.data,function(){
                                    var roleName = updateRoleName.val();
                                    IsmsRequester.requestJson(
                                            "/api/role/" + e.data.item.roleId,
                                            "PATCH",
                                            { roleName: roleName },
                                            function (response) {
                                                ui.find("[roleId='" + e.data.item.roleId + "'] td:eq(1)").html(roleName);
                                                dialog.dialog('close');
                                            });
                                });
                                dialog.dialog({modal: true,title:'Update Role'});
                            }

                            if(action=='permissions'){
                                grantDialog.dialog({modal: true});
                                //load role menu
                                grantMenuBtn.unbind();

                                IsmsRequester.requestJson("/api/role/"+e.data.item.roleId+"/menus","GET",{},function (response) {
                                    $("#menu-content input").iCheck('uncheck');
                                    for(var index in response){
                                        $("#menu-content input[menuId='"+response[index].menuId+"']").iCheck('check');
                                    }

                                    grantMenuBtn.click(e.data,function () {
                                        var menus = [];
                                        $("#menu-content input:checked").each(function () {
                                            menus.push($(this).attr('menuId'))
                                        });
                                        IsmsRequester.requestJson("/api/role/"+e.data.item.roleId+"/menus",
                                                "POST",
                                                { menus: menus },
                                                function (response) {
                                                    grantDialog.dialog('close');
                                                });
                                    });
                                })


                            }

                        });
            }
        }
    }
    window._response = null;
    function updateSearchResults(namePattern, pageNumber) {
        IsmsRequester.requestJson(
                "/api/roles",
                "POST",
                {
                    roleName: namePattern,
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
        updateRoleName.val('');
        updateRoleId.val('').attr('readonly',false);
        updateBtn.html('Create').unbind();
        updateBtn.click(function(){
            updateRoleName.find('+span').remove();
            updateRoleId.find('+span').remove();

            var roleName = updateRoleName.val();
            var roleId = updateRoleId.val();
            if(roleId.trim()==''){
                updateRoleId.after('<span class="help-block">RoleID cant empty.</span>');
                return;
            }
            if(roleName.trim()==''){
                updateRoleName.after('<span class="help-block">RoleName cant empty.</span>');
                return;
            }
            IsmsRequester.requestJson(
                    "/api/role",
                    "POST",
                    { roleName: roleName,roleId:roleId },
                    function (response) {
                        var datas = [{ roleName: roleName,roleId:roleId }];
                        $("#standard_search_results tbody").prepend($(search_results_template({results:datas})).find("tbody tr:first"));
                        setupUI(pageResult, datas);
                        dialog.dialog('close');
                        window._response.count++;
                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
                    });
        });
        dialog.dialog({modal: true,title:'Create Role'});
    });

    updateSearchResults('',0);
    
    //load allMenu
    IsmsRequester.requestJson('/api/menus','GET',{},function (response) {
        $("#menu-content").html(menu_template(response)).find('input').iCheck({
            checkboxClass: 'icheckbox_flat-blue'
        });
    });
</script>
</body>
</html>