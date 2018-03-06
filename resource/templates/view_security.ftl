<html>
<head>
    <meta charset="utf-8">
    <title>View Security</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <p class="text-right">
        <button class="btn btn-default" type="button" id="update">Update</button>
        <button class="btn btn-default" type="button" id="import">Import</button>
    </p>

    <div id="security-content"></div>
</div>

<script id="security-template" type="text/x-handlebars-template">
    <table class="table table-condensed table-bordered">
        <thead>
        <th style="width:50px;">序号</th>
        <th style="width:100px;">评测对象</th>
        <th style="width:100px;">测评指标</th>
        <th>控制项</th>
        <th style="width:150px;">结果记录</th>
        <th style="width:150px;">符合情况</th>
        <th style="width:150px;">备注</th>
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
            <td>{{conformity}}</td>
            <td>{{remark}}</td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#nav_network_security_tree").addClass("active");
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
        }
    });

    var security_template = Handlebars.compile($("#security-template").html());

    var tdArray = null;

    IsmsRequester.requestJson('/api/properties/network-security/${target}','GET',{},function (response) {
        $("#security-content").html(security_template(response));

        //tr:td
        tdArray = new Array();
        $("#security-content tbody tr").each(function () {
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
            var blurEvent = function () {
                var val = $(this).children().html();
                $(this).html(val);
            };
            tdArray[i][4].on('click',clickEvent);
            tdArray[i][5].on('click',clickEvent);
            tdArray[i][6].on('click',clickEvent);
        }

    });

    //update
    $("#update").on('click',function () {
       if(tdArray==null){
           return false;
       }

       var data = {networkEvaluations:[]};
       for(var i=0;i<tdArray.length;i++){
           var networkEvaluation = {};
           networkEvaluation.evaluationId = tdArray[i][0].parent().attr('nid');
           networkEvaluation.result = tdArray[i][4].html();
           networkEvaluation.conformity = tdArray[i][5].html();
           networkEvaluation.remark = tdArray[i][6].html();
           data.networkEvaluations.push(networkEvaluation);
       }
       IsmsRequester.requestJson('/api/properties/network-security/${target}','POST',data,function (response) {
            location.reload();
       });

    });


</script>
</body>
</html>