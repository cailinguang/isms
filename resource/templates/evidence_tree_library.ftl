<html>
<head>
    <meta charset="utf-8">
    <title>Evidence Libraries</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">

    <div class="row">
        <div class="col-md-3">
            <div id="evidence_tree" style="overflow:hidden; height:600px;overflow-y:auto;"></div>
        </div>
        <div class="col-md-9">
            <div class="row">
                <div class="input-group">
                    <input id="search_string" type="text" class="form-control" placeholder="Search for...">
                    <span class="input-group-btn">
                        <button id="search_button" class="btn btn-default" type="button">查找</button>
                    </span>
                </div>
            </div>
            <br/>
            <div class="row" id="standard_search_results"></div>
        </div>
    </div>

</div>
<div title="修改证据" style="display:none" id="evidence_update_dialog">
    <form>
        <label for="evidence_description">描述</label>
        <textarea id="evidence_description" class="form-control"></textarea>
    </form>
    <div align="right">
        <button class="btn btn-primary" id="evidence_update">保存修改</button>
    </div>
</div>
<script id="evidences" type="text/x-handlebars-template">
    <table class="table table-hover">
        <tr>
            <th>描述</th>
            <th>文件</th>
            <th></th>
        </tr>
        {{#each results}}
        <tr item_id="{{id}}">
            <td><span property="description">{{description}}</span>
            	<#if !readonly>
	                <span style="padding:10px; font-size: 8px">
						<a href="javascript:void(0)" action="edit">[edit]</a>
					</span>
				</#if>
            </td>
            <td><a href="/download_evidence/{{id}}" download="{{name}}">{{name}}</a></td>
            <#if !readonly>
	            <td align="center">
	                <button class="btn btn-default" evidence="{{this.id}}" action="delete">删除</button>
	            </td>
            </#if>
        </tr>
        {{/each}}
        <tr>
            <td colspan="3" align="right">
                {{#if hasPrevPage}}<a id="prevPage" href="javascript:void(0)" style="margin-right:20px;">前一页</a>{{/if}}
                {{#if hasNextPage}}<a id="nextPage" href="javascript:void(0)" style="margin-left:30px;">后一页</a>{{/if}}
            </td>
        </tr>
    </table>
</script>

<#include "upload_evidence.ftl">
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_evidence_library_tree").addClass("active");
    var search_results_template = Handlebars.compile($("#evidences").html());

    function setupDeleteAction(ui, result) {
        var action = ui.find("[evidence='" + result.id + "'][action='delete']");
        action.unbind();
        action.click(function() {
            IsmsRequester.requestJson(
                    "/api/properties/evidences/" + result.id,
                    "DELETE",
                    {},
                    function(response) {
                        ui.find("[item_id='" + result.id + "']").remove();
                    }
            );
        });
    }

    function setupUI(ui, evidences) {
        for (var i = 0; i < evidences.length; ++i) {
            var item = evidences[i];
            var row = ui.find("[item_id='" + item.id + "']");
            var edit = row.find("[action='edit']");
            var descSpan = row.find("[property='description']");
            if (edit.length) {
                edit.unbind();
                edit.click(
                        {
                            descSpan: descSpan,
                            item: item,
                        },
                        function (e) {
                            $("#evidence_description").prop("value", e.data.item.description);
                            $("#evidence_update").unbind();
                            $("#evidence_update").click(
                                    e.data,
                                    function (e) {
                                        var desc = $("#evidence_description").prop("value");
                                        IsmsRequester.requestJson(
                                                "/api/properties/evidences/" + e.data.item.id,
                                                "PATCH",
                                                {
                                                    description: desc,
                                                },
                                                function (response) {
                                                    e.data.descSpan.html(desc);
                                                    $("#evidence_update_dialog").dialog("close");
                                                });
                                    }
                            );
                            $("#evidence_update_dialog").dialog({modal: true});

                        });
            }
        }
    }

    function updateSearchResults(namePattern, pageNumber) {
        IsmsRequester.requestJson(
                "/api/properties/evidences",
                "POST",
                {
                    namePattern: namePattern,
                    itemPerPage: 10,
                    pageNumber: pageNumber
                },
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    $("#standard_search_results").html(search_results_template(response));
                    setupUI($("#standard_search_results"), response.results);
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
                    for (var i = 0; i < response.results.length; ++i) {
                        setupDeleteAction($("#standard_search_results"), response.results[i]);
                    }
                }
        );
    }

    $("#search_button").click(function () {
        var namePattern = $("#search_string").prop("value");
        updateSearchResults(namePattern, 0);
    });

    var newly_uploaded = [];

    $("#upload").click(function () {
        var uploader = new EvidenceUploader();
        uploader.openDialog(true, function (evidence) {
            if (typeof evidence != 'undefined') {
                newly_uploaded.push(evidence);
                $("#newly_uploaded").html(search_results_template({results: newly_uploaded}));
                setupUI($("#newly_uploaded"), newly_uploaded);
                setupDeleteAction($("#newly_uploaded"), evidence);
            }
        });
    });

    updateSearchResults("", 0);
</script>
</body>
</html>