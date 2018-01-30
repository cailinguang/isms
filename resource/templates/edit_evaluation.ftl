<html>
<head>
    <meta charset="utf-8">
    <title>Evaluation</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="row">
        <div class="col-md-3">
            <div id="vda_tree" style="overflow:scroll; height:600px"></div>
        </div>
        <div class="col-md-9">
            <div style="position: fixed; bottom: 5px; right: 120px;">
                <a style="color: black; opacity: 1" href="/score_evaluation?id=${standardId}">
                    <span class="glyphicon glyphicon-circle-arrow-right"></span>
                    Dashboard
                </a>
            </div>
            <div id="vda_view"></div>
        </div>
    </div>
</div>
<#include "vda_root.ftl">
<#include "vda_chapter.ftl">
<#include "vda_question.ftl">
<#include "vda_level.ftl">
<#include "vda_control.ftl">
<#include "iso27001_root.ftl">
<#include "iso27001_chapter.ftl">
<#include "iso27001_question.ftl">
<#include "iso27001_control.ftl">
<#include "evidence.ftl">
<#include "upload_evidence.ftl">
<#include "common_js.ftl">
<script type="text/javascript">
    $(document).ready(function(){
        $('[data-toggle="tooltip"]').tooltip();
    });
    Handlebars.registerHelper('formatCompliance', function(context, options) {
        if (context.complianceLevel.value != "OTHER") {
            return context.complianceLevel.displayValue;
        } else {
            return context.compliance.value;
        }
    });
    Handlebars.registerHelper('formatScore', function(context, options) {
        return Math.round(context * 100) / 100;
    });
    Handlebars.registerHelper('ifCond', function(v1, v2, options) {
        if(v1 === v2) {
            return options.fn(this);
        }
        return options.inverse(this);
    });
    TemplateManager.registerTemplates();
    var view = new IsmsEvaluationView($("#vda_view"), $("#vda_tree"), 3, "/api/standards/${standardId}/nodes/0/properties", ${readonly?c});
    view.annotate_resource = function (resource, depth) {
        if ($.inArray(resource.type, ["vda_level1", "vda_level2", "vda_level3"]) >= 0) {
            resource.type = "vda_level";
        } else if (resource.type == "vda_control") {
            var padding_left = depth * 10 + "px";
            resource.view = {padding_left: padding_left};
        } else if (resource.type == "iso27001_control") {
            var padding_left = depth * 10 + "px";
            resource.view = {padding_left: padding_left};
        }
        return resource;
    }
    view.customizeEditor = function (editor, resource) {
        if (resource.type == "vda_control" || resource.type == "iso27001_control") {
            console.log(resource);
            if (resource.properties.complianceLevel.value != "OTHER") {
                editor.find("[class='compliance']").hide();
            }
            editor.find("[class='complianceLevel']").unbind();
            editor.find("[class='complianceLevel']").change(function (e) {
                if (e.target.value != "OTHER") {
                    editor.find("[class='compliance']").hide();
                } else {
                    editor.find("[class='compliance']").show();
                }
            });
        }
    };
    view.render();
</script>
</body>
</html>
