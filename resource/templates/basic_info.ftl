<html>
<head>
    <meta charset="utf-8">
    <title>Evidence Libraries</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
	<#include "header.ftl">
    <div class="row">
		<#escape x as x?html>  
			<#noescape>
			  ${content}
			</#noescape>
		</#escape>
	</div>
</div>
<#include "common_js.ftl">
</body>
</html>