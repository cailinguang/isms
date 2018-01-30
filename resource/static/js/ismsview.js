/** IsmsResourceView **/
var IsmsResourceView = function (container, root_resource, annotate_resource, readonly) {
    this.container = container;
    this.root_resource = root_resource;
    this.annotate_resource = annotate_resource;
    this.resources = {};
    this.readonly = readonly;
    this.customizeEditor = function (dialog, resource) {
    };
    this.modelChanged = function () {
    };
}

IsmsResourceView.prototype.render = function () {
    this.indexResource(this.root_resource, 0);
    var template = TemplateManager.findTemplateForResource(this.root_resource.type, "view");
    this.container.html(template(this.root_resource));
    this.setupActions();
}

IsmsResourceView.prototype.indexResource = function (resource, depth) {
    this.resources[resource.id] = this.annotate_resource(resource, depth);
    for (var i = 0; i < resource.children.length; ++i) {
        var child = resource.children[i];
        this.indexResource(child, depth + 1);
    }
}

IsmsResourceView.prototype.setupActions = function () {
    var edit_actions = this.container.find("[resource_id][action='edit']");
    for (var i = 0; i < edit_actions.length; ++i) {
        var resource = this.resources[$(edit_actions[i]).attr("resource_id")];
        $(edit_actions[i]).click(
            {resource: resource},
            function (e) {
                this.openResourceEditDialog(e.data.resource);
            }.bind(this)
        );
    }
}

IsmsResourceView.prototype.openResourceEditDialog = function (resource) {
    var edit_view = new IsmsResourceEditView(resource, this.readonly);
    edit_view.customizeUI = this.customizeEditor;
    edit_view.modelChanged = function () {
        this.modelChanged();
    }.bind(this);
    edit_view.openDialog();
}


/**IsmsResourceEditView**/
var IsmsResourceEditView = function (resource, readonly) {
    this.resource = resource;
    this.readonly = readonly;
    this.modelChanged = function () {
    };
    this.customizeUI = function (dialog, resource) {
    };
    this.updates = [];
}

IsmsResourceEditView.prototype.openDialog = function () {
    var ui = this.createUI(this.resource);
    if (typeof ui == 'undefined') {
        return;
    }
    if (this.readonly) {
    	this.buttons = {
    			Close: function() { $(this).dialog("close"); }
    	}
    } else {
    	this.buttons = {
            Save: function () {
                    view.applyUpdates();
                    $(this).dialog("close");
            },
            Cancel: function () {
                view.cancelUpdates();
                $(this).dialog("close");
            }
        }
    }
    this.setupActions(ui);
    this.customizeUI(ui, this.resource);
    var view = this;
    ui.dialog({
        modal: true,
        height: ui.attr("height"),
        width: ui.attr("width"),
        position: { my: "top", at: "top+250"},
        buttons: this.buttons
    });
}

IsmsResourceEditView.prototype.createUI = function (resource) {
    var t = TemplateManager.findTemplateForResource(resource.type, 'edit');
    if (typeof t == 'undefined') {
        IsmsErrorReporter.reportError('Missing edit template for ' + resource.type);
        return undefined;
    }
    var ui = $(t(resource)).appendTo("body");
    return ui;
}

IsmsResourceEditView.prototype.setupActions = function (container) {
    for (prop_name in this.resource.properties) {
        var prop = this.resource.properties[prop_name];
        if (typeof prop == 'undefined') {
            continue;
        }
        if (Array.isArray(prop.value)) {
            this.setupCollection(container, prop);
        } else {
            this.setupScalar(container, prop);
        }
    }
}

IsmsResourceEditView.prototype.setupItemUI = function (item_ui, item, prop) {
    $(item_ui).find("[action='remove']").click(
        {
            item_ui: item_ui,
            item: item,
            prop: prop
        },
        function (e) {
            $(e.data.item_ui).remove();
            var prop = e.data.prop;
            var item = e.data.item;
            this.updates.push(
                function () {
                    for (var i = 0; i < prop.value.length; ++i) {
                        if (prop.value[i].id == item.id) {
                            prop.value.splice(i, 1);
                        }
                    }
                }
            )
        }.bind(this)
    );
}

IsmsResourceEditView.prototype.setupCollection = function (container, prop) {
    var collection_ui = container.find("[property='" + prop_name + "'][view='collection']");
    for (var i = 0; i < prop.value.length; ++i) {
        var item = prop.value[i];
        var item_ui = collection_ui.find("[item_id='" + item.id + "']");
        if (item_ui.length) {
            this.setupItemUI(item_ui, item, prop);
        }
    }
    var add_action = container.find("[property='" + prop_name + "'][action='add']");
    if (add_action.length) {
        $(add_action).click(
            {
                prop: prop,
                collection_ui: collection_ui
            },
            function (e) {
                this.openAddItemDialog(e.data.collection_ui, e.data.prop);
            }.bind(this)
        );
    }
    // HACK: only for evidences
    var create_action = container.find("[property='evidences'][action='create']");
    if (create_action.length) {
        $(create_action).click(
            {
                prop: prop,
                collection_ui: collection_ui
            },
            function (e) {
                var collection_ui = e.data.collection_ui;
                var prop = e.data.prop;
                var uploader = new EvidenceUploader();
                uploader.openDialog(true, function (evidence) {
                    if (typeof evidence != 'undefined') {
                        // TODO: duplicated code.
                        var item = evidence;
                        var t = TemplateManager.findTemplateForResource(prop.type, "item");
                        var new_item_ui = $(t(item)).appendTo(collection_ui);
                        this.setupItemUI(new_item_ui, item, prop);
                        this.updates.push(function () {
                            prop.value.push(item);
                        }.bind(this));
                    }
                }.bind(this));
            }.bind(this));
    }
}

IsmsResourceEditView.prototype.setupScalar = function (container, prop) {
    var prop_ui = container.find("[property='" + prop_name + "']");
    if (typeof prop.enumValues != 'undefined' && !this.readonly) {
        for (var e = 0; e < prop.enumValues.length; ++e) {
            var ev = prop.enumValues[e];
            if (ev.value == prop.value) {
                prop_ui.append("<option value='" + ev.value + "' selected>" + ev.displayValue + "</option>");
            } else {
                prop_ui.append("<option value='" + ev.value + "'>" + ev.displayValue + "</option>");
            }
        }
    } else {
        if (prop_ui.is("input")) {
            prop_ui.attr("value", prop.value);
        } else if (prop_ui.is("textarea")) {
            prop_ui.prop("value", prop.value);
        }
    }
    prop_ui.change(
        {
            prop: prop
        },
        function (e) {
            var value = e.target.value;
            var prop = e.data.prop;
            this.updates.push(function () {
                prop.value = value;
            });
        }.bind(this)
    );
}

IsmsResourceEditView.prototype.openAddItemDialog = function (collection_ui, prop) {
    var serach_view = new IsmsCollectionSearchView(this, collection_ui, prop);
    serach_view.openDialog();
}

IsmsResourceEditView.prototype.applyUpdates = function () {
    for (var i = 0; i < this.updates.length; ++i) {
        this.updates[i]();
    }
    this.updates = [];
    IsmsRequester.requestJson(
        this.resource.uri,
        "PATCH",
        this.resource,
        function (data) {
            this.modelChanged();
        }.bind(this));
}

IsmsResourceEditView.prototype.cancelUpdates = function () {
    this.updates = [];
}


/**IsmsCollectionSearchView**/
var IsmsCollectionSearchView = function (collection, collection_ui, prop) {
    this.collection = collection;
    this.collection_ui = collection_ui;
    this.prop = prop;
    this.ui = undefined;
}

IsmsCollectionSearchView.prototype.openDialog = function () {
    var t = TemplateManager.findTemplateForResource(this.prop.type, 'search');
    if (typeof t == 'undefined') {
        IsmsErrorReporter.reportError('Missing add template for ' + this.prop.type);
        return false;
    }
    this.ui = $(t([])).appendTo("body");
    var search_action = this.ui.find("[action='search']");
    var search_text = this.ui.find("[name='search_string']");
    if (search_action.length) {
        var add_container = this.ui.find("[property='" + this.prop.type + "']");
        $(search_action).click(
            function (e) {
                this.refreshDialogItems($(search_text).prop("value"), 0);
            }.bind(this)
        );
    }
    this.refreshDialogItems("")
    this.ui.dialog({
        modal: true,
        height: this.ui.attr("height"),
        width: this.ui.attr("width"),
        position: { my: "top", at: "top+150"},
        buttons: {
            Close: function () {
                $(this).dialog("close");
            },
        },
    });
}

IsmsCollectionSearchView.prototype.refreshDialogItems = function (search_text, pageNumber) {
    IsmsRequester.requestJson(
        this.prop.searchUri,
        "POST",
        {
            namePattern: search_text,
            itemPerPage: 5,
            pageNumber: pageNumber
        },
        function (data) {
            if (data.pageNumber > 0) {
                data.hasPrevPage = true;
            }
            this.setupAddItemView(search_text, data);
        }.bind(this));
}

IsmsCollectionSearchView.prototype.setupAddItemView = function (search_text, data) {
    var items = data.results;
    var items_by_id = {};
    for (var i = 0; i < items.length; ++i) {
        items_by_id["" + items[i].id] = items[i];
    }
    var container = this.ui.find("[property='" + this.prop.type + "']");
    var t = TemplateManager.findTemplateForResource(this.prop.type, 'search_item');
    $(container).html(t(data));
    var item_uis = container.find("[item_id]");
    for (var i = 0; i < item_uis.length; ++i) {
        var item_ui = item_uis[i];
        var add_action = $(item_ui).find("[action='add']");
        if (add_action.length) {
            var item = items_by_id[$(item_ui).attr("item_id")];
            if ($(this.collection_ui).find(
                    "[item_id='" + item.id + "']").length) {
                $(add_action).html("<span class='glyphicon glyphicon-ok'></span>");
            }
            $(add_action).click(
                {
                    add_action: add_action,
                    item: item,
                },
                function (e) {
                    if ($(this.collection_ui).find(
                            "[item_id='" + e.data.item.id + "']").length) {
                        return;
                    }
                    $(e.data.add_action).html("<span class='glyphicon glyphicon-ok'></span>");
                    var t = TemplateManager.findTemplateForResource(this.prop.type, "item");
                    var new_item_ui = $(t(e.data.item)).appendTo(this.collection_ui);
                    this.collection.setupItemUI(new_item_ui, e.data.item, this.prop);
                    var item = e.data.item;
                    this.collection.updates.push(function () {
                        this.prop.value.push(item);
                    }.bind(this));
                }.bind(this)
            );
        }
    }
    var nextPage = container.find("[action='nextPage']");
    var prevPage = container.find("[action='prevPage']");
    if (nextPage.length && data.hasNextPage) {
        nextPage.click(function () {
            this.refreshDialogItems(search_text, data.pageNumber + 1);
        }.bind(this));
    }
    if (prevPage && data.hasPrevPage) {
        prevPage.click(function () {
            this.refreshDialogItems(search_text, data.pageNumber - 1);
        }.bind(this));
    }
}

/**IsmsTreeView**/
var IsmsTreeView = function (evaluation_view, tree_view, resource, max_depth) {
    this.evaluation_view = evaluation_view;
    this.tree_view = tree_view;
    this.resource = resource;
    this.max_depth = max_depth;
}

IsmsTreeView.prototype.render = function () {
    this.tree_view.jstree({
        'core': {
            'data': []
        }
    });
    this.tree_view.jstree(true).settings.core.data = this.toJSTree();
    this.tree_view.jstree(true).refresh();
    this.tree_view.on(
        'changed.jstree',
        function (e, data) {
            if (data.selected.length) {
                var res_id = data.instance.get_node(data.selected[0]).data;
                this.evaluation_view.render_resource_id = res_id;
                this.evaluation_view.renderResource();
            }
        }.bind(this)
    );
}

IsmsTreeView.prototype.toJSTree = function () {
    var nodes = [];
    this.doToJSTree(this.resource, "#", nodes, 1, this.max_depth);
    return nodes;
},

    IsmsTreeView.prototype.doToJSTree = function (root, parent, nodes, depth) {
        if (depth > this.max_depth) {
            return;
        }
        nodes.push({
            id: root.id,
            parent: parent,
            text: root.properties.name.value,
            data: root.id
        });
        for (var i = 0; i < root.children.length; ++i) {
            this.doToJSTree(root.children[i], root.id, nodes, depth + 1);
        }
    }

/**IsmsEvaluationView**/
var IsmsEvaluationView = function (root_view, tree_view, tree_depth, root_resource_uri, readonly) {
    this.root_view = root_view;
    this.tree_view = tree_view;
    this.tree_depth = tree_depth;
    this.root_resource_uri = root_resource_uri;
    this.render_resource_id = 0;
    this.root_resource = undefined;
    this.readonly = readonly;
    this.customizeEditor = function (editor, resource) {
    };
    this.annotate_resource = function (resource, depth) {
        return resource;
    };
}

IsmsEvaluationView.prototype.render = function () {
    IsmsRequester.requestJson(
        this.root_resource_uri,
        "GET",
        {},
        function (data) {
            this.root_resource = data.resource;
            if (this.render_resource_id == 0) {
                this.render_resource_id = data.resource.id;
            }
            var tree = new IsmsTreeView(this, this.tree_view, data.resource, this.tree_depth);
            tree.render();
            this.renderResource();
        }.bind(this));
}

IsmsEvaluationView.prototype.renderResource = function () {
    var resource = this.findResource(this.root_resource, this.render_resource_id);
    var view = new IsmsResourceView(this.root_view, resource, this.annotate_resource, this.readonly);
    view.customizeEditor = this.customizeEditor;
    view.modelChanged = function () {
        this.render();
    }.bind(this);
    view.render();
}

IsmsEvaluationView.prototype.findResource = function (resource, res_id) {
    if (resource.id == res_id) {
        return resource;
    }
    for (var i = 0; i < resource.children.length; ++i) {
        var child = resource.children[i];
        var result = this.findResource(child, res_id);
        if (typeof result != 'undefined') {
            return result;
        }
    }
    return undefined;
}
