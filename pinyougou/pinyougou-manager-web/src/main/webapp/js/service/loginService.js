app.service("loginService",function ($http) {
    this.login=function () {
        return $http.get("../login/getName.do");
    };
});