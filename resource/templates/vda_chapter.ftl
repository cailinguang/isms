<script id="chapter" type="text/x-handlebars-template" resource="vda_chapter" view="view">
    {{#with properties}}
    <h2>
        <a href="javascript:void(0)" resource_id="{{../id}}" action="edit">{{name.value}}</a>
    </h2>
    <p class="maturity-level-inline" data-toggle="tooltip" title="{{description.value}}">
        Maturity Level: {{formatScore ../score}}
    </p>
    {{/with}}
    <table class="table table-bordered">
        {{#each children}}
        <tr><td>{{> question}}</td></tr>
        {{/each}}
    </table>
</script>

<script id="edit_chapter_template" type="text/x-handlebars-template" resource="vda_chapter" view="edit">
    <div title="{{properties.name.value}}">
        <form>
            <div class="form-group">
                <label>Comment</label>
                <textarea rows="5" class="form-control" property="comment" <#if readonly>readonly</#if>></textarea>
            </div>
        </form>
    </div>
</script>