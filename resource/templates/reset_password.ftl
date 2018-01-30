<html>
<head>
    <meta charset="utf-8">
    <title>Change Password</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="row">
        <div class="col-md-3" style="color: red" align="center"><#if error??>${error}</#if></div>
    </div>
    <div class="row">
        <div class="col-md-3">
            <form action="/reset_password" method="post">
                <div class="input-group">
                    <span class="input-group-addon" style="width: 120px">Old Password</span>
                    <input type="password" class="form-control" name="oldPassword"/>
                </div>
                <div class="input-group" style="margin-top: 10px">
                    <span class="input-group-addon" style="width: 120px">New Password</span>
                    <input type="password" class="form-control" name="newPassword"/>
                </div>
                <div align="right" style="margin-top: 10px">
                    <input type="submit" class="btn btn-primary" value="Reset"/></div>
            </form>
        </div>
    </div>
</div>
<#include "common_js.ftl">
</body>
</html>