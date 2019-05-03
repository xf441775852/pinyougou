 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,uploadService,itemCatService,typeTemplateService){
	
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
	//判断规格的属性是否存在
	$scope.checkAttributeValue=function (keyName,value) {
		//先判断attributeName的value是否存在
        var items=$scope.entity.tbGoodsDesc.specificationItems;
        var object = $scope.isExist(items,'attributeName',keyName);
        if(object!=null){
        	//属性存在，查询keyValue是否存在
			if (object.attributeValue.indexOf(value)>=0){
				return true;
			}else{
				return false;
			}
		}else{
        	return false;
		}

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
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
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

	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //上传文件
	$scope.upload=function () {
		uploadService.uploadFile().success(
			function (response) {
				if(response.success){
					$scope.imageEntity.url=response.message;
				}else{
					alert(response.message);
				}
            }
		)
    }
    //保存图片到列表
    $scope.entity={tbGoods:{},tbGoodsDesc:{specificationItems:[],itemImages:[]}};
	$scope.add_image_entity=function () {
		$scope.entity.tbGoodsDesc.itemImages.push($scope.imageEntity);
    }
    //从列表中删除图片
	$scope.dele_image_entity=function (imageEntity) {
        var index = $scope.entity.tbGoodsDesc.itemImages.indexOf(imageEntity);
        $scope.entity.tbGoodsDesc.itemImages.splice(index);
    }
    //初始化查询一级分类
	$scope.selectItemCat1List=function (parentId) {
        itemCatService.findParentId(parentId).success(
        	function (response) {
                $scope.selectItemCat1List=response;
            }
		)
    }
    //当一级分类值改变时，查询二级分类
	$scope.$watch("entity.tbGoods.category1Id",function (newValue, oldValue) {
        itemCatService.findParentId(newValue).success(
            function (response) {
                $scope.selectItemCat2List=response;
            }
        )
    })
	//二级分类值改变时，查询三级分类
	$scope.$watch("entity.tbGoods.category2Id",function (newValue, oldValue) {
        itemCatService.findParentId(newValue).success(
            function (response) {
                $scope.selectItemCat3List=response;
            }
        )
    })
	//三级分类值改变时，查询模板id
	$scope.$watch("entity.tbGoods.category3Id",function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.tbGoods.typeTemplateId=response.typeId;
                //根据typeId查询其品牌
                typeTemplateService.findOne(response.typeId).success(
                	function (response) {
                        $scope.tbTypeTemplate=response;
                        //品牌列表
                        $scope.tbTypeTemplate.brandIds=JSON.parse($scope.tbTypeTemplate.brandIds);
                        //扩展属性
						if($location.search()['id']==null){
                            $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.tbTypeTemplate.customAttributeItems);
						}

                    }
				)
				//查询规格
				typeTemplateService.findSpecificationOption(response.typeId).success(
					function (response) {
						$scope.entity.specificationOption=response;
                    }
				)
            }
        )

    })
	//保存选中规格选项

	$scope.updateSpecificationItems=function ($event,keyName,value) {
		var object = $scope.isExist($scope.entity.tbGoodsDesc.specificationItems,"attributeName",keyName);
		if(object!=null){
			//specification_items中attributeName存在
            if($event.target.checked){
                //复选框被勾选
				object.attributeValue.push(value);
            }else{
            	//移除取消的值
            	object.attributeValue.splice(object.attributeValue.indexOf(value),1);
            	if(object.attributeValue.length==0){
                    $scope.entity.tbGoodsDesc.specificationItems.splice(
                        $scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			//specification_items中attributeName不存在
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":keyName,"attributeValue":[value]});
		}

    }



    //创建SKU列表
	$scope.createItemList=function () {
		//定义空集合
		$scope.entity.itemList= [{spec:{},price:"",
						defaultItemId:"",num:"",status:"0",isDefault:""}];
        // [{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){
            $scope.entity.itemList = addcolumn($scope.entity.itemList,
					items[i].attributeName,items[i].attributeValue);
		}
    }
    addcolumn=function (list, columnName, conlumnValues) {
		//新的集合
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldRow = list[i];
			for(var j=0;j<conlumnValues.length;j++){
				//深克隆
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName]=conlumnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
    }
});
