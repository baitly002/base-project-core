/**
 * 
 */
package org.basecode.core.criterion.exception;


/**
 * 
 * @author ShenHuaJie
 * @version 2016年6月7日 下午8:43:02
 */
@SuppressWarnings("serial")
public abstract class BaseException extends RuntimeException {
	public BaseException() {
	}

	public BaseException(Throwable ex) {
		super(ex);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, Throwable ex) {
		super(message, ex);
	}
}
