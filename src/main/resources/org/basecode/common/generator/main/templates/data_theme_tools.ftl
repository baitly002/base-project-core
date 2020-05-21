<#list toolsModuleType as type>
	function formatter${type.typeid}(cellvalue, options, row) {
		var rs = "";
		switch(cellvalue)
		{
		<#list type.toolsModule as module>
		case ${module.id}:
		  rs = "${module.name}";
		  break;
		</#list>
		default:
		  rs = "";
		}
		return rs;
	}
</#list>
function edit_data_theme(rowid, iRow, iCol, e, gridid, themeid){
	//var list=$("#"+gridid).jqGrid('getRowData',rowid);
	var irowIndex = iRow-1;
	var list=$("#"+gridid).jqGrid('getRowData')[irowIndex];
	//show_enterprise_info(rowid,'6444','测试');
	//$("#"+gridid).jqGrid('editGridRow',rowid,{height:280,reloadAfterSubmit:false});
	var parms = {id:list.id,themeid:themeid,year:list.year,season:list.season,month:list.month};
	$.ajax({
		type : "POST",
		url : "datatheme_index_edit",
		dataType : "html",
		data : parms,
		success : function(data) {	
			var dialog = art.dialog({id: "edit001",title: '编辑信息'});
			dialog.content(data);
		}
    });
	//art.dialog({title: '编辑信息', id:'edit001', content: "url:datatheme_index_edit?themeid="+themeid+"&parm_index="+list});
	
}
function show_enterprise_with_map_formatter(cellvalue, options, row){
	return "<a href=\"javascript:show_enterprise_info("+row.id+", "+"'"+"dlg_show_ep_info"+"', "+"'"+cellvalue+"'"+")\">"+cellvalue+"</a>";
}