app.controller('baseController',function ($scope) {
    //分页控制配件
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };

//重新加载列表
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage,$scope.searchEntity);
    };

    $scope.ids = [];//定义一个勾选的id数组

//更新复选框勾选
    $scope.updateSelection = function ($event, id) {
        if($event.target.checked){
            $scope.ids.push(id);//复选框被选中
        } else{
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index,1);//删除
        }

    }
    //字符串转化为对象
    $scope.stringToJson=function (jsonString,key) {
        var json = JSON.parse(jsonString);
        value="";
        //遍历json
        for(var i=0;i<json.length-1;i++){
            if(i>0){
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }
});
