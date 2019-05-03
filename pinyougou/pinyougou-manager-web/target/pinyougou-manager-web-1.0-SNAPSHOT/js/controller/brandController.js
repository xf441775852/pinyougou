/*定义一个控制器*/
app.controller("brandController", function ($scope, $controller,brandService) {
    $controller('baseController',{$scope:$scope});
    //查询所有品牌信息
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    };


    //分页查询所有品牌
    $scope.findPage = function (currentPage, pageSize) {
        brandService.findPage(currentPage, pageSize).success(
            function (response) {
                $scope.list = response.rows;//页面数据
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    };

    //保存品牌
    $scope.add = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = this.add();//若id为空，则为增加方法（输入框没设id值）
        } else {
            object = this.update();
        }
        object.success(
            function (response) {
                if (response.success) {
                    //添加成功,刷新加载列表
                    $scope.reloadList();
                } else {
                    //添加失败
                    alert(response.message);
                }
            }
        )
    };

    //根据id查询TbBrand
    $scope.findOne = function (id) {
        brandService.findOne().success(
            function (response) {
                $scope.entity = response;
            }
        )
    };


    //删除操作
    $scope.del = function () {
        brandService.del().success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        );
    }
    $scope.searchEntity={};
    //条件查询
    $scope.search = function (page,pageSize) {
        brandService.search(page,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )
    }
});