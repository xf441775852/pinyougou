/*品牌服务层*/
app.service("brandService",function ($http) {

    this.findAll = function () {
        return $http.get("../tbBrand/findAll.do");
    };

    this.findPage = function (currentPage, pageSize) {
        return $http.get('../tbBrand/findPage.do?currentPage=' + currentPage + '&pageSize=' + pageSize);
    };

    this.add = function (entity) {
        return $http.post('../tbBrand/add.do',entity);
    };
    this.update = function (entity) {
        return $http.post('../tbBrand/update.do', entity);
    };
    this.findOne = function (id) {
        return $http.get("../tbBrand/findOne.do?id=" + id);
    };
    this.del = function (ids) {
        return $http.get("../tbBrand/delete.do?ids=" +ids);
    };
    this.search = function (page,pageSize,searchEntity) {
        return $http.post("../tbBrand/search.do?page="+page+"&pageSize="+pageSize,searchEntity);
    }
    this.selectOptionList=function () {
        return $http.get("../tbBrand/selectOptionList.do");
    }
});
