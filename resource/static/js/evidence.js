var EvidenceUploader = function () {
    this.container = $("#upload-evidence");
    this.form = $("#upload-evidence-form");
    this.upload = $("#upload-evidence-button");
    this.result = $("#update-evidence-result");
    this.evidence = undefined;
}

EvidenceUploader.prototype.openDialog = function (auto_close, newEvidenceCallback) {
    this.upload.unbind();
    this.upload.click(function (e) {
        e.preventDefault();
        e.stopImmediatePropagation();
        this.result.html("Uploading...");
        this.form.ajaxForm({
            success: function (evidence) {
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
        height: this.container.attr("height"),
        width: this.container.attr("width"),
        position: { my: "top", at: "top+250"},
        buttons: {
            Close: function () {
                $(this).dialog("close");
            },
        },
    });
}

