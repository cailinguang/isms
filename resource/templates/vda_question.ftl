<script id="question" type="text/x-handlebars-template" resource="vda_question" view="view"
        xmlns="http://www.w3.org/1999/html">
    {{#with properties}}
    <table class="table">
        <tr>
            <td>
                <a href="javascript:void(0)" resource_id="{{../id}}" action="edit" style="font-weight: bold;">{{name.value}}</a>
                <br/>
                <span class="maturity-level-inline" data-toggle="tooltip" title="{{description.value}}">
                    Maturity Level: {{formatScore ../score}}
                </span>
            </td>
            <td>Severity: {{severity.displayValue}}</td>
            <td>Applicable: {{applicable.displayValue}}</td>
        </tr>
    </table>
    {{/with}}
    {{#ifCond type "vda_question"}}
      {{#each children}}
        {{> level}}
      {{/each}}
    {{else}}
    <table class="table table-hover">
        <tr>
            <td colspan="2">
                <table class="table table-hover" style="margin-left: 20px">
                    <tr>
                        <th>Name</th>
                        <th>Compliance</th>
                        <th>#Evidences</th>
                    </tr>
                    {{#each children}}
                    {{> control}}
                    {{/each}}
                </table>
            </td>
        </tr>
    </table>
    {{/ifCond}}
</script>

<script id="edit_question_template" type="text/x-handlebars-template" resource="vda_question" view="edit">
    <div title="{{properties.name.value}}">
        <form>
            <div class="form-group">
                <label>Severity</label>
                <#if readonly>
                	<input class="form-control" property="severity" readonly/>
                <#else>
                	<select class="form-control" property="severity"/>
                </#if>
            </div>
            <div class="form-group">
                <label>Applicable</label>
                <#if readonly>
                	<input class="form-control" property="applicable" readonly/>
                <#else>
                	<select class="form-control" property="applicable"/>
                </#if>
            </div>
            <div class="form-group">
                <label>Comment</label>
                <textarea rows="5" class="form-control" property="comment" <#if readonly>readonly</#if>></textarea>
            </div>
        </form>
    </div>
</script>