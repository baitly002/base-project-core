package org.basecode.common.criterion.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.dashu.lazyapidoc.annotation.Doc;

import java.io.Serializable;

public class ResultMsg<T> implements Serializable{

	private static final long serialVersionUID = 128192891739273982L;
	@JSONField(ordinal = 0)
	@Doc("调用接口是否成功，0：失败，1：成功")
	private Integer status;//调用接口是否成功，0：失败，1：成功
	@JSONField(ordinal = 1)
	@Doc("错误码，当status=0时，必须返回错误码")
	private String errorCode;//错误码，当status=0时，必须返回错误码
	@JSONField(ordinal = 2)
	@Doc("错误具体信息")
	private String message;//错误具体信息
//	@Doc("标准化路径")
//	private String normalisedPath;//标准化路径
//	@Doc("匹配的表达式路径")
//	private String expressionPath;//匹配的表达式路径
	@Doc("返回信息的具体内容")
	private T data;//返回信息的具体内容

	private transient PropertyPreFilter filter;
	
	public ResultMsg() {
	}
	public ResultMsg(Integer status) {
		this.status = status;
		if(status==1){
			this.message = "操作成功";
		}else{
			this.message = "操作失败";
		}
		this.errorCode = "";
//		this.normalisedPath = "";
//		this.expressionPath = "";
	}
	public ResultMsg(Integer status, PropertyPreFilter filter) {
//		this.status = status;
//		if(status==1){
//			this.errorMsg = "操作成功";
//		}else{
//			this.errorMsg = "操作失败";
//		}
//		this.errorCode = "";
		this(status);
		this.filter = filter;
//		this.normalisedPath = normalisedPath;
//		this.expressionPath = expressionPath;
	}
	public ResultMsg(Integer status, PropertyPreFilter filter, T data) {
//		this.status = status;
//		if(status==1){
//			this.errorMsg = "操作成功";
//		}else{
//			this.errorMsg = "操作失败";
//		}
//		this.errorCode = "";
//		this.normalisedPath = normalisedPath;
//		this.expressionPath = expressionPath;
		this(status, filter);
		this.data = data;
	}
	public ResultMsg(Integer status, String errorCode, String message, PropertyPreFilter filter, T data) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
		this.filter = filter;
		this.data = data;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
//	public String getNormalisedPath() {
//		return normalisedPath;
//	}
//	public void setNormalisedPath(String normalisedPath) {
//		this.normalisedPath = normalisedPath;
//	}
//	public String getExpressionPath() {
//		return expressionPath;
//	}
//	public void setExpressionPath(String expressionPath) {
//		this.expressionPath = expressionPath;
//	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}

	public PropertyPreFilter getFilter() {
		return filter;
	}

	public void setFilter(PropertyPreFilter filter) {
		this.filter = filter;
	}
}
