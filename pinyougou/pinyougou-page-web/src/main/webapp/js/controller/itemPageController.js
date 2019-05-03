app.controller('itemPageController',function($scope,$http){
	
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	$scope.specificationItems={};//记录用户选择的规格
    $scope.selectSpecificationItems=function (name, value) {
        $scope.specificationItems[name]=value;//选择的规格

        for(var i=0;i<itemList.length;i++){
            if(matchObject($scope.specificationItems,itemList[i].spec)){
                $scope.sku=itemList[i];
				return;
            }
        }
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
    }
    //判断某规格选项是否被选中
    $scope.isSelected=function (name, value) {
        return $scope.specificationItems[name]==value;
    }
	 //加载默认SKU
    $scope.loadSKU=function () {
        $scope.sku=itemList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }
	//匹配两个对象是否相等
    matchObject=function (map1, map2) {
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    }
	
	//添加商品到购物车
	$scope.addToCart=function(itemId,num){
        return $http.get("http://localhost:9013/cart/addGoodsToCart.do?itemId="+itemId+
                    "&num="+num,{'withCredentials':true}).success(
                        function (response) {
                            if(response.success){
                                location.href="http://localhost:9013/cart.html";
                            }else{
                                alert(response.message);
                            }
                        }
        );
	}
})