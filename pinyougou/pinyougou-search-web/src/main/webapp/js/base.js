//定义一个模块
var app=angular.module('pinyougou',[]);
//html过滤
app.filter("trustHtml",['$sce',function ($sce) {
        /*data是传过来的需要过滤的*/
        return function (data) {
            return $sce.trustAsHtml(data);
        }
}])