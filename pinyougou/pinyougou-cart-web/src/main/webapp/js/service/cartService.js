//服务
app.service("cartService",function ($http) {
    //查看购物车
    this.getCartList=function () {
        return $http.get("cart/getCartList.do");
    }

    //添加||删除购物车
    this.addGoodsToCart=function (itemId, num) {
        return $http.get("cart/addGoodsToCart.do?itemId="+itemId+"&num="+num);
    }

    //获取所有地址
    this.getAllAddress=function () {
        return $http.get("address/getAll.do");
    }
    //添加地址
    this.addAddress=function (order) {
        return $http.post("address/add.do",order);
    }
    //添加订单
    this.addOrder=function (order) {
        return $http.post("order/add.do",order);
    }
})