 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	

	//注册
	$scope.reg=function () {
		if($scope.entity.password!=null&&$scope.entity.password.length>0){
            if(!angular.equals($scope.entity.password,$scope.password)){
            	alert("输入的密码不一致");
            	return;
			}
		}else{
			alert("请输入密码");
		}

        userService.add($scope.entity,$scope.code).success(
            function (response) {
				alert(response.message);
            }
        )
    }
	//发送验证码
	$scope.send=function () {
		userService.send($scope.entity.phone).success(
			function (response) {
				alert(response.message);
            }
		)
    }
    //查找订单
	$scope.selectOrder=function () {
		userService.selectOrder().success(
			function (response) {
				$scope.orderAndOrderItems=response;
            }
		)
    }
    //查看订单详情
	$scope.selectOrderDetail=function (orderId) {
		location.href="home-orderDetail.html#?orderId="+orderId;
    }
});	
