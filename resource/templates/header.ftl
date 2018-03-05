<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="isms_header">
            <div><img src="images/SAIClogo.png" alt="SAIC" style="float:left"></div>
            <div><h1 style="padding: 40px">ISMS信息安全管理平台</h1></div>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li id="nav_basic_info"><a href="/basic_info">基础信息</a></li>
                <li id="nav_standard_library"><a href="/standards">标准库</a></li>
                <li id="nav_evaluation_library"><a href="/evaluations">安全评审</a></li>
                <li id="nav_evidence_library_tree"><a href="/evidences_tree">证据库</a></li>
                <li id="nav_data_library_tree"><a href="/data_tree">数据分类分级管理模块</a></li>
                <li id="nav_security_library_tree"><a href="/security_tree">信息安全对标模块</a></li>
                <li id="nav_evidence_library_tree"><a href="/evidences_tree">风险管理库</a></li>
                <li id="nav_network_security_tree"><a href="/network_security">网络安全法</a></li>
                <li id="nav_evidence_library_tree"><a href="/evidences_tree">审计日志</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
            <#if auth??>
                <#if auth.principal.authorities?seq_contains("ROLE_ADMIN")>
                    <li><a href="/admin">用户管理</a></li>
                </#if>
            </#if>
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