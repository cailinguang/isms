<script id="level" type="text/x-handlebars-template" resource="vda_level" view="view">
    <table class="table table-hover">
        {{#with properties}}
        <tr>
            <td>
                <a href="javascript:void(0)" resource_id="{{../id}}" action="edit">{{name.value}}</a>
                <br/>
                <span class="maturity-level-inline" data-toggle="tooltip" title="{{description.value}}">
                    Maturity Level: {{formatScore ../score}}
                </span>
            </td>
            <td>Level Approved: {{levelApproved.displayValue}}</td>
        </tr>
        {{/with}}
        {{#if children}}
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
        {{/if}}
    </table>
</script>

<script id="edit_level_template" type="text/x-handlebars-template" resource="vda_level" view="edit">
    <div title="{{properties.name.value}}">
        <form>
            <div class="form-group">
                <label>Level Approved</label>
                <#if readonly>
                	<input class="form-control" property="levelApproved" readonly/>
                <#else>
                	<select class="form-control" property="levelApproved"/>
                </#if>
            </div>
            <div class="form-group">
                <label>Comment</label>
                <textarea rows="5" class="form-control" property="comment" <#if readonly>readonly</#if>></textarea>
            </div>
        </form>
    </div>
</script>