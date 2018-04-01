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
                <button id="export_button" class="btn btn-default" type="button">Export</button>
            </span>
        </div>
    </div>
    <br/>
    <div id="standard_search_results" class="table-responsive"></div>
</div>

<script id="sites" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="min-width:120px;">应用网站名称</th>
                <th style="min-width:120px;">服务器位置</th>
                <th style="min-width:120px;">网站首页URL</th>
                <th style="min-width:80px;">等保定级</th>
                <th style="min-width:80px;">负责部门</th>
                <th style="min-width:80px;">联系人</th>
                <th style="min-width:80px;">联系方式</th>
                <th style="min-width:260px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr siteId="{{siteId}}">
                <td>{{siteName}}</td>
                <td>{{sitePath}}</td>
                <td>{{siteUrl}}</td>
                <td>{{siteGrade}}</td>
                <td>{{siteDept}}</td>
                <td>{{siteContacts}}</td>
                <td>{{siteContactWay}}</td>
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
                <td colspan="8">
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
<div style="display:none;overflow: hidden;" id="update_dialog">
    <form class="form-horizontal">
        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-siteName">网站名称:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-siteName" maxlength="64">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-sitePath">服务器位置:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-sitePath" maxlength="256">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-siteUrl">网站首页URL:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-siteUrl" maxlength="256">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-siteGrade">等保定级:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-siteGrade" maxlength="64">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-siteDept">负责部门:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-siteDept" maxlength="64">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-siteContacts">联系人:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-siteContacts" maxlength="64">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label"  for="update-siteContactWay">联系方式:</label>
            <div class="col-sm-7">
                <input class="form-control" id="update-siteContactWay" maxlength="256">
            </div>
        </div>

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
    var updateSiteContacts = $("#update-siteContacts");
    var updateSiteContactWay = $("#update-siteContactWay");
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
                                updateSiteContacts.val(e.data.item.siteContacts);
                                updateSiteContactWay.val(e.data.item.siteContactWay);

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
                                                siteContacts:updateSiteContacts.val(),
                                                siteContactWay:updateSiteContactWay.val()
                                            },
                                            function (response) {
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(0)").html(response.siteName);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(1)").html(response.sitePath);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(2)").html(response.siteUrl);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(3)").html(response.siteGrade);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(4)").html(response.siteDept);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(5)").html(response.siteContacts);
                                                ui.find("[siteId='" + e.data.item.siteId + "'] td:eq(6)").html(response.siteContactWay);
                                                e.data.item = response;
                                                dialog.dialog('close');
                                            });
                                });
                                dialog.dialog({modal: true,height:500,width:400,title:'Update Site'});
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
                        siteContacts:updateSiteContacts.val(),
                        siteContactWay:updateSiteContactWay.val()
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
        dialog.dialog({modal: true,height:500,width:400,title:'Create Site'});
    });

    updateSearchResults('',0);

    $("#export_button").click(function () {
        window.location.href='/api/export_site?siteName='+$("#search_string").prop("value");
    });
</script>
</body>
</html>