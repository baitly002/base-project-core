package ${package}.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import ${package}.dao.${MapperName};
import ${package}.entity.${BeanName};
import com.nhjm.util.entity.Msg;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class ${ActionName} extends ActionSupport implements Serializable {
	private static final long serialVersionUID = 1L;
	@Autowired
	private ${MapperName} mapper;
	private ${BeanName} ${beanName};
	private List<${BeanName}> list${BeanName};
	
	/*必须参数*/
	private int id;
	private String idStr;
	private int rows = 10;
	private int page = 1;
	private int total = 0;
	private int records = 0;
	private Msg msg;
	private Map<String, Object> data_result;

	public ${MapperName} getMapper() {
		return mapper;
	}
	public void setMapper(${MapperName} mapper) {
		this.mapper = mapper;
	}
	public ${BeanName} get${BeanName}() {
		return ${beanName};
	}
	public void set${BeanName}(${BeanName} ${beanName}) {
		this.${beanName} = ${beanName};
	}
	public List<${BeanName}> getList${BeanName}() {
		return list${BeanName};
	}
	public void setList${BeanName}(List<${BeanName}> list${BeanName}) {
		this.list${BeanName} = list${BeanName};
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdStr() {
		return idStr;
	}
	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	public Msg getMsg() {
		return msg;
	}
	public void setMsg(Msg msg) {
		this.msg = msg;
	}
	public Map<String, Object> getData_result() {
		return data_result;
	}
	public void setData_result(Map<String, Object> data_result) {
		this.data_result = data_result;
	}
	
	
	public String json() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startrow", ((page - 1) * rows));
		map.put("endrow", page * rows);
		
		this.list${BeanName} = mapper.select(map);
		this.records = mapper.count(map);
		this.total = (int) Math.ceil((double) records / (double) rows);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("page", this.getPage());
		m.put("total", this.getTotal());
		m.put("records", this.getRecords());
		m.put("rows", this.getList${BeanName}());
		this.data_result = m;
		return "SUCCESS";
	}
	public String select() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startrow", ((page - 1) * rows));
		map.put("endrow", page * rows);
		
		this.list${BeanName} = mapper.select(map);
		this.records = mapper.count(map);
		this.total = (int) Math.ceil((double) records / (double) rows);
		return "SUCCESS";
	}
	public String manager(){
		//前往管理页面
		return "SUCCESS";
	}
	public String add(){
		//前往添加页面
		return "SUCCESS";
	}
	public String saveAdd(){
		//保存添加数据页面
		if(${beanName}!=null){
			int rs = mapper.insert(${beanName});
			if(rs!=0){
				Msg m = new Msg("success","操作成功");
				this.msg = m;
			}else{
				Msg m = new Msg("error","操作失败");
				this.msg = m;
			}
		}else{
			Msg m = new Msg("error","不能添加空数据，请填写好信息后再提交");
			this.msg = m;
		}
		return "SUCCESS";
	}
	public String edit(){
		//前往编辑页面
		return "SUCCESS";
	}
	public String saveEdit(){
		//更新数据
		if(${beanName}!=null){
			int rs = mapper.updateByPrimaryKey(${beanName});
			if(rs!=0){
				Msg m = new Msg("success","操作成功");
				this.msg = m;
			}else{
				Msg m = new Msg("error","操作失败");
				this.msg = m;
			}
		}else{
			Msg m = new Msg("error","不能添加空数据，请填写好信息后再提交");
			this.msg = m;
		}
		return "SUCCESS";
	}
	public String delete(){
		//删除数据
		if(id!=0&&idStr==null){
			//删除单条数据
			int rs = mapper.deleteByPrimaryKey(id);
			if(rs!=0){
				Msg m = new Msg("success","操作成功");
				this.msg = m;
			}else{
				Msg m = new Msg("error","操作失败");
				this.msg = m;
			}
		}else if(id==0&&idStr!=null){
			//删除多条数据
			String ids[] = idStr.split(",");
			int rs = 0;
			int re = 0;
			for (String i : ids) {
				int r = mapper.deleteByPrimaryKey(Integer.parseInt(i.trim()));
				if(r == 0){
					re++;
				}else{
					rs++;
				}
			}
			Msg m = new Msg("success","操作成功,共删除"+rs+"条，"+re+"条失败！");
			this.msg = m;
		}else{
			Msg m = new Msg("error","请选择数据后再操作");
			this.msg = m;
		}
		return "SUCCESS";
	}
	public String show(){
		//显示详细信息
		if(id!=0){
			this.${beanName}=mapper.selectByPrimaryKey(id);
		}else{
			Msg m = new Msg("error","请选择数据后再操作");
			this.msg = m;
		}
		return "SUCCESS";
	}
}
