var IsmsErrorReporter = {
    reportError: function (msg) {
        template = Handlebars.compile($("#alert_dialog").html());
        $(template(msg)).dialog({
            resizable: false,
            modal: true,
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                }
            }
        });
    }
}

/**IsmsRequester**/
var IsmsRequester = {
    requestJson: function (url, action, data, done) {
        var request = $.ajax(
            {
                url: url,
                type: action,
                data: JSON.stringify(data),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
            });
        request.done(done);
        request.fail(function (jqXHR, textStatus) {
            IsmsErrorReporter.reportError(jqXHR.statusText);
        });
    },
    requestJsonWithFail: function (url, action, data, done, fail) {
        var request = $.ajax(
            {
                url: url,
                type: action,
                data: JSON.stringify(data),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
            });
        request.done(done);
        request.fail(function (jqXHR, textStatus) {
            var responseJSON = jqXHR.responseJSON;

            IsmsErrorReporter.reportError((responseJSON!=null?responseJSON.message:false) || jqXHR.statusText);
            fail();
        });
    }
}

/** TemplateManager **/
var TemplateManager = {
    templates: {},

    registerTemplates: function () {
        var scripts = $("script[type='text/x-handlebars-template']");
        for (var i = 0; i < scripts.length; ++i) {
            var template_id = scripts[i].id;
            Handlebars.registerPartial(template_id, $("#" + template_id).html());
            this.templates[template_id] = Handlebars.compile($(
                "#" + template_id).html());
        }
    },

    findTemplateForResource: function (res_type, view) {
        var script = $("script[type='text/x-handlebars-template'][view='"
            + view + "'][resource='" + res_type + "']");
        if (script.length) {
            return this.templates[script.attr('id')];
        }
        console.log("Missing template " + res_type + " " + view);
        return undefined;
    }
}