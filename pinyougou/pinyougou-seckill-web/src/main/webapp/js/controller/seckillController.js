app.controller("seckillController",function ($scope,$location,$interval,seckillService) {
    //查询所有参与秒杀的商品
    $scope.findList=function () {
        seckillService.findList().success(
            function (response) {
                $scope.seckillGoodsList=response;
            }
        )
    }
    //根据id获取实体
    $scope.findOne=function () {
        var id = $location.search()['id'];
        seckillService.findOne(id).success(
            function (response) {
                $scope.entity=response;

                //获取时间总秒数
                allSecond = Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000);
                time = $interval(function () {
                    if(allSecond>0){
                        allSecond = allSecond -1;
                        //将秒数转化为时间
                        $scope.timeStr = $scope.convertToDate(allSecond);
                    }else{
                        $interval.cancel(time);
                        alert("秒杀服务已结束");
                    }
                },1000)
            }
        )
    }

    $scope.convertToDate=function (allSecond) {
        var day = Math.floor(allSecond / (60*60*24));//天数
        var hour = Math.floor((allSecond % (60*60*24)) / (60*60));//小时
        var minute = Math.floor((allSecond % (60*60)) / 60);//分钟数
        var second = Math.floor(allSecond % 60);//秒数
        var timeString="";
        if(day>0){
            timeString=day+"天 ";
        }
        return timeString+hour+":"+minute+":"+second;
    }

    //提交订单
    $scope.submitOrder=function (id) {
        seckillService.submitOrder(id).success(
            function (response) {
                if(response.success){
                    alert("下单成功，请在1分钟内完成支付");
                    location.href="pay.html";
                } else{
                    alert(response.message);
                }
            }
        )
    }


})