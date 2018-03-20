<html>
<head>
    <meta charset="utf-8">
    <title>Evidence Libraries</title>
    <#include "../common_css.ftl">
    <style type="text/css">
        .errorpPage-box h1{font-size:44px; color:rgba(145, 149, 156, 1); padding:20px 0px 10px;}
        .errorpPage-box h2{color:rgba(61, 164, 76, 1); font-size:16px; padding:10px 0px 40px;}
        .errorpPage-box p{color:#666;}
        .errorpPage-box{padding:20px 20px 40px;border-style:dashed;border-color:#e4e4e4;line-height:30px;margin-left:auto; margin-right:auto;}
        .errorpPage-operate .operateBtn{width:100px; height:28px; margin-left:0px; margin-top:10px; background:#009CFF; border-bottom:4px solid #0188DE; text-align:center;display:inline-block;font-size:14px; color:#fff; }
        .errorpPage-operate .operateBtn:hover{ background:#5BBFFF;}
    </style>
</head>
<body>
<div class="container">
	<#include "../header.ftl">
    <div class="errorpPage-box">
        <h1>400</h1>
        <h2>bad request</h2>
        <div class="errorpPage-operate">
            <a href="javascript:window.location.reload()" class="operateBtn" title="刷新试试">刷新试试</a>
            <a href="/" class="operateBtn" title="返回首页">返回首页</a>
        </div>
    </div>
</div>
<#include "../common_js.ftl">
</body>
</html>