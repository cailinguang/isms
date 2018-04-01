<html>
<head>
    <meta charset="utf-8">
    <title>Login</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="row">
        <div class="col-md-6" align="center" style="color: red">${loginMessage}</div>
    </div>
    <div class="row" style="margin-top: 10px">
        <div class="col-md-3">
            <form action="/login" method="post">
                <div class="input-group">
                    <span class="input-group-addon" style="width: 100px">User Name</span>
                    <input type="text" class="form-control" name="username"/>
                </div>
                <div class="input-group" style="margin-top: 10px">
                    <span class="input-group-addon" style="width: 100px">Password</span>
                    <input type="password" class="form-control" name="password"/>
                </div>
                <div align="right" style="margin-top: 10px">
                    <input type="submit" class="btn btn-primary" value="Sign In"/></div>
            </form>
        </div>
    </div>
</div>
<#--测试版本弹出框-->
<script type="text/javascript">
    function uatVersion() {
       alert('current version:UAT');
       return true;
    }
</script>
</body>
</html>