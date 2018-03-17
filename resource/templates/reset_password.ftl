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
        <div class="col-md-11" style="color: red" ><#if error??>${error}</#if></div>
    </div>
    <div class="row">

        <form action="/reset_password" method="post" id="resetForm">
            <div class="col-md-11">
                <div class="input-group">
                    <span class="input-group-addon" style="width: 140px">Old Password</span>
                    <input type="password" class="form-control" name="oldPassword"/>
                </div>
                <div class="input-group" style="margin-top: 10px">
                    <span class="input-group-addon" style="width: 140px">New Password</span>
                    <input type="password" class="form-control" name="newPassword"/>
                </div>
                <div class="input-group" style="margin-top: 10px">
                    <span class="input-group-addon" style="width: 140px">Repeat Password</span>
                    <input type="password" class="form-control" name="repeatPasword"/>
                </div>
            </div>
            <div class="col-md-11">
                <div class="col-md-4" align="right" style="margin-top: 10px;padding-right:25px;">
                    <input type="submit" class="btn btn-primary" value="Reset"/>
                </div>
            </div>
        </form>

    </div>
</div>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#resetForm").on('submit',function () {
       if($(this).find("input[name='oldPassword']").val()==""){
           IsmsErrorReporter.reportError("please enter old password");
           return false;
       }
       if($(this).find("input[name='newPassword']").val()==""){
           IsmsErrorReporter.reportError("please enter new password");
           return false;
       }
       if($(this).find("input[name='repeatPasword']").val()==""){
           IsmsErrorReporter.reportError("please repeat enter new password");
           return false;
       }
       if($(this).find("input[name='repeatPasword']").val()!=$(this).find("input[name='newPassword']").val()){
           IsmsErrorReporter.reportError("please enter the same password");
           return false;
       }
       return true;
    });
</script>
</body>
</html>