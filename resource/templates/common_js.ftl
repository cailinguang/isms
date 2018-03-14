<script src="/dist/jquery/jquery-3.0.0.min.js"></script>
<script src="/dist/jquery/jquery.form.js"></script>
<script src="/dist/bootstrap-3.3.5/js/bootstrap.min.js"></script>
<script src="/dist/handlebars/handlebars-v4.0.5.js"></script>
<script src="/dist/jquery-ui-1.12.0/jquery-ui.min.js"></script>
<script src="/dist/bootstrap-3.3.5/js/bootstrapSwitch.js"></script>
<script src="/dist/bootstrap-3.3.5/js/bootstrap-datetimepicker.min.js"></script>
<script src="/dist/bootstrap-3.3.5/js/bootstrap-select.min.js"></script>
<script src="/dist/jstree/jstree.min.js"></script>
<script src="/dist/chart/Chart.bundle.js"></script>
<script src="/js/common.js"></script>
<script src="/js/evidence.js"></script>
<script src="/js/ismsview.js"></script>
<script src="/js/tree.js"></script>
<script src="/js/dataTree.js"></script>
<script type="text/javascript">
    //计算分页的循环的num
    Handlebars.registerHelper({'calculateIndex':function(index,options){
        var index = index+1;
        if(options.data.root.pageNumber!=undefined){
            return options.data.root.pageNumber*options.data.root.itemPerPage+index;
        }
    }});
    //获取分页信息
    Handlebars.registerHelper({'page-info':function(page,options){
        var str = '当前第{{pageNumber}}页,每页{{itemPerPage}}条记录(共{{totalPage}}页,{{count}}条记录)';
        var pageInfo = {
            totalPage:parseInt(page.count/page.itemPerPage) + (page.count%page.itemPerPage==0?0:1),
            count:page.count,
            pageNumber:page.pageNumber+1,
            itemPerPage:10
        };
        var tip = Handlebars.compile(str)(pageInfo);
        return new Handlebars.SafeString(tip);
    }});
</script>
<#include "alert_dialog.ftl">