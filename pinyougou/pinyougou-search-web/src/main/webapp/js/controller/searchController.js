//控制层
app.controller("searchController",function ($scope,$location, searchService) {
    //定义搜索条件
    $scope.searchMap={'category':'','brand':'','spec':{},'price':'','pageNum':1,'pageSize':5,'sort':'','sortField':''};
    $scope.itemSearch=function () {
        //将当前页转化为int类型
        $scope.searchMap.pageNum = parseInt($scope.searchMap.pageNum);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                buildPageLabel();//调用分页
            }
        )
    }
    //增加搜索条件
    $scope.addSearchMap=function (key, value) {
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]=value;
        }else{//传的是规格
            $scope.searchMap.spec[key]=value;
        }
        $scope.itemSearch();
    }
    //删除搜索条件
    $scope.removeSearchMap=function(key){
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]="";
        }else{//传的是规格
            delete $scope.searchMap.spec[key];
        }
        $scope.itemSearch();
    }
    //构建分页标签
    buildPageLabel=function () {
        $scope.pageLabel=[];//新增分页栏属性
        $scope.perPageLabel = 5;//展示的分页栏页数
        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;//总页数
        if($scope.resultMap.totalPages>$scope.perPageLabel){//总页数大于展示的分页数
            //当前页小于3时，firstPage=1
            if($scope.searchMap.pageNum<=(1+$scope.perPageLabel)/2){
                lastPage=5;
            }else if($scope.searchMap.pageNum>=($scope.resultMap.totalPages-($scope.perPageLabel-1)/2)){
                firstPage=$scope.resultMap.totalPages-($scope.perPageLabel-1);
            } else{//正常情况
                firstPage=$scope.searchMap.pageNum-($scope.perPageLabel-1)/2;
                lastPage=$scope.searchMap.pageNum+($scope.perPageLabel-1)/2;
            }
        }
        //将页数遍历放进分页栏集合
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //根据页码查询
    $scope.queryByPage=function (pageNum) {
        if(pageNum<1 ||pageNum>$scope.resultMap.totalPages){//第一页或最后一页
            return;
        }

        $scope.searchMap.pageNum = pageNum;
        $scope.itemSearch();
    }
    //排序查询
    $scope.sortSearch=function (sortValue, sortField) {
        $scope.searchMap.sort=sortValue;
        $scope.searchMap.sortField=sortField;

        $scope.itemSearch();
    }
    //隐藏品牌列表
    $scope.keywordsIsBrand=function () {
        //循环品牌列表
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //接收主页跳转的参数
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.itemSearch();
    }


})