app.controller("loginController",function ($scope, loginService) {
    //登录
    $scope.getLoginName=function () {
        loginService.login().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }
});