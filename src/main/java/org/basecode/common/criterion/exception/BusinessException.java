package org.basecode.common.criterion.exception;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.basecode.common.config.base.CommonConfig;
import org.basecode.common.util.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * @ClassName: BusinessException
 * @Description: 业务异常
 * 
 */
public class BusinessException extends BaseException {

	private static final long	serialVersionUID	= -9177074810415018988L;

	public static final String	UNKNOWN_EXCEPTION	= "1000000";				// 未知异常

	private String				expCode				= null;					// 异常编码

	private String				expDesc				= null;					// 异常描述
	private Object	data; //额外数据
	private int httpStatus = 200;

	public BusinessException() {
		super();
	}

	public BusinessException(String expDesc) {
		super(expDesc);
		this.expDesc = expDesc;
	}

	public BusinessException(String expDesc, Throwable cause) {
		super(expDesc, cause);
		this.expDesc = expDesc;
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String expCode, String expDesc) {
		this(200, expCode, expDesc, null, new String[] {});
	}
	public BusinessException(int httpStatus, String expCode, String expDesc) {
		this(httpStatus, expCode, expDesc, null, new String[] {});
	}
	
	public BusinessException(String expCode, String expDesc, Object data) {
		this(200, expCode, expDesc, data, new String[] {});
	}
	
	public BusinessException(int httpStatus, String expCode, String expDesc, Object data) {
		this(httpStatus, expCode, expDesc, data, new String[] {});
	}
	
	public BusinessException(String expCode, String expDesc, Object data, Object... params) {
		this(200, expCode, expDesc, data, params);
	}
	
	public BusinessException(int httpStatus, String expCode, String expDesc, Object data, Object... params) {
		super(String.format("【TraceId:%s; 异常编码是：%s；异常提示是：%s】", TraceContext.traceId(), (expCode == null ? UNKNOWN_EXCEPTION : expCode), expDesc));
		this.httpStatus = httpStatus;
		String msg = "";
		if(!StringUtils.isBlank(expCode)){
//			msg = ProHolder.getString(expCode);
			msg = CommonConfig.getValue("spring.basecode.error["+expCode+"]");
		}
		if(StringUtils.isBlank(msg)){
			//如果没有在配置文件中配置该消息，则从代码中获取
			//m.put("errorMsg", errorMsg);
			msg = expDesc;
		}
		if(params != null && params.length>0) {
			//int i=0;
			for(Object param : params) {
				//msg = msg.replace("{"+i+"}", param);
				//i++;
				msg = msg.replaceFirst("\\{}", String.valueOf(param));
			}
		}
		this.expCode = (expCode == null ? UNKNOWN_EXCEPTION : expCode);
		this.expDesc = msg;
		this.data = data;
	}

//	public BusinessException(String expCode, String expDesc, Throwable cause) {
//		this(expCode, expDesc);
//		super.initCause(cause);， 
//	}

	/**
	 * 获取异常中的BusinessException
	 * 
	 * @return 如果异常中不存在BusinessException则返回null
	 */
	public static BusinessException getBottomDacsRuntimeException(Exception e) {
		Throwable tmp = e;
		while (tmp != null) {
			if (tmp instanceof BusinessException)
				return (BusinessException) tmp;
			tmp = tmp.getCause();
		}
		return null;
	}

	/**
	 * 提取异常中嵌套的简短消息（过滤了CallStack的信息）
	 * 
	 * @param t
	 * @return
	 */
	public static String getNestedMessage(Throwable t) {
		StringBuilder sb = null;

		if (t == null) {
			return null;
		} else {
			sb = new StringBuilder();
			getNestedMessage(t, sb);

			return sb.toString();
		}
	}

	private static void getNestedMessage(Throwable t, StringBuilder sb) {
		String msg = null;
		Throwable cause = null;

		msg = t.getMessage();
		if (msg != null) {
			sb.append(msg).append(" ");
		} else {
			sb.append(" null. ");
		}

		cause = t.getCause();
		while (cause != null && cause != t) {
			msg = cause.getMessage();

			if (msg != null) {
				sb.append(" {Nested caused by: ").append(msg).append("} ");
			} else {
				sb.append(" {Nested caused by: ").append("null").append("} ");
			}

			cause = cause.getCause();
		}

		return;
	}

	/**
	 * 提取异常中嵌套的简短消息（过滤了CallStack的信息）
	 * 
	 * @param t
	 * @return
	 */
	public static String getNestedMessage(List<Throwable> tList) {
		StringBuilder sb = null;
		int index = 1;

		if (tList == null) {
			return null;
		} else {
			sb = new StringBuilder();
			for (Iterator<Throwable> i = tList.iterator(); i.hasNext(); index++) {
				Throwable t = i.next();

				sb.append(String.format(" Caused by[%d]{ ", index));
				getNestedMessage(t, sb);
				sb.append(" } ");
			}

			return sb.toString();
		}
	}

	public static String toNestedString(Throwable t) {
		StringBuilder sb = null;

		if (t == null) {
			return null;
		} else {
			sb = new StringBuilder();

			toNestedString(t, sb);

			return sb.toString();
		}
	}

	public static String toNestedString(List<Throwable> tList) {
		StringBuilder sb = null;
		int index = 1;

		if (tList == null) {
			return null;
		} else {
			sb = new StringBuilder();
			for (Iterator<Throwable> i = tList.iterator(); i.hasNext(); index++) {
				Throwable t = i.next();

				sb.append(String.format("Caused by[%d]{ ", index));
				toNestedString(t, sb);
				sb.append(" }");
			}

			return sb.toString();
		}
	}

	private static void toNestedString(Throwable t, StringBuilder sb) {
		String msg = null;
		Throwable cause = null;

		msg = t.toString();
		if (msg != null) {
			sb.append(msg).append(" ");
		} else {
			sb.append("null ");
		}

		cause = t.getCause();
		while (cause != null && cause != t) {
			msg = cause.toString();

			if (msg != null) {
				sb.append(" {Nested caused by: ").append(msg).append(" } ");
			} else {
				sb.append(" {Nested caused by: ").append("null").append(" } ");
			}

			cause = cause.getCause();
		}

		return;
	}

	public String toString() {
		return String.format("【异常类型是：%s；异常编码是：%s；异常提示是：%s】", this.getClass().getName(), expCode, expDesc);
	}

	public String getExpCode() {
		return expCode;
	}

	public void setExpCode(String expCode) {
		this.expCode = expCode;
	}

	public String getExpDesc() {
		return expDesc;
	}

	public void setExpDesc(String expDesc) {
		this.expDesc = expDesc;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
