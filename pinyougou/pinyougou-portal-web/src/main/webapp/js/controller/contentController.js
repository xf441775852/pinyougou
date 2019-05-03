//控制层
app.controller("contentController",function ($scope, contentService) {

    //所有广告列表
    $scope.contentList=[];
    //通过分类id查询广告列表
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    }
    //搜索并跳转搜索页面
    $scope.search=function () {
        location.href="http://localhost:9007/search.html#?keywords="+$scope.keywords;
    }
})