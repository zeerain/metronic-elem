package com.elementwin.bs.model;

/***
 * Ecard-个人名片
 * @author Yaojf@dibo.ltd
 */
public class ECard extends dibo.framework.model.BaseModel{
	
	private static final long serialVersionUID = 7264930043949060493L;
	
	// 个人名片二维码参数
	private String ecardSize = "160"; // 大小 默认160
	private String ecardFill = "#000"; // 颜色 默认黑色块：#000
	private String ecardRadius = "0.25"; // 半径(直角：0 小圆角：0.25 大圆角：0.5) 默认0.25小圆角
	private String ecardMode = "2"; // 码上签名(无签名：0  有签名：2) 默认2有签名
	private String ecardFontcolor = null; //码上签名颜色 默认黑色：#000
	
	public String getEcardSize() {
		return ecardSize;
	}

	public void setEcardSize(String ecardSize) {
		this.ecardSize = ecardSize;
	}

	public String getEcardFill() {
		return ecardFill;
	}

	public void setEcardFill(String ecardFill) {
		this.ecardFill = ecardFill;
	}

	public String getEcardRadius() {
		return ecardRadius;
	}

	public void setEcardRadius(String ecardRadius) {
		this.ecardRadius = ecardRadius;
	}

	public String getEcardMode() {
		return ecardMode;
	}

	public void setEcardMode(String ecardMode) {
		this.ecardMode = ecardMode;
	}

	public String getEcardFontcolor() {
		return ecardFontcolor;
	}

	public void setEcardFontcolor(String ecardFontcolor) {
		this.ecardFontcolor = ecardFontcolor;
	}
}
