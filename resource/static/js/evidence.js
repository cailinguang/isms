var EvidenceUploader = function (classType) {
    this.container = $("#upload-evidence");
    this.form = $("#upload-evidence-form");
    this.upload = $("#upload-evidence-button");
    this.result = $("#update-evidence-result");
    this.treeInput = $("#choice-lassType");
    this.classId = $("#classId");
    this.tree = $("#upload-tree");
    this.evidence = undefined;
    this.classType = classType;
}

EvidenceUploader.prototype.openDialog = function (auto_close, newEvidenceCallback,node) {
    if(node){
        this.treeInput.val(node.text);
        this.classId.val(node.id);
    }

    //init tree
    this.treeInput.on('click',function () {
        //init tree
        new IsmsDataTree({
            view: this.tree,
            type: this.classType,
            readonly:true,
            selectRoot:false,
            initSelectNode:node?node.id:undefined,
            selectionCallback:function (node) {
                this.tree.dialog('close');
                this.treeInput.val(node.text);
                this.classId.val(node.id);
            }.bind(this)
        }).render();;

        this.tree.dialog({
            modal: true,
            height: 400,
            width: 500,
            buttons: {
                Close: function () {
                    $(this).dialog("close");
                },
            },
        });
    }.bind(this));

    this.upload.unbind();
    this.upload.click(function (e) {
        e.preventDefault();
        e.stopImmediatePropagation();
        this.result.html("Uploading...");
        this.form.ajaxForm({
            success: function (evidence) {
                evidence.classId = this.classId;
                this.evidence = evidence;
                this.result.html(evidence.name + " has been uploaded successfully.");
                newEvidenceCallback(evidence);
                if (auto_close) {
                    $(this.container).dialog("close");
                }
            }.bind(this),
            error: function (xhr, status, error) {
            	resp = JSON.parse(xhr.responseText);
                this.result.html("Upload Failed: " + resp.message);
            }.bind(this),
            dataType: "json"
        }).submit();
        e.preventDefault();
    }.bind(this));
    this.container.dialog({
        modal: true,
        height: 500,
        width: 400,
        //position: { my: "top", at: "top+250"},
        buttons: {
            Close: function () {
                $(this).dialog("close");
            },
        },
    });
}

