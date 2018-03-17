<html>
<head>
    <meta charset="utf-8">
    <title>WebSite</title>
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

<script id="sites" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:20%;min-width:180px;">互联网应用网站名称</th>
                <th style="width:10%;min-width:150px;">服务器位置</th>
                <th style="width:20%;min-width:150px;">网站首页URL</th>
                <th style="width:10%;min-width:150px;">等保定级</th>
                <th style="width:10%;min-width:150px;">负责部门</th>
                <th style="width:10%;min-width:150px;">联系人</th>
                <th style="width:10%;min-width:260px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr siteId="{{siteId}}">
                <td>{{siteName}}</td>
                <td>{{sitePath}}</td>
                <td>{{siteUrl}}</td>
                <td>{{siteGrade}}</td>
                <td>{{siteDeptName}}</td>
                <td>{{siteContacts}}</td>
                <td style="text-align: center;width:200px;">
                    <button class="btn btn-default" evidence="{{this.id}}" action="evaluation">Evaluation</button>
                    <#if !readonly>
                    <button class="btn btn-default" evidence="{{this.id}}" action="update">Update</button>
                    <button class="btn btn-default" evidence="{{this.id}}" action="delete">Delete</button>
                    </#if>
                </td>
            </tr>
            {{/each}}
        </tbody>
        <tfoot>
            <tr>
                <td colspan="7">
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
        <label for="update-siteId">网站名称:</label>
        <input class="form-control" id="update-siteName">

        <label for="update-siteName">服务器位置:</label>
        <input class="form-control" id="update-sitePath">

        <label for="update-siteName">网站首页URL:</label>
        <input class="form-control" id="update-siteUrl">

        <label for="update-siteName">等保定级:</label>
        <input class="form-control" id="update-siteGrade">

        <label for="update-siteName">负责部门:</label>
        <select class="form-control selectpicker" id="update-siteDept" title="please select"  data-live-search="true">
        <#list depts as d>
            <option value="${d.deptId}">${d.deptName}</option>
        </#list>
        </select>

        <label for="update-siteName">联系人:</label>
        <input class="form-control" id="update-siteContracts">
    </form>
    <div align="right">
        <button class="btn btn-primary" id="update-btn">Update</button>
    </div>
</div>
<#include "common_js.ftl">
<script type="text/javascript">
    var search_results_template = Handlebars.compile($("#sites").html());

    $("select.selectpicker").selectpicker('refresh');

    var pageResult = $("#standard_search_results");
    var dialog = $("#update_dialog");
    var updateSiteName = $("#update-siteName");
    var updateSitePath = $("#update-sitePath");

    var updateSiteUrl = $("#update-siteUrl");
    var updateSiteGrade = $("#update-siteGrade");
    var updateSiteDept = $("#update-siteDept");
    var updateSiteContracts = $("#update-siteContracts");
    var updateBtn = $("#update-btn");

    function setupUI(ui, sites) {
        for (var i = 0; i < sites.length; ++i) {
            var item = sites[i];
            var row = ui.find("[siteId='" + item.siteId + "']");
            var action = row.find("[action]");
            if (action.length) {
                action.unbind();
                action.click(
                        { item: item},
                        function (e) {
                            var action = $(this).attr('action');
                            if(action=='evaluation'){
                                location.href='/network_security?siteId='+e.data.item.siteId;
                            }
                            if(action=='delete'){
                                IsmsRequester.requestJson(
                                    "/api/site",
                                    "DELETE",
                                    {siteId:e.data.item.siteId},
                                    function (response) {
                                        ui.find("[siteId='" + e.data.item.siteId + "']").remove();
                                        window._response.count--;
                                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
                                    });
                            }

                            if(action=='update'){
                                updateSiteName.val(e.data.item.siteName);
                                updateSitePath.val(e.data.item.sitePath);
                                updateSiteUrl.val(e.data.item.siteUrl);
                                updateSiteGrade.val(e.data.item.siteGrade);
                                updateSiteDept.val(e.data.item.siteDept).selectpicker('refresh');
                                updateSiteContracts.val(e.data.item.siteContacts);

                                updateBtn.html('Update').unbind();
                                updateBtn.click(e.data,function(){
                                    IsmsRequester.requestJson(
                                            "/api/site",
                                            "PATCH",
                                            {
                                                siteId: e.data.item.siteId,
                                                siteName: updateSiteName.val(),
                                                sitePath:updateSitePath.val(),
                                                siteUrl:updateSiteUrl.val(),
                                                siteGrade:updateSiteGrade.val(),
                                                siteDept:updateSiteDept.val(),
                                                siteContacts:updateSiteContracts.val()
                                            },
                                            function (response) {
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(0)").html(response.siteName);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(1)").html(response.sitePath);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(2)").html(response.siteUrl);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(3)").html(response.siteGrade);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(4)").html(response.siteDeptName);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(5)").html(response.siteContacts);
                                                e.data.item = response;
                                                dialog.dialog('close');
                                            });
                                });
                                dialog.dialog({modal: true,height:500,width:300,title:'Update Site'});
                            }

                        });
            }
        }
    }
    window._response = null;
    function updateSearchResults(namePattern, pageNumber) {
        IsmsRequester.requestJson(
                "/api/sites",
                "POST",
                {
                    siteName: namePattern,
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
        dialog.find(':input+span').remove();
        dialog.find(':input').val('');

        updateBtn.html('Create').unbind();
        updateBtn.click(function(){
            if(updateSiteName.val().trim()==''){
                updateSiteName.after('<span class="help-block">Site Name cant empty.</span>');
                return;
            }

            IsmsRequester.requestJson(
                    "/api/site",
                    "POST",
                    {
                        siteName: updateSiteName.val(),
                        sitePath:updateSitePath.val(),
                        siteUrl:updateSiteUrl.val(),
                        siteGrade:updateSiteGrade.val(),
                        siteDept:updateSiteDept.val(),
                        siteContacts:updateSiteContracts.val()
                    },
                    function (response) {
                        var datas = [response];
                        $("#standard_search_results tbody").prepend($(search_results_template({results:datas})).find("tbody tr:first"));
                        setupUI(pageResult, datas);
                        dialog.dialog('close');
                        window._response.count++;
                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
                    });
        });
        dialog.dialog({modal: true,height:500,width:300,title:'Create Site'});
    });

    updateSearchResults('',0);
</script>
</body>
</html>