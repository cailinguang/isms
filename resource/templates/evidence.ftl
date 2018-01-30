<script id="evidence_item" type="text/x-handlebars-template"
        resource="evidences" view="item">
    <tr item_id="{{id}}">
        <td>{{description}}</td>
        <td>
            <a href="/download_evidence/{{id}}" download="{{name}}">
            {{name}}
            </a>
        </td>
        <#if !readonly>
        	<td><a href="javascript:void(0)" action="remove"><span class='glyphicon glyphicon-remove'></span></a></td>
        </#if>
    </tr>
</script>
<script id="evidence_add_items" type="text/x-handlebars-template"
        resource="evidences" view="search_item">
    <table class="table">
        {{#each results}}
        <tr item_id="{{id}}">
            <td>{{description}}</td>
            <td><a href="/download_evidence/{{id}}" download="{{name}}">{{name}}</a></td>
            <td><a href="javascript:void(0)" action="add"><span class='glyphicon glyphicon-plus'></span></a></td>
        </tr>
        {{/each}}
    </table>
    <div class="form-group" align="right">
        {{#if hasPrevPage}}
        <button class="btn btn-link" href="javascript:void(0)" property="evidences" action="prevPage">Prev</button>
        {{/if}}
        {{#if hasNextPage}}
        <button class="btn btn-link" href="javascript:void(0)" property="evidences" action="nextPage">Next</button>
        {{/if}}
    </div>
</script>
<script id="evidences_add_template" type="text/x-handlebars-template"
        resource="evidences" view="search">
    <div title="Add Evidence" width="600">
        <div class="input-group">
            <input name="search_string" type="text" class="form-control">
            <div class="input-group-addon">
                <span action="search" class="glyphicon glyphicon-search"></span>
            </div>

        </div>
        <div property="evidences">
            {{> evidence_add_items}}
        </div>
    </div>
</script>