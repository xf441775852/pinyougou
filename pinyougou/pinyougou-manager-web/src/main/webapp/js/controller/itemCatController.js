 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}


	//批量删除 
	$scope.dele=function(ids){
		//获取选中的复选框			
		itemCatService.dele(ids).success(
			function(response){
				if(response.success){
                    $scope.findParentId($scope.entity.parentId);//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//分类查询
	$scope.findParentId=function (parentId) {
		itemCatService.findParentId(parentId).success(
			function (response) {
				$scope.list=response;
            }
		)
    }
    //面包屑
	$scope.grade=1;
	$scope.setGrade=function (value) {
		$scope.grade=value;
    }
    $scope.selectList=function (entity){
		if($scope.grade==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grade==2){
            $scope.entity_1=entity;
            $scope.entity_2=null;
		}
		if($scope.grade==3){
			$scope.entity_2=entity;
		}

        $scope.findParentId(entity.id);
    }
    $scope.entity={};
    $scope.setParent=function () {
		if($scope.grade==1){
            $scope.entity.parentId=0;
		}
		if($scope.grade==2){
            $scope.entity.parentId=$scope.entity_1.id;
		}
        if($scope.grade==3){
            $scope.entity.parentId=$scope.entity_2.id;
        }
    }
    //保存
    $scope.add=function(entity){
    	var object = null;
    	if(entity.id!=null){
    		object = itemCatService.update(entity);
		}else{
            object = itemCatService.add(entity);
		}
        object.success(
            function (response) {
                if(response.success){
                    $scope.findParentId($scope.entity.parentId);
                } else{
                    alert(response.false);
                }
            }
        )
    }
});	
