<script id="root" type="text/x-handlebars-template" resource="vda_root" view="view">
    {{#with properties}}
    <h1>
        VDA: <a href="javascript:void(0)" resource_id="{{../id}}" action="edit">{{name.value}}</a>
    </h1>
    <p class="maturity-level-inline" data-toggle="tooltip" title="{{description.value}}">
        Maturity Level: {{formatScore ../score}}
    </p>
    {{/with}}
    {{#each children}}
    {{> chapter}}
    {{/each}}
</script>

<script id="edit_root" type="text/x-handlebars-template" resource="vda_root" view="edit">
    <div title="{{properties.name.value}}">
        <form>
            <div class="form-group">
                <label>Name</label>
                <textarea class="form-control" property="name" <#if readonly>readonly</#if>></textarea>
            </div>
        </form>
    </div>
</script>