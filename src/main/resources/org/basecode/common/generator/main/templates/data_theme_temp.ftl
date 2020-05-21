<style>
#themegrid${datatheme.id}_MngTb .ui-state-highlight{
    color: #363636;
}
#themegrid${datatheme.id}_MngTb .ui-priority-secondary{
    opacity: 1;
}
</style>

<script type="text/javascript">
	$(document).ready(function() {
		var defval = $("#def${datatheme.id}").attr("data-value");
		var dv = defval.split("_");
		var dy = dv[0];
		var ds = dv[1];
		var dm = dv[2];
		if(dy!='0'){
			$(".theme_parm_ul > li[data-type='year']").siblings().children("div").attr("class","parm_div_click");
			$(".theme_parm_ul > li[data-value='"+dy+"']").siblings().children("div").attr("class","parm_div");
		}
		if(ds!='0'){
			$(".theme_parm_ul > li[data-type='season']").siblings().children("div").attr("class","parm_div_click");
			$(".theme_parm_ul > li[data-value='"+ds+"']").siblings().children("div").attr("class","parm_div");
		}
		if(dm!='0'){
			$(".theme_parm_ul > li[data-type='month']").siblings().children("div").attr("class","parm_div_click");
			$(".theme_parm_ul > li[data-value='"+dm+"']").siblings().children("div").attr("class","parm_div");
		}
		$(".theme_parm_ul > li").click(function(){
			var datatype = $(this).attr("data-type");
			var dataindex = $(this).attr("data-index");
			var datavalue = $(this).attr("data-value");
			var dataif = $(this).attr("data-if");
			add_parm_${datatheme.id}(datatype, dataindex, datavalue, dataif);
			$(this).siblings().children("div").removeClass("parm_div_click");
			$(this).siblings().children("div").addClass("parm_div");
			$(this).children("div").addClass("parm_div_click");
			select_data${datatheme.id}();
		});
		
		//默认查询ui-state-highlight
		//var post_data=$("#datatheme${datatheme.id}form").serialize();
		$("#themegrid${datatheme.id}_MngTb").jqGrid(
			{
				url : "data_theme_query_json",
				datatype : "json",
				colModel : [ 
				<#list listField as field>
					{
						name : '${field.attrenname}',
						index : '${field.attrenname}',
						label : '${field.label!field.attrname}',
						<#if field.align??>
							align: ${field.align},
						</#if>
						<#if field.classes??>
							classes: ${field.classes},
						</#if>
						<#if field.datefmt??>
							datefmt: ${field.datefmt},
						</#if>
						<#if field.defval??>
							defval: ${field.defval},
						</#if>
						<#if field.editable??>
							editable: ${field.editable},
						</#if>
						<#if field.editoptions??>
							editoptions: ${field.editoptions},
						</#if>
						<#if field.editrules??>
							editrules: ${field.editrules},
						</#if>
						<#if field.edittype??>
							edittype: ${field.edittype},
						</#if>
						<#if field.fixed??>
							fixed: ${field.fixed},
						</#if>
						<#if field.formoptions??>
							formoptions: ${field.formoptions},
						</#if>
						<#if field.formatoptions??>
							formatoptions: ${field.formatoptions},
						</#if>
						<#if field.attrenname=="ename">
							formatter: show_enterprise_with_map_formatter,
						<#else>
							<#if field.attroptionvalue?? && field.attroptionvalue != 0>
								<#if field.formatter??>
									formatter: ${field.formatter},
								<#else>
									formatter: formatter${field.attroptionvalue},
								</#if>
							</#if>
						</#if>
						<#if field.hidedlg??>
							hidedlg: ${field.hidedlg},
						</#if>
						<#if field.hidden??>
							hidden: ${field.hidden},
						</#if>
						<#if field.jsonmap??>
							jsonmap: ${field.jsonmap},
						</#if>
						<#if field.key??>
							key: ${field.key},
						</#if>
						<#if field.resizable??>
							resizable: ${field.resizable},
						</#if>
						<#if field.search??>
							search: ${field.search},
						</#if>
						<#if field.searchoptions??>
							searchoptions: ${field.searchoptions},
						</#if>
						<#if field.sortable??>
							sortable: ${field.sortable},
						</#if>
						<#if field.sorttype??>
							sorttype: ${field.sorttype},
						</#if>
						<#if field.stype??>
							stype: ${field.stype},
						</#if>
						<#if field.surl??>
							surl: ${field.surl},
						</#if>
						<#if field.xmlmap??>
							xmlmap: ${field.xmlmap},
						</#if>
						<#if field.unformat??>
							unformat: ${field.unformat},
						</#if>
						<#if field.title??>
							title: ${field.title},
						</#if>
						<#if field.viewable??>
							viewable: ${field.viewable},
						</#if>
						width : ${field.width!'50'}
					}
					<#if field_has_next>
						,
					</#if>
				</#list>
				],
				rowNum : 50,
				rowList : [ 30, 50, 100 ],
				serializeGridData : function (postData){
					var post_data = $("#datatheme${datatheme.id}form").serializeArray();
					$.each(postData, function (name,value){
						var arr = {name:name,value:value};
						post_data.push(arr);
					})
					return post_data;
				},
				pager : '#themegrid${datatheme.id}_MngPaper',
				viewrecords : true,
				emptyrecords :"没有数据返回!",
				recordpos : "left",
				sortable : true,
				sortname : "eid",
				sortorder : "asc",
				caption : "${datatheme.name}数据管理",
				autowidth: true,
				altRows: true,
				altclass: "ui-priority-secondary",
				multiselect: false,
				toolbar: [true,"top"],
				jsonReader : {  
		            root: "rows", 
		            page: "page", 
		            total: "total", 
		            records: "records", 
		            repeatitems: false,
		            cell: "cell", 
		            id: "id" //对应数据库中的ID，这样就不会获取到行号了
		        },
		        ondblClickRow: function(rowid, iRow, iCol, e){
		        	var gridid = "themegrid${datatheme.id}_MngTb";
		        	var themeid = ${datatheme.id};
					edit_data_theme(rowid, iRow, iCol, e, gridid, themeid);
				},
				gridComplete: function(){
					$("#themegrid${datatheme.id}_MngTb .ui-row-ltr").click(function(){
						$("#themegrid${datatheme.id}_MngTb .ui-row-ltr").removeClass("ui-state-highlight-zui ui-state-highlight");
						$(this).addClass("ui-state-highlight-zui");
						setTimeout(function(){
						//	$("#themegrid${datatheme.id}_MngTb .ui-row-ltr").removeClass("ui-state-highlight");
							$("#themegrid${datatheme.id}_MngTb .ui-state-highlight").removeClass("ui-state-highlight");
						},50);
					})
				}
				});			
        innitMapdata();
		$("#themegrid${datatheme.id}_MngTb").jqGrid('navGrid', '#themegrid${datatheme.id}_MngPaper', {
			edit : false,
			add : false,
			del : false,
			search : false,
			refresh : false,
			position : 'right'
		});
		$("#t_themegrid${datatheme.id}_MngTb").append(
			"<a class='zui_btn' href=\"javascript:show_query_data_map_fenbu(1,'data${datatheme.id}','当前数据地图分布')\"><span>查看当前数据地图分布</span></a>"+
			"<a class='zui_btn' href=\"javascript:show_query_data_map_fenbu(2,'data${datatheme.id}','所有数据地图分布')\"><span>查看所有数据地图分布</span></a>"+
			"<a class='zui_btn' href='javascript:exportdatatheme${datatheme.id}()'><span>导出EXCEL</span></a>"+
			"<a class='zui_btn' href='javascript:show_maptable(${datatheme.id})'><span>查看图表</span></a>"+
			"<a class='zui_btn' href='javascript:add_themegrid${datatheme.id}();'><span>添加</span></a>"+
			"<a class='zui_btn' href='javascript:reflesh_themegrid${datatheme.id}();'><span>刷新</span></a>"
		);
	});
	function add_parm_${datatheme.id}(datatype, dataindex, datavalue, dataif){
		$("input[data-type='"+datatype+"']").remove();
		$("#submit_input${datatheme.id}").append("<input type=\"hidden\" data-type=\""+datatype+"\" data-value=\""+datavalue+"\" name=\"parm_index['"+dataindex+"']\" value=\""+dataif+":"+datavalue+"\"/>");
		//去掉没有值的，无意义
		$("input[data-value='novalue']").remove();
//		$("#submit_input${datatheme.id}").append("<input type=\"hidden\" data-type=\""+datatype+"\" name=\"parm_if['"+dataindex+"']\" value=\""+dataif+"\"/>");
//		$("#submit_input${datatheme.id}").append("<input type=\"hidden\" data-type=\""+datatype+"\" name=\"parm_index."+dataindex+"\" value=\""+datavalue+"\"/>");
//		$("#submit_input${datatheme.id}").append("<input type=\"hidden\" data-type=\""+datatype+"\" name=\"parm_if."+dataindex+"\" value=\""+dataif+"\"/>");
	}
	//jqgrid
	function select_data${datatheme.id}(){
		innitMapdata();
		//var post_data=$("#datatheme${datatheme.id}form").serialize();
		//postData
		$("#themegrid${datatheme.id}_MngTb").jqGrid('setGridParam',{
			url:"data_theme_query_json",
			serializeGridData : function (postData){
					var post_data = $("#datatheme${datatheme.id}form").serializeArray();
					$.each(postData, function (name,value){
						var arr = {name:name,value:value};
						post_data.push(arr);
					})
					return post_data;
				},
			ajaxGridOptions:{type: "POST"},
			page:1, 
			sortable : true,
		}).trigger("reloadGrid");  
	}
	function datatheme${datatheme.id}form(){
		select_data${datatheme.id}();
	    return false;
	}
	//刷新管理列表数据
	function reflesh_themegrid${datatheme.id}(){
		$("#themegrid${datatheme.id}_MngTb").trigger("reloadGrid");
	}

	//添加数据
	function add_themegrid${datatheme.id}(){
		var parms = {themeid:${datatheme.id}};
		$.ajax({
			type : "POST",
			url : "datatheme_index_edit",
			dataType : "html",
			data : parms,
			success : function(data) {	
				var dialog = art.dialog({id: "edit001",title: '添加信息'});
				dialog.content(data);
			}
	    });
	}
	function exportdatatheme${datatheme.id}(){
		var records = $("#themegrid${datatheme.id}_MngTb").getGridParam("records");
		if(records>0){
			var post_data=$("#datatheme${datatheme.id}form").serialize();
			var data_c = "";
			if(records>2000){
				data_c = "导出数据较多，系统正在处理，请耐心等候……";
			}else{
				data_c = "系统正在处理，请耐心等候……";
			}
			art.dialog({
				lock: true,
				title: "系统信息提示:导出"+records+"条数据",
			    icon: 'loading',
			    content: data_c
			}).time(records/2000);
			if (typeof (download_file.iframe) == "undefined") {
				var iframe = document.createElement("iframe");
				download_file.iframe = iframe;
				document.body.appendChild(download_file.iframe);
			}
			var url = "export_data_theme_${datatheme.id}?records="+records+"&"+post_data;
			$(download_file.iframe.contentWindow.document.body).html("");
			download_file.iframe.src = url;
			download_file.iframe.style.display = "none";
			if (download_file.iframe.attachEvent){    
			    download_file.iframe.attachEvent("onload", function(){
			    	var list = art.dialog.list;
					for (var i in list) {
					    list[i].close();
					};
			    	var html_c = $(download_file.iframe.contentWindow.document.body).html();
			    	if($.trim(html_c)!=""){
				        var dialog = art.dialog({id: "download001",title: '系统信息提示'});
						dialog.content(html_c);
					}
			    });
			}else{
			    download_file.iframe.onload = function(){        
			    	var list = art.dialog.list;
					for (var i in list) {
					    list[i].close();
					};
			    	var html_c = $(download_file.iframe.contentWindow.document.body).html();
			    	if($.trim(html_c)!=""){
				        var dialog = art.dialog({id: "download001",title: '系统信息提示'});
						dialog.content(html_c);
					}
			    };
			}
		}else{
			art.dialog({
				lock: true,
				title: "系统信息提示",
			    content: "没有数据需要导出，请检查查询结果是否有数据!"
			});
		}
	}
	function innitMapdata(){
     var post_data=$("#datatheme${datatheme.id}form").serialize();
     $.ajax({
				type : "POST",
				url : "data_theme_map_json",
				data:post_data,
				dataType : "json",
				success : function(data) {
					$("#data${datatheme.id}_fenbud").val(data.fenbunow);
					$("#data${datatheme.id}_fenbuall").val(data.fenbuall);
					$("#data${datatheme.id}_eidstr").val(data.eidstr);	
					$("#data${datatheme.id}_records").val(data.records);
					window.townsarray${datatheme.id}=new Array();
					if(data.fenbuall!=null&&data.fenbuall.length>0){
					  var json=JSON.parse(data.fenbuall);
					  for (var i = 0; i < json.length; i++){
					    var townarray=new Array();
					    townarray[0]=json[i].name;
					    townarray[1]=json[i].sum;
					    townsarray${datatheme.id}[i]=townarray;
					  }	
					}			
				}
		});	
    }	
</script>

<div layouth="0">
	<div class="pageHeader">
		<div style="display: none;" id="def${datatheme.id}" data-value="${datatheme.indexdefaultyear!'0'}_${datatheme.indexdefaultseason!'0'}_${datatheme.indexdefaultmonth!'0'}"></div>
		<form onsubmit="return datatheme${datatheme.id}form();" action="data_theme_query_json" method="post" id="datatheme${datatheme.id}form">
			<!-- 要提交的动态参数 -->
			<div id="submit_input${datatheme.id}">
				<input type="hidden" value="index_${indexAttr.istime}_${indexAttr.id}:1" name="dtheme"/>
				<input type="hidden" value="${datatheme.id}" name="dthemeid"/>
				<input type="hidden" id="data${datatheme.id}_fenbud" value="">
				<input type="hidden" id="data${datatheme.id}_fenbuall" value="">
			    <input type="hidden" id="data${datatheme.id}_eidstr" value=""/>
			    <input type="hidden" id="data${datatheme.id}_records" value=""/>
			    <#if datatheme.indexdefaultyear?? && datatheme.indexdefaultyear!=0>
					<input type="hidden" value="eq_0:${datatheme.indexdefaultyear}" name="parm_index['year']" data-type="year">
				</#if>
			    <#if datatheme.indexdefaultseason?? && datatheme.indexdefaultseason!=0>
					<input type="hidden" value="eq_0:${datatheme.indexdefaultseason}" name="parm_index['season']" data-type="season">
				</#if>
			    <#if datatheme.indexdefaultmonth?? && datatheme.indexdefaultmonth!=0>
					<input type="hidden" value="eq_0:${datatheme.indexdefaultmonth}" name="parm_index['month']" data-type="month">
				</#if>
			</div>
			<!--条件操作 -->
			<table class="select_table" style="">
				<!-- 默认条件：模糊查询；年；季度；月 -->
				<tr>
					<td class="select_table_firsttd">
						<em class="termname">
							模糊查询
						</em>
					</td>
					<td class="select_table_contexttd">
						<input type="text" maxlength="100" name="search"/>
						<input type="submit" value="查询"/>
					</td>
				</tr>
				<#if maintable==1 || maintable==2 ||maintable==3 || indexAttr.istime == 1 || indexAttr.istime == 2 || indexAttr.istime == 3>
					<tr>
						<td class="select_table_firsttd">
							<em class="termname">
								年份
							</em>
						</td>
						<td class="select_table_contexttd">
							<ul class="theme_parm_ul">
								<li data-type="year" data-index="year" data-value="novalue" data-if="eq_0">
						  			<div class="parm_div_click">
						  				<em>&nbsp;不限&nbsp;</em>
						  			</div>
						  		</li>
						  		<#list yearData as y>
							  		<li data-type="year" data-index="year" data-value="${y}" data-if="eq_0">
							  			<div class="parm_div">
							  				<em>&nbsp;${y}&nbsp;</em>
							  			</div>
							  		</li>
						  		</#list>
							</ul>
						</td>
					</tr>
				</#if>
				<#if maintable==2 ||maintable==3 || indexAttr.istime == 2 || indexAttr.istime == 3>
					<tr>
						<td class="select_table_firsttd">
							<em class="termname">
								季度
							</em>
						</td>
						<td class="select_table_contexttd">
							<ul class="theme_parm_ul">
								<li data-type="season" data-index="season" data-value="novalue" data-if="eq_0">
						  			<div class="parm_div_click">
						  				<em>&nbsp;不限&nbsp;</em>
						  			</div>
						  		</li>
								<li data-type="season" data-index="season" data-value="1" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>第一季度</em>
						  			</div>
						  		</li>
								<li data-type="season" data-index="season" data-value="2" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>第二季度</em>
						  			</div>
						  		</li>
								<li data-type="season" data-index="season" data-value="3" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>第三季度</em>
						  			</div>
						  		</li>
								<li data-type="season" data-index="season" data-value="4" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>第四季度</em>
						  			</div>
						  		</li>
							</ul>
						</td>
					</tr>
				</#if>
				<#if maintable==3 || indexAttr.istime == 3>
					<tr>
						<td class="select_table_firsttd">
							<em class="termname">
								月份
							</em>
						</td>
						<td class="select_table_contexttd">
							<ul class="theme_parm_ul">
								<li data-type="month" data-index="month" data-value="novalue" data-if="eq_0">
						  			<div class="parm_div_click">
						  				<em>&nbsp;不限&nbsp;</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="1" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>一月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="2" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>二月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="3" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>三月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="4" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>四月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="5" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>五月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="6" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>六月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="7" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>七月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="8" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>八月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="9" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>九月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="10" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>十月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="11" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>十一月</em>
						  			</div>
						  		</li>
								<li data-type="month" data-index="month" data-value="12" data-if="eq_0">
						  			<div class="parm_div">
						  				<em>十二月</em>
						  			</div>
						  		</li>
							</ul>
						</td>
					</tr>
				</#if>
				<!-- 系统后台配置的条件 -->
				<#list datatheme.listterm as term>
				<tr>
					<td class="select_table_firsttd">
						<em class="termname">
							${term.name}
						</em>
					</td>
					<td class="select_table_contexttd">
						<ul class="theme_parm_ul">
							<li data-type="term${term.id}" data-index="${term.index_id!'null'}" data-value="novalue" data-if="eq_${term.index_typeid!'1'}">
								<div class="parm_div_click">
									<em>
										&nbsp;不限&nbsp;
									</em>
								</div>
							</li>
							<#list term.listdatathemeterm as item>
							<li data-type="term${term.id}" data-index="${item.index_id!'null'}" data-value="${item.value}" data-if="${item.value_type}_${item.index_typeid!'1'}">
								<div class="parm_div">
									<em>
										&nbsp;${item.name}&nbsp;
									</em>
								</div>
							</li>
							</#list>
						</ul>
					</td>
				</tr>
				</#list>
			</table>
		</form>
	</div>
	<br/>
	<div id="theme${datatheme.id}_result" class="pageContent">
	 	<div><table id="themegrid${datatheme.id}_MngTb"></table></div>
		<div id="themegrid${datatheme.id}_MngPaper"></div>
	</div>
</div>