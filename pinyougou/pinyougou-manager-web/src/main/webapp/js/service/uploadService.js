app.service("uploadService",function ($http) {

    this.upload=function () {
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:"post",
            url:"../upload.do",
            data:formData,
            headers:{'Content-Type':undefined},
            //表单序列化
            transformRequest: angular.identity
        })
    }
})