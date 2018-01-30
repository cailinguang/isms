<html>
<head>
    <meta charset="utf-8">
    <title>Admin</title>
<#include "common_css.ftl">
</head>
<body>
<div style="display:none" id="create_user" title="Create User" height="300" width="400">
    <div class="input-group">
        <span class="input-group-addon">User Name</span>
        <input type="text" id="create-user-name" class="form-control"/>
    </div>
    <div style="margin-top: 10px" align="right">
        <button type="submit" class="btn btn-primary" id="create-user-button">Create</button>
    </div>
    <div style="margin-top: 10px" id="create-user-result"></div>
</div>
<div class="container">
<#include "header.ftl">
    <div class="row">
        <div class="col-md-9 input-group">
            <input id="search_string" type="text" class="form-control">
            <div class="input-group-btn">
                <button id="search_button" class="btn btn-default">
                    <span class="glyphicon glyphicon-search"></span>
                    Search
                </button>
                <button id="create_user_link" class="btn btn-default">
                    <span class="glyphicon glyphicon-user"></span>
                    Create User
                </button>
                <button id="create_readonly_user_link" class="btn btn-default">
                    <span class="glyphicon glyphicon-user"></span>
                    Create ReadOnly User
                </button>
            </div>
        </div>
    </div>
    <div class="row">
        <div id="search_results"></div>
    </div>
</div>
<script id="users" type="text/x-handlebars-template">
    <table id="user_table" class="table">
        <tr>
            <th>User Name</th>
            <th>Role</th>
            <th>Name</th>
            <th>Department</th>
            <th>Tel</th>
            <th>Email</th>
            <th></th>
        </tr>
        {{#each results}}
        <tr user="{{this.userName}}" realname="{{this.realName}}" department="{{this.department}}" tel="{{this.tel}}" email="{{this.email}}">
            <td>{{this.userName}}</td>
            <td>{{this.role}}</td>
            <td>{{this.realName}}</td>
            <td>{{this.department}}</td>
            <td>{{this.tel}}</td>
            <td>{{this.email}}</td>
            <td>
                <a href="javascript:void(0)" action="reset">Reset Password</a>
                <span>|</span>
                <a href="javascript:void(0)" action="update">Update Profile</a>
                {{#ifCond this.role "ROLE_ADMIN"}}
                {{else}}
                <span>|</span>
                <a href="javascript:void(0)" action="delete">Delete</a>
                {{/ifCond}}
            </td>
        </tr>
        {{/each}}
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td>{{#if hasPrevPage}}<a id="prevPage" href="javascript:void(0)">Prev</a>{{/if}}</td>
            <td>{{#if hasNextPage}}<a id="nextPage" href="javascript:void(0)">Next</a>{{/if}}</td>
        </tr>
    </table>
</script>
<script id="update_user_profile" type="text/x-handlebars-template">
    <div title="Update User Profile">
        <form>
            <div class="form-group">
                <label>Name</label>
                <input type="text" class="form-control" id="update-user-realname" value="{{realname}}"/>
                <label>Department</label>
                <input type="text" class="form-control" id="update-user-department" value="{{department}}"/>
                <label>Tel</label>
                <input type="text" class="form-control" id="update-user-tel" value="{{tel}}"/>
                <label>Email</label>
                <input type="text" class="form-control" id="update-user-email" value="{{email}}"/>
            </div>
        </form>
    </div>
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    Handlebars.registerHelper('ifCond', function(v1, v2, options) {
        if(v1 === v2) {
            return options.fn(this);
        }
        return options.inverse(this);
    });
    var search_results_template = Handlebars.compile($("#users").html());

    function updateSearchResults(namePattern, pageNumber) {
        IsmsRequester.requestJson(
                "/api/admin/users",
                "POST",
                {
                    namePattern: namePattern,
                    pageNumber: pageNumber,
                    itemPerPage: 10,
                },
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    $("#search_results").html(search_results_template(response));
                    var prevPage = $("#prevPage");
                    if (prevPage.length && response.pageNumber > 0) {
                        prevPage.click(function () {
                            updateSearchResults(namePattern, standardType, pageNumber - 1);
                        });
                    }
                    var nextPage = $("#nextPage");
                    if (nextPage.length && response.hasNextPage) {
                        nextPage.click(function () {
                            updateSearchResults(namePattern, standardType, pageNumber + 1);
                        });
                    }
                    setupUserTable();
                }
        );
    }

    function openCreateUserDialog(readonly) {
        var create_user = $("#create-user-button");
        var create_user_form = $("#create-user-form");
        var create_user_result = $("#create-user-result");
        create_user.unbind();
        create_user.click(function (e) {
            IsmsRequester.requestJson(
                    "/api/admin/createuser",
                    "POST",
                    {
                        userName: $("#create-user-name").prop("value"),
                        readonly: readonly
                    },
                    function (user) {
                        $("#create-user-result").html(
                                "Successfully created user: " + user.userName + ", password: " + user.password
                        );
                    }
            );
        });
        $("#create_user").dialog({
            modal: true,
            height: $("#create_user").attr("height"),
            width: $("#create_user").attr("width"),
            buttons: {
                Close: function () {
                    $(this).dialog("close");
                },
            },
        });
    }

    function resetPassword(username) {
        IsmsRequester.requestJson(
                "/api/admin/users/" + username + "/password",
                "PATCH",
                {
                    userName: username
                },
                function (user) {
                    // TODO: it's a success notification
                    IsmsErrorReporter.reportError("New password for user " + user.userName + ": " + user.password);
                }
        );
    }

    function deleteUser(username) {
        IsmsRequester.requestJson(
                "/api/admin/users/" + username,
                "DELETE",
                {
                    userName: username
                },
                function (user) {
                    // TODO: it's a success notification
                    IsmsErrorReporter.reportError(user.userName + " has been deleted.");
                    var user_table = $("#user_table");
                    var row = user_table.find("[user='" + username + "']");
                    if (row.length) {
                        row.remove();
                    }
                }
        );
    }
    
    function updateUserProfile(data, ui) {
    	IsmsRequester.requestJson(
                "/api/admin/users/" + data.username + "/profile",
                "PATCH",
                {
                    userName: data.username,
                    realName: data.realname,
                    email: data.email,
                    department: data.department,
                    tel: data.tel
                },
                function (user) {
                    var user_table = $("#user_table");
			        var rows = user_table.find("[user]");
			        var row = rows[data.index];
			        var cols = $(row).find("td");
			        $(cols[2]).html(data.realname);
			        $(cols[3]).html(data.department);
			        $(cols[4]).html(data.tel);
			        $(cols[5]).html(data.email);
			        ui.dialog("close");
                }
        );
    	
    }

    function setupUserTable() {
        var user_table = $("#user_table");
        var rows = user_table.find("[user]");
        for (var i = 0; i < rows.length; ++i) {
            var username = $(rows[i]).attr("user");
            var reset_password = $(rows[i]).find("[action='reset']");
            var delete_user = $(rows[i]).find("[action='delete']");
            var update_user = $(rows[i]).find("[action='update']");
            if (reset_password.length) {
                reset_password.click(
                        {username: username},
                        function (e) {
                            resetPassword(e.data.username);
                        }
                );
            }
            if (delete_user.length) {
                delete_user.click(
                        {username: username},
                        function (e) {
                            deleteUser(e.data.username);
                        }
                );
            }
            if (update_user.length) {
            	update_user.click(
            		{index: i,
            		 username: $(rows[i]).attr("user"),
            		 realname: $(rows[i]).attr("realName"),
            		 department: $(rows[i]).attr("department"),
            		 email: $(rows[i]).attr("email"),
            		 tel: $(rows[i]).attr("tel"),
            		},
            		function (e) {
	            		template = Handlebars.compile($("#update_user_profile").html());
	       				$(template(e.data)).dialog({
				            resizable: false,
				            modal: true,
				            buttons: {
				                "Update": function () {
				                	e.data.realname = $(this).find("#update-user-realname").val();
				                	e.data.department = $(this).find("#update-user-department").val();
				                	e.data.email = $(this).find("#update-user-email").val();
				                	e.data.tel = $(this).find("#update-user-tel").val();
				                    updateUserProfile(e.data, $(this));
				                    
				                },
				                "Cancel": function () {
				                    $(this).dialog("close");
				                }
				            }
	        			});
	        		});
            }
        }
    }


    $("#search_button").click(function () {
        var namePattern = $("#search_string").prop("value");
        updateSearchResults(namePattern, 0);
    });

    $("#create_user_link").click(function() {openCreateUserDialog(false);});
    $("#create_readonly_user_link").click(function() {openCreateUserDialog(true);});
    updateSearchResults("", 0);
</script>
</body>
</html>