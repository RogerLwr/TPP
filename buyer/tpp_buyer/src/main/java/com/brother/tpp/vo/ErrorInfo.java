package com.brother.tpp.vo;

import java.io.Serializable;

/**
 * 错误信息提示
 * @author Administrator
 *
 */
public class ErrorInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 错误的状态
	 */
	public int errorState;
	/**
	 * 错误的消息
	 */
	public String errorMsg;
}
