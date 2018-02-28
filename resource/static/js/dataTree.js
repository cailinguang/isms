var IsmsDataTree = function (options) {
    this.view = $(options.viewId);
    this.id = options.id;
    this.type = options.type;
    this.resource_uri = options.resource_uri;
}

IsmsDataTree.prototype.render = function () {
    IsmsRequester.requestJson(
        this.resource_uri,
        "GET",
        {},
        function (data) {
            this.view.jstree({
                'core': {
                    'data': []
                }
            });

            this.view.jstree(true).settings.core.data = this.toJSTree(data);
        }.bind(this)
    );
}

IsmsDataTree.prototype.toJSTree = function (data) {
    var nodes = [];
    this.doToJSTree(this.resource, "#", nodes, 1, this.max_depth);
    return nodes;
}