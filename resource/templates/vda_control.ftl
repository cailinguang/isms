<script id="control" type="text/x-handlebars-template"
        resource="vda_control" view="view">
    {{#with properties}}
    <tr>
        <td style="padding-left: {{../view.padding_left}}">
            {{#if compliance}}
            <a href="javascript:void(0)" resource_id="{{../id}}" action="edit">{{name.value}}</a>
            {{else}}
            {{name.value}}
            {{/if}}
            <br/>
            <span class="maturity-level-inline" data-toggle="tooltip" title="{{description.value}}">
                Maturity Level: {{formatScore ../score}}
            </span>
        </td>
        {{#if compliance}}
        <td>{{#formatCompliance this}}{{/formatCompliance}}</td>
        <td>{{evidences.value.length}}</td>
        {{else}}
        <td></td>
        <td></td>
        {{/if}}
    </tr>
    {{/with}}
    {{#each children}}
    {{> control}}
    {{/each}}
</script>



<script id="edit_control_template" type="text/x-handlebars-template"
        resource="vda_control" view="edit">
    <div title="{{properties.name.value}}" width="600">
        {{#with properties}}
        {{#if complianceLevel}}
        <div class="complianceLevel">
            <label>Compliance Level</label>
            <#if readonly>
            	<input class="form-control" property="complianceLevel" readonly/>
            <#else>
            	<select class="form-control" property="complianceLevel"/>
            </#if>
        </div>
        {{/if}}
        {{#if compliance}}
        <div class="compliance">
            <label>Compliance</label>
            <input class="form-control" type="text" property="compliance" <#if readonly>readonly</#if>/>
        </div>
        {{/if}}
        <div class="form-group">
            <label>Comment</label>
            <textarea rows="5" class="form-control" property="comment" <#if readonly>readonly</#if>></textarea>
        </div>
        <div class="form-group">
            {{#if evidences}}
            <h4>Evidences</h4>

            <table class="table" property="evidences" view="collection">
                <tr>
                    <th>Description</th>
                    <th>File</th>
                    <th></th>
                </tr>
                {{#each evidences.value}}
                {{> evidence_item}}
                {{/each}}
            </table>
            <#if !readonly>
	            <div class="form-group" align="right">
	                <button class="btn btn-link" href="javascript:void(0)" property="evidences" action="add">Add Existing</button>
	                <button class="btn btn-link" href="javascript:void(0)" property="evidences" action="create">Add New</button>
	            </div>
            </#if>
            {{/if}}
            {{/with}}
        </div>
    </div>
</script>