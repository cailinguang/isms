<html>
<head>
    <meta charset="utf-8">
    <title>Network Security</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div id="security-content"></div>
</div>

<script id="security-template" type="text/x-handlebars-template">
    {{#each this}}
    <div class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-body">
                <button type="button" class="btn btn-link btn-block" onclick="location.href='/view_security?target={{this}}'">{{this}}</button>
            </div>
        </div>

        <#--<div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">{{this}}</h3>
            </div>
            <div class="panel-body">
                <button type="button" class="btn btn-link" action="view" onclick="location.href='/view_security?target={{this}}'">View</button>
                <button type="button" class="btn btn-link" action="import" onclick="location.href='/import_security?target={{this}}'">Import</button>
            </div>
        </div>-->
    </div>
    {{/each}}
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_network_security_tree").addClass("active");
    var security_template = Handlebars.compile($("#security-template").html());

    IsmsRequester.requestJson('/api/properties/network-security/','GET',{},function (response) {
        $("#security-content").html(security_template(response));
    })
</script>
</body>
</html>