<html>
<head>
    <meta charset="utf-8">
    <title>Network Security</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="row">
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
                    <select class="selectpicker" multiple data-live-search="true" id="categorySelect" title="请选择评估对象">
                    </select>
                </div>

            </div>
        </form>
    </div>
    <hr/>
    <div id="table-content"></div>
</div>

<script id="table-template" type="text/x-handlebars-template">
    <table class="table table-condensed table-bordered">
        <thead>
        <tr>
            <th style="width:10%;min-width:80px;">评估对象</th>
            <th style="width:20%;min-width:80px;">风险源</th>
            <th style="width:10%;min-width:80px;">后果</th>
            <th style="width:40%">风险源说明</th>
            <th style="width:20%;min-width:80px;">附录A条款</th>
        </tr>
        </thead>
        <tbody>
        {{#each this}}
        <tr nid="{{id}}">
            <td>{{evaluationObject}}</td>
            <td>{{riskSource}}</td>
            <td>{{consequence}}</td>
            <td>{{riskDescripion}}</td>
            <td>{{appendix}}</td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_risk_library").addClass("active");
    var table_template = Handlebars.compile($("#table-template").html());

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
                var tableList = new Array();
                var selectVals = $(this).selectpicker('val');
                for(var v in selectVals){
                    for(var i in list){
                        if(html_encode(list[i].name)==selectVals[v]){
                            tableList = riskAryConcatFilterEquals(tableList,list[i].risks);
                            break;
                        }
                    }
                }
                $("#table-content").html(table_template(tableList));

                //tr:td
                var tdArray = new Array();
                $("#table-content tbody tr").each(function () {
                    var tds = new Array();
                    $(this).find("td").each(function () {
                        tds.push($(this));
                    });
                    tdArray.push(tds);
                });
                //rowspan td
                for(var i=0;i<tdArray.length;i++) {
                    if (i + 1 < tdArray.length) {
                        var td = tdArray[i][0].data('td') || tdArray[i][0];
                        if (td.html() == tdArray[i + 1][0].html()) {
                            tdArray[i + 1][0].remove();
                            tdArray[i + 1][0].data('td', td);

                            td.css({textAlign: 'center', verticalAlign: 'middle'})
                                    .attr('rowspan',
                                            td.attr('rowspan') != undefined ?
                                                    parseInt(td.attr('rowspan')) + 1 : 2);
                        }
                    }
                };
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

    function riskAryConcatFilterEquals(source,target){
        var tempAdd = new Array();
        for(var i in target){
            var targetRisk = target[i];
            var isContain = false;
            for(var j in source){
                var sourceRisk = source[j];
                isContain =   targetRisk.evaluationObject == sourceRisk.evaluationObject
                        && targetRisk.riskSource == sourceRisk.riskSource
                        && targetRisk.consequence == sourceRisk.consequence
                        && targetRisk.riskDescripion == sourceRisk.riskDescripion
                        && targetRisk.appendix == sourceRisk.appendix;
                if(isContain) {
                    break;
                }
            }
            if(!isContain){
                tempAdd.push(targetRisk);
            }
        }
        return source.concat(tempAdd);
    }
</script>
</body>
</html>