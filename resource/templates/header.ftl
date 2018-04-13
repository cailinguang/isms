<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="isms_header">
            <div><img src="images/logo.png" alt="SAIC" style="float:left"></div>
            <div><h1 style="padding-bottom: 20px;padding-top:15px;">&nbsp;&nbsp;ISMS信息安全管理平台<small></small></h1></div>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <#if auth??>
                <li id="nav_basic" <#if requestURI=="/basic_info">class="active"</#if> ><a href="/basic_info">基础信息</a></li>
                <#list menus as m>
                    <li id="nav_${m.menuId}" <#if requestURI==m.menuUrl>class="active"</#if> ><a href="${m.menuUrl}">${m.menuName}</a></li>
                </#list>
                <#if auth.principal.authorities?seq_contains("PER_Admin")>
                    <li <#if requestURI=="/backup">class="active"</#if> ><a href="/backup">数据备份</a></li>
                </#if>
                </#if>
            </ul>
            <ul class="nav navbar-nav navbar-right">
            <#if auth??>
                <#if auth.authenticated>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                           aria-expanded="false">${auth.principal.username}<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="/logout">退出</a></li>
                            <li><a href="/reset_password">修改密码</a></li>
                        </ul>
                    </li>
                <#else>
                    <li><a href="/login">登录</a></li>
                </#if>
            <#else>
                <li><a href="/login">登录</a></li>
            </#if>
            </ul>
        </div><!--/.nav-collapse -->
    </div><!--/.container-fluid -->
</nav>