 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id=$location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//富文本信息添加
				editor.html($scope.entity.tbGoodsDesc.introduction);
				//商品图片
				$scope.entity.tbGoodsDesc.itemImages=JSON.parse($scope.entity.tbGoodsDesc.itemImages);
				//扩展属性
				$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems)
				//规格
                $scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
				//SKU列表
				for(var i=0;i< $scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	//保存 
	$scope.save=function(){
        //获取富文本内容，将值赋给entity
        $scope.entity.tbGoodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
                if(response.success){
                    alert(response.message);
                    location.href="goods.html";
                }else{
                    alert(response.message);
                }
			}		
		);				
	}
	

    //申请状态
    $scope.status=["未申请","申请中","审核通过","已驳回"];
    //查询所有分类
	$scope.itemCatList=[];
	$scope.selectItemCatList=function () {
        itemCatService.findAll().success(
        	function (response) {
        		for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
				}

            }
		)
    }

    $scope.searchEntity={};//定义搜索对象
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//修改商品状态
	$scope.updateStatus=function (status) {
		goodsService.updateStatus($scope.ids,status).success(
			function (response) {
				if(response.success){
                    $scope.reloadList();//刷新列表
                    $scope.ids=[];
				}else{
					response.message;
				}
            }
		)
    }
    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        goodsService.dele( $scope.ids ).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }
});
