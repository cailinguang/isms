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

    <form class="form-inline">
        <div class="form-group col-md-3">
            <div class="input-group" style="width: 200px;">
                <span class="input-group-addon">评估对象：</span>
                <select class="form-control selectpicker" id="evaluationObjectSelect" title="please select">
                </select>
            </div>
        </div>
        <div class="form-group col-md-6">
            <div class="input-group">
                <span class="input-group-addon">@</span>
                <select class="selectpicker" multiple data-live-search="true" id="categorySelect" title="please select">
                </select>
            </div>

        </div>
    </form>

</div>

<script id="security-template" type="text/x-handlebars-template">
    {{#each this}}
    <div class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-body">
                <button type="button" class="btn btn-link btn-block" onclick="location.href='/view_security?target={{this}}'">{{this}}</button>
            </div>
        </div>

    </div>
    {{/each}}
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_risk_library").addClass("active");
    var security_template = Handlebars.compile($("#security-template").html());

    var risk_map = null;
    IsmsRequester.requestJson('/api/risk_library','POST',{},function (response) {
        risk_map = response;

        var evaluationObjectHTML='';
        for(var key in risk_map){
            var v = html_encode(key);
            evaluationObjectHTML+='<option value="'+v+'">'+v+'</option>';
        }
        $("#evaluationObjectSelect").html(evaluationObjectHTML).selectpicker('refresh').unbind('changed.bs.select').on('changed.bs.select',function (e) {
            //load next select
            var key = $(this).val();
            var list = risk_map[key];
            var categorySelectHTML = '';
            for(var index=0;index<list.length;index++){
                var n = html_encode(list[index].name);
                categorySelectHTML += '<option value="'+n+'">'+n+'</option>';
            }
            $("#categorySelect").html(categorySelectHTML).selectpicker('refresh').unbind('changed.bs.select').on('changed.bs.select',function (e) {
                alert($(this).selectpicker('val'))
            });
        });
    });


    //编码
    function html_encode(str)
    {
        var s = "";
        if (str.length == 0) return "";
        s = str.replace(/&/g, ">");
        s = s.replace(/</g, "<");
        s = s.replace(/>/g, ">");
        s = s.replace(/ /g, " ");
        s = s.replace(/\'/g, "'");
        s = s.replace(/\"/g, "\"");
        s = s.replace(/\n/g, "<br>");
        return s;
    }

    //解码
    function html_decode(str)
    {
        var s = "";
        if (str.length == 0) return "";
        s = str.replace(/>/g, "&");
        s = s.replace(/</g, "<");
        s = s.replace(/>/g, ">");
        s = s.replace(/ /g, " ");
        s = s.replace(/'/g, "\'");
        s = s.replace(/"/g, "\"");
        s = s.replace(/<br>/g, "\n");
        return s;
    }
</script>
</body>
</html>