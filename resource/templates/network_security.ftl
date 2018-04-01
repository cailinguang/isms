<html>
<head>
    <meta charset="utf-8">
    <style>
        .target_active{
            color: #333;
            background-color: #e6e6e6 !important;
            border-color: #adadad !important;
        }
    </style>
    <title>Network Security</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div id="security-content"></div>
    <#if !readonly>
    <p class="text-right">
        <button class="btn btn-default" type="button" onclick="location.href='/site'">Back</button>
        <button class="btn btn-default" type="button" id="update">Update</button>
        <#--<button class="btn btn-default" type="button" id="import">Import</button>-->
    </p>
    </#if>

    <div id="security-table-content"></div>
</div>


<div id="security-import" style="display: none;" title="Import">
    <p id="import-result"></p>
    <form id="import-form" method="POST" enctype="multipart/form-data">
        <label>File:</label>
        <input class="form-control-file" name="file" id="file" type="file"/><br/>
        <input class="btn btn-primary" type="submit" value="Import"/>
    </form>
</div>

<script id="security-template" type="text/x-handlebars-template">
    {{#each this}}
    <div class="col-md-3">
        <div class="panel panel-default" target="{{this}}">
            <div class="panel-body">
                <button type="button" class="btn btn-link btn-block" onclick="view_security('{{this}}')">{{this}}</button>
            </div>
        </div>
    </div>
    {{/each}}
</script>
<script id="security-table-template" type="text/x-handlebars-template">
    <table class="table table-condensed table-bordered">
        <thead>
        <th style="width:5%;min-width:50px;">序号</th>
        <th style="width:10%;min-width:80px;">评测对象</th>
        <th style="width:10%;min-width:80px;">测评指标</th>
        <th style="width:45%">控制项</th>
        <th style="width:10%;min-width:80px;">结果记录</th>
        <th style="width:10%;min-width:80px;">符合情况</th>
        <th style="width:10%;">备注</th>
        </th>
        </thead>
        <tbody>
        {{#each this}}
        <tr nid="{{evaluationId}}">
            <td>{{computeAdd  @index 1}}</td>
            <td>{{evaluationTarget}}</td>
            <td>{{evaluationIndex}}</td>
            <td>{{controlItem}}</td>
            <td>{{result}}</td>
            <td style="text-align:center;">
                <input name="conformityCheckBox" type="checkbox" data-on="info" data-on-text="Y" data-off-text="N" data-size="small" {{#isEquals conformity 'Y' }}checked{{/isEquals }} />
            </td>
            <td>{{remark}}</td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    var siteId = '${RequestParameters["siteId"]}';
    var readonly = ${readonly?string('true','false')};

    var security_template = Handlebars.compile($("#security-template").html());

    IsmsRequester.requestJson('/api/properties/network-security/','GET',{},function (response) {
        $("#security-content").html(security_template(response));

        $("#security-content div.panel:first button").trigger('click');
    })


    Handlebars.registerHelper({
        //加
        'computeAdd': function() {
            var big = 0;
            try{
                var len = arguments.length - 1;
                for(var i = 0; i < len; i++){
                    if(arguments[i]){
                        big = eval(big +"+"+ arguments[i]);
                    }
                }
            }catch(e){
                throw new Error('Handlerbars Helper "computeAdd" can not deal with wrong expression:'+arguments);
            }
            return big;
        },
        'isEquals':function(one,two, options) {
            if (one==two) {
                return options.fn(this);
            }
            return options.inverse(this)
        }
    });

    var security_table_template = Handlebars.compile($("#security-table-template").html());

    function view_security(target) {
        $("#security-content div.panel").removeClass("target_active");
        $("#security-content div[target='"+target+"']").addClass("target_active");


        var tdArray = null;
        IsmsRequester.requestJson('/api/properties/network-security/'+siteId+'/'+target,'GET',{},function (response) {
            $("#security-table-content").html(security_table_template(response));

            //tr:td
            tdArray = new Array();
            $("#security-table-content tbody tr").each(function () {
                var tds = new Array();
                $(this).find("td").each(function () {
                    tds.push($(this));
                });
                tdArray.push(tds);
            });


            //rowspan td
            for(var i=0;i<tdArray.length;i++){
                if(i+1<tdArray.length){
                    var td = tdArray[i][1].data('td') || tdArray[i][1];
                    if(td.html()==tdArray[i+1][1].html()){
                        tdArray[i+1][1].remove();
                        tdArray[i+1][1].data('td',td);

                        td.css({textAlign:'center',verticalAlign:'middle'})
                                .attr('rowspan',
                                        td.attr('rowspan')!=undefined ?
                                        parseInt(td.attr('rowspan'))+1 : 2 );
                    }

                    var td1 = tdArray[i][2].data('td') || tdArray[i][2];
                    if(td1.html()==tdArray[i+1][2].html()){
                        tdArray[i+1][2].remove();
                        tdArray[i+1][2].data('td',td1);

                        td1.css({textAlign:'center',verticalAlign:'middle'})
                                .attr('rowspan',
                                        td1.attr('rowspan')!=undefined ?
                                        parseInt(td1.attr('rowspan'))+1 : 2 );
                    }
                }


                //click event
                var clickEvent = function () {
                    var val = $(this).css('padding','0px').html();
                    var textArea = $('<textarea style="width:'+$(this).width()+';height:'+$(this).height()+';overflow: auto;padding:0px;">'+val+'</textarea>');
                    textArea.on('blur',function () {
                        var parent = $(this).parent();
                        parent.attr('value',$(this).val());
                        $(this).unbind('blur').remove();
                        parent.css('padding','5px').html(parent.attr('value'));
                    }).on('click',function () {
                        return false;
                    }).on('keydown',function (event) {
                        //over 255 only delete backspace
                        if($(this).val().length>255 && (event.keyCode!=46 && event.keyCode!=8)) return false;
                    });
                    $(this).empty().append(textArea);
                    textArea.focus();
                };

                tdArray[i][5].find("[name='conformityCheckBox']").bootstrapSwitch();

                if(readonly){
                    tdArray[i][5].find("[name='conformityCheckBox']").bootstrapSwitch('disabled',true);
                    continue;
                }
                tdArray[i][4].on('click',clickEvent);
                tdArray[i][6].on('click',clickEvent);

            }

        });

        //update
        $("#update").unbind().on('click',function () {
            if(tdArray==null){
                return false;
            }

            var data = {networkEvaluations:[],siteId:siteId};
            for(var i=0;i<tdArray.length;i++){
                var networkEvaluation = {};
                networkEvaluation.evaluationId = tdArray[i][0].parent().attr('nid');
                networkEvaluation.result = tdArray[i][4].html();
                networkEvaluation.conformity = tdArray[i][5].find("[name='conformityCheckBox']").bootstrapSwitch('state')?'Y':'N';
                networkEvaluation.remark = tdArray[i][6].html();
                data.networkEvaluations.push(networkEvaluation);
            }
            IsmsRequester.requestJson('/api/properties/network-security/'+siteId+'/'+target,'POST',data,function (response) {
                //location.reload();
                IsmsErrorReporter.reportError("Update Success");
            });

        });

        //import
        $("#import").on('click',function () {
            var $importDiv = $("#security-import");
            var $form = $("#import-form");
            $form.attr("action","/api/properties/network-security/"+target+"/import");

            $form.find("input[type='submit']").unbind('click')
                    .bind('click',function (e) {
                        e.preventDefault();
                        e.stopImmediatePropagation();
                        $form.prev().html("Uploading...");
                        $form.ajaxForm({
                            success: function (evidence) {
                                $form.prev().html(evidence.name + " has been uploaded successfully.");
                                $importDiv.dialog("close");
                            },
                            error: function (xhr, status, error) {
                                resp = JSON.parse(xhr.responseText);
                                $form.prev().html("Upload Failed: " + resp.message);
                            },
                            dataType: "json"
                        }).submit();
                        e.preventDefault();
                    });

            $importDiv.dialog({
                modal: true,
                height: 300,
                width: 400,
                position: { my: "top", at: "top+250"},
                buttons: {
                    Close: function () {
                        $(this).dialog("close");
                    },
                },
            });
        });
    }


</script>
</body>
</html>