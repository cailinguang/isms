var IsmsTree = {
    createNew: function (id, readonly) {
        var tree = {};

        tree.id = id;
        tree.metadata = {};
        tree.selection_callbacks = [];
        tree.root = {id: "-1"};
        tree.data = [];
        tree.current_copy = null;
        tree.current_copy_children = null;
        tree.last_parent_id = null;
        tree.readonly = readonly;

        $(id).jstree(
            {
                "core": {
                    "check_callback": function (operation, node,
                                                node_parent, node_position, more) {
                    	if (tree.readonly) {
                    		return false;
                    	}
                        if (operation == "move_node") {
                            return tree.handleMove(node, node_parent,
                                node_position);
                        }
                        if (operation == "create_node") {
                            return tree.handleCreate(node, node_parent,
                                node_position);
                        }
                        if (operation == "rename_node") {
                            return tree.handleRename(node, node_parent,
                                node_position);
                        }
                        if (operation == "delete_node") {
                            return tree.handleRemove(node, node_parent,
                                node_position);
                        }
                        console.log(operation);
                        return false;
                    },
                    "multiple": false,
                },
                "plugins": ["contextmenu", "dnd"],
                "contextmenu": {
                    "items": function (node) {
                    	if (tree.readonly) {
                    		return [];
                    	}
                        return tree.createContextMenu(node);
                    }
                },
                "dnd": {
                    "copy": tree.readonly,
                    "drag_selection": false,
                },
            });

        tree.createContextMenu = function (node) {
            var m = tree.metadata[node.data.type];
            var tmp = $.jstree.defaults.contextmenu.items();
            delete tmp.ccp;
            if (m != null && "create" in m) {
                tmp.create.label = m.create.label;
            } else {
                delete tmp.create;
            }
            if (m != null && "rename" in m) {
                tmp.rename.label = m.rename.label;
            } else {
                delete tmp.rename;
            }
            if (m != null && "remove" in m) {
                tmp.remove.label = m.remove.label;
            } else {
                delete tmp.remove;
            }
            if (m != null && "move" in m) {
                tmp.copy = {};
                tmp.copy.label = "复制";
                tmp.copy.action = function (data) {
                    var inst = $.jstree.reference(data.reference);
                    tree.current_copy = inst.get_node(data.reference);

                }
            }
            if (node.children.length) {
                var child = $(tree.id).jstree(true).get_node(node.children[0]);
                var childMetadata = tree.metadata[child.data.type];
                if (childMetadata != null && "move" in childMetadata) {
                    tmp.copy_children = {};
                    tmp.copy_children.label = "复制子结点";
                    tmp.copy_children.action = function (data) {
                        var inst = $.jstree.reference(data.reference);
                        tree.current_copy_children = inst.get_node(data.reference);
                    }
                }
            }
            if (tree.current_copy != null) {
                var childMetadata = tree.metadata[tree.current_copy.data.type];
                if (childMetadata.move.allowed_parent_types != null &&
                    childMetadata.move.allowed_parent_types.indexOf(node.data.type) >= 0) {
                    tmp.paste = {};
                    tmp.paste.label = "粘贴";
                    tmp.paste.action = function (data) {
                        var inst = $.jstree.reference(data.reference);
                        var parentNode = inst.get_node(data.reference);
                        tree.handleCopy(tree.current_copy, parentNode);
                        tree.current_copy = null;
                    }
                }
            }
            if (tree.current_copy_children != null) {
                if (tree.current_copy_children.data.type == node.data.type) {
                    tmp.paste_children = {};
                    tmp.paste_children.label = "粘贴子结点";
                    tmp.paste_children.action = function (data) {
                        var inst = $.jstree.reference(data.reference);
                        var parentNode = inst.get_node(data.reference);
                        tree.handleCopyChildren(tree.current_copy_children, parentNode);
                        tree.current_copy_children = null;
                    }
                }
            }
            return tmp;
        }

        tree.handleResponse = function (response) {
            tree.reload(response.metadata, response.data);
        }

        tree.handleMove = function (node, node_parent, node_position) {
            var type = tree.metadata[node.data.type];
            if (node_parent.id == "#") {
                return false;
            }
            if (type == null) {
                return false;
            }
            if (type.move == null) {
                return false;
            }
            if (type.move.allowed_parent_types == null) {
                return false;
            }
            if (type.move.allowed_parent_types.indexOf(node_parent.data.type) < 0) {
                return false;
            }
            return true;
        }

        tree.handleCreate = function (node, node_parent, node_position) {
            var parent_type = tree.metadata[node_parent.data.type];
            if (parent_type == null) {
                return false;
            }
            if (parent_type.create == null) {
                return false;
            }
            tree.last_parent_id = node_parent.id;
            IsmsRequester.requestJsonWithFail(
                Handlebars.compile(parent_type.create.uri)(node_parent),
                'POST',
                {
                    parentId: node_parent.id,
                    nodePosition: node_position,
                    createNew: true
                },
                tree.handleResponse,
                tree.reloadOldTree);
            return false;
        }

        tree.handleCopy = function (node, node_parent) {
            var parent_type = tree.metadata[node_parent.data.type];
            tree.last_parent_id = node_parent.id;
            IsmsRequester.requestJsonWithFail(
                Handlebars.compile(parent_type.create.uri)(node_parent),
                'POST',
                {
                    parentId: node_parent.id,
                    srcNodeId: node.id,
                    duplicate: true
                },
                tree.handleResponse,
                tree.reloadOldTree);
        }

        tree.handleCopyChildren = function (node, node_parent) {
            var parent_type = tree.metadata[node_parent.data.type];
            tree.last_parent_id = node_parent.id;
            IsmsRequester.requestJsonWithFail(
                Handlebars.compile(parent_type.create.uri)(node_parent),
                'POST',
                {
                    parentId: node_parent.id,
                    srcNodeId: node.id,
                    appendChildren: true
                },
                tree.handleResponse,
                tree.reloadOldTree);
        }

        tree.handleRename = function (node, node_parent, node_position) {
            var type = tree.metadata[node.data.type];
            if (type == null) {
                return false;
            }
            if (type.rename == null) {
                return false;
            }
            return true;
        }

        tree.handleRemove = function (node, node_parent, node_position) {
            var type = tree.metadata[node.data.type];
            if (type == null) {
                return false;
            }
            if (type.remove == null) {
                return false;
            }
            tree.last_parent_id = node_parent.id;
            IsmsRequester.requestJsonWithFail(Handlebars.compile(type.remove.uri)(node), 'DELETE',
                {}, tree.handleResponse, tree.reloadOldTree);
            return true;
        }

        tree.reload = function (metadata, data) {
            tree.metadata = metadata;
            tree.reloadData(data);
        }

        tree.reloadData = function (data) {
            $(tree.id).jstree(true).settings.core.data = data;
            var new_tree = this.buildTree(data);
            //$(tree.id).jstree(true).refresh();
            this.refreshChanged(this.root, new_tree)
            this.root = new_tree;
            this.data = data;
        }

        tree.refreshChanged = function (old_tree, new_tree) {
            var treeInst = $(tree.id).jstree(true);
            if (!this.nodeEquals(old_tree, new_tree)) {
                if (old_tree.id == "-1") {
                    treeInst.refresh();
                } else {
                    treeInst.refresh(new_tree.id);
                }
            } else {
                for (var k in old_tree.childNodes) {
                    this.refreshChanged(old_tree.childNodes[k], new_tree.childNodes[k]);
                }
            }
        }

        tree.nodeEquals = function (n1, n2) {
            if (n1.id != n2.id) {
                return false;
            }
            if (n1.text != n2.text) {
                return false;
            }
            if (n1.parent != n2.parent) {
                return false;
            }
            if (n1.data.type != n2.data.type) {
                return false;
            }
            if (Object.keys(n1.childNodes).length != Object.keys(n2.childNodes).length) {
                return false;
            }
            for (var k in n1.childNodes) {
                if (!k in n2.childNodes) {
                    return false;
                }
            }
            return true;
        }

        tree.addSelectionCallback = function (callback) {
            tree.selection_callbacks.push(callback);
        }

        tree.buildTree = function (data) {
            var node_index = {};
            for (var i = 0; i < data.length; ++i) {
                var node = data[i];
                node.childNodes = {};
                node_index[node.id] = node;
            }
            for (var id in node_index) {
                var node = node_index[id];
                var parentId = node.parent;
                if (parentId != "#") {
                    var parent = node_index[parentId];
                    parent.childNodes[node.id] = node;
                }
            }
            return node_index["0"];
        }

        tree.reloadOldTree = function () {
            $(tree.id).jstree(true).settings.core.data = tree.data;
            $(tree.id).jstree(true).refresh();
        }

        $(id).on("change.jstree", function (e, data) {
            for (var i = 0; i < tree.selection_callbacks.length; ++i) {
                tree.selection_callbacks[i](e, data);
            }
        });

        $(tree.id).on("rename_node.jstree", function (e, data) {
            var node = data.node;
            IsmsRequester.requestJsonWithFail(Handlebars.compile(tree.metadata[node.data.type].rename.uri)(node), 'PATCH', {
                text: data.text
            }, tree.handleResponse, tree.reloadOldTree);
        });
        $(tree.id).on("move_node.jstree", function (e, data) {
            var node = data.node;
            IsmsRequester.requestJsonWithFail(Handlebars.compile(tree.metadata[node.data.type].move.uri)(node), 'PATCH', {
                parentId: node.parent,
                children: $(tree).jstree().get_node(node.parent).children
            }, tree.handleResponse, tree.reloadOldTree);
        });

        return tree;
    }
}