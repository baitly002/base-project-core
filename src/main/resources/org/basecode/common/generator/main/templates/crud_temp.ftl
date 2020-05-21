<script type="text/javascript">
	$(document).ready(function() {
		$("#${beanName}_MngTb").jqGrid(
			{
				url : "${beanname}_json",
				datatype : "json",
				colModel : [ 
				<#list listField as field>
					{
						name : '${field.name}',
						index : '${field.name}',
						label : '${field.name}',
						width : 100
					},
				</#list>
					{
						name : 'id',
						index : 'id',
						label : '操作', 
						width : 200,
						formatter : dolink_indexattr
					}
				],
				rowNum : 30,
				rowList : [ 20, 30, 50 ],
				pager : '#${beanName}_MngPaper',
				sortname : 'id',
				viewrecords : true,
				emptyrecords :"没有数据返回!",
				sortable : false,
				sortorder : "desc",
				caption : "${cnName}数据管理",
				autowidth: true,
				altRows: true,
				altclass: "ui-priority-secondary",
				multiselect: true,
				toolbar: [true,"top"],
				jsonReader : {  
		            root: "result.list", 
		            page: "result.pageNum", 
		            total: "result.pages", 
		            records: "result.total", 
		            repeatitems: false,
		            cell: "cell", 
		            id: "id" //对应数据库中的ID，这样就不会获取到行号了
		        }
			});
		$("#${beanName}_MngTb").jqGrid('navGrid', '#${beanName}_MngPaper', {
			edit : false,
			add : false,
			del : false,
			search : false,
			refresh : false
		});
		
		$("#t_${beanName}_MngTb").append("<input type='button' value='添加' onclick='javascript:add_${beanName}()' style='height: 30px'/><input type='button' value='刷新' onclick='javascript:reflesh_${beanName}()' style='height: 30px'/>");
		
	});
	
	/*
	操作用户管理列表
	*/
	function dolink_indexattr(cellvalue, options, row) {
		/*
		return "<a class=\"ui-state-default ui-corner-all\" onclick='javascript:show_${beanName}("+cellvalue+")'/> href=\"javascript:;\"><span class=\"ui-icon ui-icon-document\"></span>详情</a>"+
			"<a class=\"ui-state-default ui-corner-all\" onclick='javascript:edit_${beanName}("+cellvalue+")'/> href=\"javascript:;\"><span class=\"ui-icon ui-icon-disk\"></span>编辑</a>"+
			"<a class=\"ui-state-default ui-corner-all\" onclick='javascript:delete_${beanName}("+cellvalue+")'/> href=\"javascript:;\"><span class=\"ui-icon ui-icon-trash\"></span>删除</a>";
			*/
		return "<input type='button' value='详情' onclick='javascript:show_${beanName}("+cellvalue+")'/><input type='button' value='编辑' onclick='javascript:edit_${beanName}("+cellvalue+")'/><input type='button' value='删除' onclick='javascript:delete_${beanName}("+cellvalue+")'/>";
	};
	
	//刷新管理列表数据
	function reflesh_${beanName}(){
		$("#${beanName}_MngTb").trigger("reloadGrid");
	}

	//添加数据
	//打开添加页面
	function add_${beanName}(){
		var dialog = art.dialog({id: "A3690",title: "添加${cnName}信息"});
		dialog.content($("#add${beanName}").html());
	}
	//保存添加的数据
	function ${beanName}addform(){
		$("#${beanName}addForm").ajaxSubmit(function(data){
			if(data.status==1){
				var list = art.dialog.list;
				for (var i in list) {
				    list[i].close();
				};
				//number=1;
				art.dialog({id: "ajaxresult"}).content(data.message).time(3);
				reflesh_${beanName}();
			}else{
				art.dialog({id: "ajaxresult"}).content(data.message).time(3);
			}
		});
	    return false;
	}

	//删除数据
	function delete_${beanName}(id){
		var dialog = art.dialog({id: "D3690",title: "删除${cnName}信息"});
		var params = {"${beanName}.id":id};
		$.ajax({
			type : "POST",
			url : "${beanname}_delete",
			data : params,
			dataType : "json",
			success : function(data) {
				if(data.status==1){
					reflesh_${beanName}();
				}
				dialog.content(data.message);
			}
    	});
	}

	function delete_list_${beanName}(idstr){
		var selectedIds = $("#${beanName}_MngTb").jqGrid("getGridParam","selarrrow");
		if(selectedIds == 0){
			var dialog = art.dialog({id: "DL3690",title: "删除${cnName}数据"});
			dialog.content("请选中你要删除的数据再点击删除！");
		}else{
			var dialog = art.dialog({id: "D3690",title: "删除${cnName}信息"});
			var params = {"ids":selectedIds};
			$.ajax({
				type : "POST",
				url : "${beanname}_delete",
				data : params,
				dataType : "json",
				success : function(data) {
					if(data.status==1){
						reflesh_${beanName}();
					}
					dialog.content(data.message);
				}
	    	});
		}
	}
	
	//显示详细信息
	function show_${beanName}(id){
		var dialog = art.dialog({id: "S3690",title: "${cnName}详细信息"});
		var params = {"id":id};
		$.ajax({
			type : "POST",
			url : "user/show.json",
			data : params,
			dataType : "json",
			success : function(data) {
				if(data.status==1){
					dialog.content($("#show${beanName}").html());
					$("#${beanName}showForm:visible").autofill(data.result);
				}else{
					dialog.content(data.message);
				}
			}
		});
	}

	//编辑数据
	//打开编辑页面
	function edit_${beanName}(id){
		var dialog = art.dialog({id: "E3690",title: "编辑${cnName}信息"});
		var params = {"id":id};
		$.ajax({
			type : "POST",
			url : "user/show.json",
			data : params,
			dataType : "json",
			success : function(data) {
				if(data.status==1){
					dialog.content($("#edit${beanName}").html());
					$("#${beanName}editForm:visible").autofill(data.result);
				}else{
					dialog.content(data.message);
				}
			}
		});
	}
	//保存编辑数据
	function ${beanName}editform(){
		$("#${beanName}editForm").ajaxSubmit(function(data){
			if(data.status==1){
				var list = art.dialog.list;
				for (var i in list) {
				    list[i].close();
				};
				//number=1;
				art.dialog({id: "ajaxresult"}).content(data.message).time(3);
				reflesh_${beanName}();
			}else{
				art.dialog({id: "ajaxresult"}).content(data.message).time(3);
			}
		});
	    return false;
	}
	
</script>

<div layouth="0">
	<div class="pageHeader"></div>
	<br/>
	<div><table id="${beanName}_MngTb"></table></div>
	<div id="${beanName}_MngPaper"></div>
	<!--添加-->
	<div id="add${beanName}" style="display: none;">
		<form method="post" id="${beanName}addForm" action="user/add.json" onsubmit="return ${beanName}addform();">
			<table class="tools_table" style="width: 700px;">
				<#list listField as field>
				<tr>
					<td class="trlabel">${field.name}:</td>
					<td><input type="text" name="${field.name}" maxlength="200"/></td>
				</tr>
				</#list>
				<tr>
					<td colspan="2" align="center">
						<input type="submit" value="保存" id="${beanName}_submit" />
					    <input type="reset" value="重置" id="${beanName}_reset"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<!--修改-->
	<div id="edit${beanName}" style="display: none;">
		<form method="post" id="${beanName}editForm" action="user/update.json" onsubmit="return ${beanName}editform();">
			<table class="tools_table" style="width: 700px;">
				<#list listField as field>
				<tr>
					<td class="trlabel">${field.name}:</td>
					<td><input type="text" name="${field.name}" maxlength="200"/></td>
				</tr>
				</#list>
				<tr>
					<td colspan="2" align="center">
						<input type="submit" value="保存" id="${beanName}_submit" />
					    <input type="reset" value="重置" id="${beanName}_reset"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<!--查看-->
	<div id="show${beanName}" style="display: none;">
		<form method="post" id="${beanName}showForm" action="user/show.json">
			<table class="tools_table" style="width: 700px;">
				<#list listField as field>
				<tr>
					<td class="trlabel">${field.name}:</td>
					<td><input type="text" name="${field.name}" maxlength="200"/></td>
				</tr>
				</#list>
			</table>
		</form>
	</div>
</div>
