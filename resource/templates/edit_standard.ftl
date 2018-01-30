<html>
<head>
    <meta charset="utf-8">
    <title>Edit Standard</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="row">
        <div class="col-md-4">
            <div id="standard-tree" style="overflow:scroll; height:600px"></div>
        </div>
        <div class="col-md-8">
            <div id="tree_properties">

                    <div class="form-group">
                        <label for="name">Name</label>
                        <textarea class="form-control" type="text" id="name" <#if readonly>readonly</#if>></textarea>
                    </div>
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" <#if readonly>readonly</#if>></textarea>
                    </div>
                    <#if !readonly>
                    	<button class="btn btn-primary" id="update">Update</button>
					</#if>
            </div>
        </div>
    </div>
</div>
<#include "alert_dialog.ftl">
<#include "common_js.ftl">
<script>
    var tree = IsmsTree.createNew("#standard-tree", ${readonly?c});
    function updateProperites(resource) {
        var metadata = tree.metadata[resource.type];
        if (metadata != null && "rename" in metadata) {
            $("#name").prop('disabled', false);
        } else {
            $("#name").prop('disabled', true);
        }
        $("#name").prop("value", resource.properties["name"].value);
        $("#description").prop("value", resource.properties["description"].value);
        $("#update").unbind();
        $("#update").click(function () {
            if (metadata != null && "rename" in metadata) {
                var name = $("#name").prop("value");
                if (name != "") {
                    resource.properties["name"].value = name;
                } else {
                    IsmsErrorReporter.reportError("Name must be non empty.");
                    $("#name").prop("value", resource.properties["name"].value);
                    return;
                }
            }
            resource.properties["description"].value = $("#description").prop("value");
            IsmsRequester.requestJson(
                    resource.uri,
                    "PATCH",
                    resource,
                    function (data) {
                        $("#standard-tree").jstree('rename_node', "#" + resource.id , resource.properties["name"].value);                      
                    }
            );
        });
    }
    function linkUpdateToNode(id) {
        IsmsRequester.requestJson(
                "/api/standards/${standardId}/nodes/" + id + "/nodeProperties",
                "GET",
                {},
                function (resource) {
                    updateProperites(resource);
                }
        );
    }

    $("#standard-tree").on(
            'changed.jstree',
            function (e, data) {
                if (data.selected.length) {
                    linkUpdateToNode(data.selected[0]);
                }
            }
    );
    IsmsRequester.requestJson(
            "/api/standards/${standardId}",
            "GET",
            {},
            function (response) {
                tree.reload(response.metadata, response.data);
                linkUpdateToNode("0");
            }
    );

</script>
</body>
</html>