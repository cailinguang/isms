<html>
<head>
    <meta charset="utf-8">
    <title>Evidence Libraries</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div>
        <div class="col-md-3">
            <div id="evidence_tree" style="overflow:hidden; height:600px;overflow-y:auto;"></div>
        </div>
        <div class="col-md-9">
            <div class="row">
                <ol class="breadcrumb" id="tree_title">
                </ol>
            </div>

            <div class="row">
                <div class="input-group">
                    <input id="search_string" type="text" class="form-control" placeholder="Search for...">
                    <span class="input-group-btn">
                        <button id="search_button" class="btn btn-default" type="button">查找</button>
                        <button id="upload_button" class="btn btn-default" type="button">上传</button>
                    </span>
                </div>
            </div>
            <br/>
            <div class="row" id="standard_search_results"></div>
        </div>
    </div>

</div>

<div title="Choice" style="display:none" id="update-upload-tree"></div>
<div title="修改证据" style="display:none" id="evidence_update_dialog">
    <form>
        <label>Evidence Directory:</label>
        <input class="form-control" name="classTypeTxt" id="update-choice-lassType">
        <input class="form-control" name="classId" id="update-classId" type="hidden"/>

        <label for="evidence_description">描述</label>
        <textarea id="evidence_description" class="form-control"></textarea>
    </form>
    <div align="right">
        <button class="btn btn-primary" id="evidence_update">保存修改</button>
    </div>
</div>
<script id="evidences" type="text/x-handlebars-template">
    <table class="table table-hover">
        <thead>
            <tr>
                <th style="width:50px !important;">序号</th>
                <th style="width:100px !important;">证据分类</th>
                <th>文件</th>
                <th>描述</th>
                <th style="width:50px !important;"></th>
            </tr>
        </thead>
        <tbody>
            {{#each results}}
            <tr item_id="{{id}}">
                <td>{{calculateIndex @index}}</td>
                <td>{{classType}}</td>
                <td><a href="/download_evidence/{{id}}" download="{{name}}">{{name}}</a></td>
                <td><span property="description">{{description}}</span>
                    <#if !readonly>
                        <span style="padding:10px; font-size: 8px">
                            <a href="javascript:void(0)" action="edit">[edit]</a>
                        </span>
                    </#if>
                </td>
                <#if !readonly>
                    <td style="padding-right:0px;">
                        <button class="btn btn-default" evidence="{{this.id}}" action="delete">删除</button>
                    </td>
                </#if>
            </tr>
            {{/each}}
        </tbody>
        <tfoot>
            <tr>
                <td colspan="5">
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

<#include "upload_evidence.ftl">
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_evidence_library_tree").addClass("active");

    var search_results_template = Handlebars.compile($("#evidences").html());

    //init tree
    var tree = new IsmsDataTree({
        viewId:'#evidence_tree',
        type:'EVIDENCE',
        selectionCallback:function (node) {
            $("#search_string").val('');
            //set title
            $("#tree_title").html('').prepend('<li class="active">'+node.text+'</li>');
            if(node.parent!='#'){
                while(true){
                    var node = tree.view.jstree(true).get_node(node.parent);
                    $("#tree_title").prepend('<li><a href="javacript:void(0);" onclick="clickTreeTitleNode(\''+node.id+'\');">'+node.text+'</a></li>');
                    if(node.parent=='#') break;
                }
            }
            updateSearchResults("", 0);
        }
    });
    tree.render();

    function clickTreeTitleNode(id) {
        tree.view.jstree(true).deselect_all();
        tree.view.jstree(true).select_node(id);
    }

    function setupDeleteAction(ui, result) {
        var action = ui.find("[evidence='" + result.id + "'][action='delete']");
        action.unbind();
        action.click(function() {
            IsmsRequester.requestJson(
                    "/api/properties/evidences/" + result.id,
                    "DELETE",
                    {classId:result.classId},
                    function(response) {
                        ui.find("[item_id='" + result.id + "']").remove();

                        window._response.count--;
                        $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
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
                            node:tree.getSelectNode()
                        },
                        function (e) {
                            $("#update-classId").val(e.data.node.id);
                            $("#update-choice-lassType").val(e.data.node.text).on('click',function () {
                                new IsmsDataTree({
                                    view: $("#update-upload-tree"),
                                    type: "EVIDENCE",
                                    readonly:true,
                                    selectRoot:false,
                                    initSelectNode:e.data.node.id,
                                    selectionCallback:function (node) {
                                        $("#update-upload-tree").dialog('close');
                                        $("#update-classId").val(node.id);
                                        $("#update-choice-lassType").val(node.text);
                                    }
                                }).render();;

                                $("#update-upload-tree").dialog({
                                    modal: true,
                                    height: 400,
                                    width: 500,
                                    buttons: {
                                        Close: function () {
                                            $(this).dialog("close");
                                        },
                                    },
                                });
                            });

                            $("#evidence_description").prop("value", e.data.descSpan.html());
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
                                                    classId:$("#update-classId").val()
                                                },
                                                function (response) {
                                                    //e.data.descSpan.html(desc);
                                                    updateSearchResults($("#search_string").prop("value"),window._pageNumber);
                                                    $("#evidence_update_dialog").dialog("close");
                                                });
                                    }
                            );
                            $("#evidence_update_dialog").dialog({modal: true});

                        });
            }
        }
    }
    window._pageNumber = 0;
    window._response = null;
    function updateSearchResults(namePattern, pageNumber) {
        var node = tree.getSelectNode();
        window._pageNumber = pageNumber;
        IsmsRequester.requestJson(
                "/api/properties/evidences",
                "POST",
                {
                    namePattern: namePattern,
                    itemPerPage: 10,
                    pageNumber: pageNumber,
                    classId:node.id
                },
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    for(var i=0;i<response.results.length;i++) {response.results[i].classType=node.text;}
                    window._response = response;
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
                        response.results[i].classId = node.id;
                        setupDeleteAction($("#standard_search_results"), response.results[i]);
                    }
                }
        );
    }

    $("#search_button").click(function () {
        var namePattern = $("#search_string").prop("value");
        updateSearchResults(namePattern, 0);
    });


    $("#upload_button").click(function () {
        var uploader = new EvidenceUploader('EVIDENCE');
        uploader.openDialog(true, function (evidence) {
            if (typeof evidence != 'undefined') {
                evidence.classType=$("#choice-lassType").val();
                $("#standard_search_results tbody").prepend($(search_results_template({results: [evidence]})).find('tr:eq(-2)'))
                setupUI($("#standard_search_results"), [evidence]);
                setupDeleteAction($("#standard_search_results"), evidence);
                window._response.count++;
                $("#pageInfo").html(Handlebars.compile('{{page-info this}}')(window._response));
            }
        },tree.getSelectNode());
    });
</script>
</body>
</html>