<html>
<head>
    <meta charset="utf-8">
    <title>Admin</title>
<#include "common_css.ftl">
</head>
<body>
<div style="display:none;overflow-x:hidden;" id="create_user" title="Create User">
    <form class="form-horizontal">
        <div class="form-group">
            <label for="create-user-name" class="col-sm-4 control-label">User Name</label>
            <div class="col-sm-7">
                <input type="text" id="create-user-name" class="form-control"/>
            </div>
        </div>
        <div class="form-group">
            <label for="create-user-department" class="col-sm-4 control-label">Department</label>
            <div class="col-sm-7">
                <select class="form-control selectpicker" id="create-user-department" title="please select"  data-live-search="true">
                    <#list depts as d>
                        <option value="${d.deptId}">${d.deptName}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="create-userType-u" class="col-sm-4 control-label">UserType</label>
            <div class="col-sm-7" style="margin-top:8px;">
                <input type="radio" id="create-userType-u" name="create-userType">
                <label for="create-userType-u">User</span></label>
                &nbsp;
                <input type="radio" id="create-userType-uo" name="create-userType">
                <label for="create-userType-uo">ReadOnly_User</span></label>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-4 col-sm-10">
                <button type="button" class="btn btn-primary" id="create-user-button">Create</button>
            </div>
        </div>
        <div style="margin-top: 10px" id="create-user-result"></div>
    </form>
</div>
<div style="display: none;" id="role-dialog" title="Asign Role">
    <div style="margin: 10px;">
        <ul class="list-unstyled">
        <#list roles as r>
            <li>
                <input type="checkbox" id="role-${r.roleId}" roleId="${r.roleId}"/>
                <label for="role-${r.roleId}">${r.roleName}</label>
            </li>
        </#list>
        </ul>
    </div>
</div>
<div class="container">
<#include "header.ftl">
    <div>
        <div class="input-group">
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
            </div>
        </div>
    </div>

    <br/>
    <div id="search_results"></div>
</div>
<script id="users" type="text/x-handlebars-template">
    <table id="user_table" class="table">
        <tr>
            <th>User Name</th>
            <th>UserType</th>
            <th>Name</th>
            <th>Department</th>
            <th>Tel</th>
            <th>Email</th>
            <th></th>
        </tr>
        {{#each results}}
        <tr user="{{this.userName}}" realname="{{this.realName}}" department="{{this.department}}" tel="{{this.tel}}" email="{{this.email}}" role="{{role}}">
            <td>{{this.userName}}</td>
            <td>{{this.role}}</td>
            <td>{{this.realName}}</td>
            <td>{{this.departmentName}}</td>
            <td>{{this.tel}}</td>
            <td>{{this.email}}</td>
            <td>
                <a href="javascript:void(0)" action="reset">Reset Password</a>
                <span>|</span>
                <a href="javascript:void(0)" action="roles">Assign Role</a>
                <span>|</span>
                <a href="javascript:void(0)" action="update">Update Profile</a>
                {{#ifCond this.role "ROLE_Admin"}}
                {{else}}
                <span>|</span>
                <a href="javascript:void(0)" action="delete">Delete</a>
                {{/ifCond}}
            </td>
        </tr>
        {{/each}}
        <tr>
            <td colspan="7" align="right">
                {{#if hasPrevPage}}<a id="prevPage" href="javascript:void(0)" style="margin-right:20px;">Prev</a>{{/if}}
                {{#if hasNextPage}}<a id="nextPage" href="javascript:void(0)" style="margin-left:30px;">Next</a>{{/if}}
            </td>
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
                <select class="form-control" id="update-user-department">
                <#list depts as d>
                    <option value="${d.deptId}" >${d.deptName}</option>
                </#list>
                </select>

                <label>UserType</label><br/>
                <div class="form-control">
                    <label class="radio-inline">
                        <input type="radio" name="update-userType" id="udpate-userType-u" value="false"> User
                    </label>
                    <label class="radio-inline">
                        <input type="radio" name="update-userType" id="udpate-userType-uo" value="true"> ReadOnly_User
                    </label>
                </div>

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
    $("#nav_admin").addClass("active");
    Handlebars.registerHelper('ifCond', function(v1, v2, options) {
        if(v1 === v2) {
            return options.fn(this);
        }
        return options.inverse(this);
    });
    var search_results_template = Handlebars.compile($("#users").html());

    $("select.selectpicker").selectpicker('refresh');
    $("#create_user :radio").iCheck({
        radioClass: 'iradio_square-blue'
    });
    $("#role-dialog input").iCheck({
        checkboxClass: 'icheckbox_square-blue'
    });

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
                            updateSearchResults(namePattern,  pageNumber - 1);
                        });
                    }
                    var nextPage = $("#nextPage");
                    if (nextPage.length && response.hasNextPage) {
                        nextPage.click(function () {
                            updateSearchResults(namePattern,  pageNumber + 1);
                        });
                    }
                    setupUserTable();
                }
        );
    }

    function openCreateUserDialog() {
        var create_user = $("#create-user-button");
        var createUserName = $("#create-user-name");
        var createReadOnly = $("#create-userType-uo");
        var createDepartment = $("#create-user-department");
        var create_user_result = $("#create-user-result");
        create_user.unbind();
        create_user.click(function (e) {

            if(createUserName.prop("value")==''){
                $("#create-user-result").html('please input userName');
                return false;
            }
            if(createDepartment.prop("value")==''){
                $("#create-user-result").html('please select department');
                return false;
            }
            if(createReadOnly.prop('checked')==false&&$("#create-userType-u").prop('checked')==false){
                $("#create-user-result").html('please select readOnly type');
                return false;
            }


            IsmsRequester.requestJson(
                    "/api/admin/createuser",
                    "POST",
                    {
                        userName: createUserName.prop("value"),
                        readonly: createReadOnly.prop('checked'),
                        department: createDepartment.prop('value')
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
            height: 350,
            width: 400,
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
                    tel: data.tel,
                    readonly:data.readonly
                },
                function (user) {
                    var user_table = $("#user_table");
			        var rows = user_table.find("[user]");
			        var row = rows[data.index];
			        var cols = $(row).find("td");
                    $(cols[1]).html(user.role);
			        $(cols[2]).html(user.realname);
			        $(cols[3]).html(user.departmentName);
			        $(cols[4]).html(user.tel);
			        $(cols[5]).html(user.email);
			        data.role = user.role;
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
            var update_role = $(rows[i]).find("[action='roles']");
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
                     role:$(rows[i]).attr("role")
            		},
            		function (e) {
	            		template = Handlebars.compile($("#update_user_profile").html());
	       				$(template(e.data)).dialog({
				            resizable: false,
				            modal: true,
                            open:function (event, ui) {
                                $(this).find("#update-user-department").val(e.data.department);
                                $(this).find("input[name='update-userType'][value='"+(e.data.role=="ReadOnly"?"true":"false")+"']").attr('checked',true);
                            },
				            buttons: {
				                "Update": function () {
				                	e.data.realname = $(this).find("#update-user-realname").val();
				                	e.data.department = $(this).find("#update-user-department").val();
				                	e.data.email = $(this).find("#update-user-email").val();
				                	e.data.tel = $(this).find("#update-user-tel").val();
				                	e.data.readonly = $(this).find("#udpate-userType-uo").prop('checked');
				                    updateUserProfile(e.data, $(this));
				                    
				                },
				                "Cancel": function () {
				                    $(this).dialog("close");
				                }
				            }
	        			});
	        		});
            }
            if(update_role.length>0){
                update_role.click({
                    username: $(rows[i]).attr("user")
                },function (e) {
                    //load user role
                    IsmsRequester.requestJson('/api/admin/users/'+e.data.userName+'/roles','GET',{},function (response) {
                        $("#role-dialog").dialog({
                            modal: true,
                            open:function(event,ui){
                                $(this).find("input").iCheck('uncheck');
                                for(var i in response){
                                    var r = response[i];
                                    $(this).find("input[roleId='"+r.roleId+"']").iCheck('check');
                                }
                            },
                            buttons: {
                                "Update": function () {
                                    var roles = [];
                                    $(this).find("input:checked").each(function () {
                                        roles.push($(this).attr('roleId'))
                                    });
                                    var me = this;
                                    IsmsRequester.requestJson('/api/admin/users/'+e.data.userName+'/role','POST',{roles:roles},function (res) {
                                        $(me).dialog("close");
                                    })

                                },
                                "Cancel": function () {
                                    $(this).dialog("close");
                                }
                            }
                        });
                    });


                })
            }

        }
    }


    $("#search_button").click(function () {
        var namePattern = $("#search_string").prop("value");
        updateSearchResults(namePattern, 0);
    });

    $("#create_user_link").click(function() {openCreateUserDialog();});
    updateSearchResults("", 0);
</script>
</body>
</html>