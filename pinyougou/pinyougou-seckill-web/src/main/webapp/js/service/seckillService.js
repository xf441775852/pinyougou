app.service("seckillService",function ($http) {
    //查询参与秒杀的商品
    this.findList=function () {
        return $http.get("/seckillGoods/findList.do");
    }

    //根据id获取实体
    this.findOne=function (id) {
        return $http.get("/seckillGoods/findOneFromRedis.do?id="+id);
    }

    //提交订单
    this.submitOrder=function (id) {
        return $http.get("/seckillOrder/submitOrder.do?id="+id);
    }
    //生成二维码
    this.createNative=function () {
        return $http.get("/seckillOrder/createNative.do");
    }
})