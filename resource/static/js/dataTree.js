var IsmsDataTree = function (options) {
    this.viewId = options.viewId;
    this.type = options.type;
    this.url = options.url || '/api/data_type/'+this.type;
    this.selectionCallback = options.selectionCallback;

    this.readonly = options.readonly||false;

    this.view = options.view || $(this.viewId);
    this.datas = null;
    this.selectNodes = [];
}

IsmsDataTree.prototype.render = function () {
    var me = this;
    IsmsRequester.requestJson(
        me.url,
        "GET",
        {},
        function (datas) {
            me.datas = datas;
            me.view.jstree({
                'core': {
                    'multiple':false,
                    'data': me.toJSTree(datas),
                    "check_callback": function (operation, node,
                                                node_parent, node_position, more) {
                        if (operation == "move_node") {
                            return me.handleMove(node, node_parent,
                                node_position);
                        }
                        if (operation == "create_node") {
                            return me.handleCreate(node, node_parent,
                                node_position);
                        }
                        if (operation == "rename_node") {
                            return me.handleRename(node, node_parent,
                                node_position);
                        }
                        if (operation == "delete_node") {
                            return me.handleRemove(node, node_parent,
                                node_position);
                        }
                    }
                },
                "plugins" : [ "wholerow" ,"contextmenu", "dnd"],
                "contextmenu": {
                    "items": function (node) {
                        return me.createContextMenu(node);
                    }
                },
                "dnd": {
                    "copy": false,
                    "drag_selection": false,
                }
            });

            //open and select  root node  on jstree ready.
            me.view.on('ready.jstree',function (e,data) {
                me.view.jstree(true).open_node('1');
                if(!me.readonly){
                    me.view.jstree(true).select_node('1');
                }
            });

            me.view.on('changed.jstree', function (e, data) {
                var i, j, r = [];
                for(i = 0, j = data.selected.length; i < j; i++) {
                    r.push(data.instance.get_node(data.selected[i]));
                }
                me.selectNodes = r;

                if(data.selected.length>0){
                    me.selectionCallback(me.selectNodes[0]);
                }
            });

            me.view.on("rename_node.jstree", function (e, data) {
                var node = data.node;
                IsmsRequester.requestJsonWithFail(me.url+'/nodes/'+node.id, 'PATCH', {
                    text: data.text,
                    parentId: node.parent,
                    nodePosition:node.original.position
                }, me.reloadData.bind(me), me.reloadOldTree.bind(me));
            });
            me.view.on("move_node.jstree", function (e, data) {
                var node = data.node;
                IsmsRequester.requestJsonWithFail(me.url+'/nodes/'+node.id, 'PATCH', {
                    parentId: node.parent,
                    children: me.view.jstree().get_node(node.parent).children
                }, me.reloadData.bind(me), me.reloadOldTree.bind(me));
            });

        }
    );
}

//return contextMenu items
IsmsDataTree.prototype.createContextMenu = function(node){

    if(this.readonly){
        return [];
    }

    var tmp = $.jstree.defaults.contextmenu.items();
    delete tmp.ccp;

    if(node.parent=='#'){
        delete tmp.rename;
        delete tmp.remove;
    }

    return tmp;
}
//return node is can move
IsmsDataTree.prototype.handleMove = function (node, node_parent, node_position) {
    if(this.readonly){
        return false;
    }
    if (node_parent.id == "#") {
        return false;
    }
    return true;
}
//handle create node
IsmsDataTree.prototype.handleCreate = function (node, node_parent, node_position) {
    var me = this;
    IsmsRequester.requestJsonWithFail(me.url+'/nodes', 'POST',
        {
            parentId: node_parent.id,
            nodePosition: node_position,
            text:node.text,
            createNew: true
        },
        me.reloadData.bind(me),
        me.reloadOldTree.bind(me));
    return false;
}

IsmsDataTree.prototype.handleRename = function (node, node_parent, node_position) {
    return true;
}

IsmsDataTree.prototype.handleRemove = function (node, node_parent, node_position) {
    var me = this;
    IsmsRequester.requestJsonWithFail(me.url+'/nodes/'+node.id, 'DELETE',
        {}, me.reloadData.bind(me), me.reloadOldTree.bind(me));
    return true;
}

IsmsDataTree.prototype.getSelectNodeId = function(){
    if(this.selectNodes.length>0){
        return this.selectNodes[0].id;
    }
    return null;
}


IsmsDataTree.prototype.toJSTree = function (datas) {
    var nodes = [];
    for(var i in datas){
        var data = datas[i];
        nodes.push({id:data.classId,parent:data.parentId=='0'?'#':data.parentId,text:data.className,position:data.position});
    }
    return nodes;
}

IsmsDataTree.prototype.reloadData = function (datas) {
    this.view.jstree(true).settings.core.data = this.toJSTree(datas);
    this.view.jstree(true).refresh();
    this.datas = datas;
}

IsmsDataTree.prototype.reloadOldTree = function () {
    this.view.jstree(true).settings.core.data = this.toJSTree(this.datas);
    this.view.jstree(true).refresh();
}