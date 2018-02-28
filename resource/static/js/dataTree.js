var IsmsDataTree = function (options) {
    this.viewId = options.viewId;
    this.type = options.type;
    this.url = options.url || '/api/data_type/'+this.type+'/all';

    this.view = $(this.viewId);
}

IsmsDataTree.prototype.render = function () {
    IsmsRequester.requestJson(
        this.url,
        "GET",
        {},
        function (datas) {
            this.view.jstree({
                'core': {
                    'multiple':false,
                    'data': []
                }
            });

            this.view.jstree(true).settings.core.data = this.toJSTree(datas);
            this.view.jstree(true).refresh();
            this.view.on('ready.jstree',function (e,data) {
                this.view.jstree(true).open_node('1');
                this.view.jstree(true).select_node('1');
            }.bind(this))

        }.bind(this)
    );
}

IsmsDataTree.prototype.toJSTree = function (datas) {
    var nodes = [];
    for(var i in datas){
        var data = datas[i];
        nodes.push({id:data.classId,parent:data.parentId||'#',text:data.className});
    }
    return nodes;
}
