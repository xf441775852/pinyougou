//控制模块
app.controller("cartController",function ($scope   ,cartService) {

    //查看购物车
    $scope.getCartList=function () {
        cartService.getCartList().success(
            function (response) {
                $scope.cartList=response;
                getTotal($scope.cartList);
            }
        )
    }


    //遍历cartList
    getTotal=function (cartList) {
        $scope.totalNum=0;//总数量
        $scope.totalMoney=0;//总金额
        for(var i=0;i<cartList.length;i++){//遍历购物车列表得到每一个购物车
            var item = cartList[i].orderItemList;
            for(var j=0;j<item.length;j++){//遍历购物车获取数量和总金额
                $scope.totalNum+=item[j].num;//求总数量
                $scope.totalMoney+=item[j].totalFee;
            }
        }
    }

    //添加||删除购物车
    $scope.addGoodsToCart=function (itemId, num) {
        cartService.addGoodsToCart(itemId, num).success(
            function (response) {
                if(response.success){//添加成功
                    $scope.getCartList();//刷新页面
                }else {
                    alert(response.message);
                }
            }
        )
    }
    //添加地址
    $scope.addAddress=function () {
        cartService.addAddress($scope.order).success(
            function (response) {
                if(response.success){
                    alert(response.message);
                    $scope.getAllAddress();
                }else{
                    alert(response.message);
                }
            }
        )
    }
    //获取所有地址
    $scope.getAllAddress=function () {
        cartService.getAllAddress().success(
            function (response) {
                $scope.addressList=response;
                for (var i=0;i<$scope.addressList.length;i++){
                    if ($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                    }
                }
            }
        )
    }
    //选择地址
    $scope.selectAdrress=function(address){
        $scope.address=address;
    }
    //判断选择的哪一个地址
    $scope.isSelectedAddress=function (address) {
        return $scope.address==address;
    }

    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPaymentType=function (type) {
        $scope.order.paymentType=type;
    }

    //添加订单
    $scope.addOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.addOrder($scope.order).success(
            function (response) {
                if(response.success){
                    //提交成功，判断是哪一种支付方式
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message);
                }
            }
        )
    }
})