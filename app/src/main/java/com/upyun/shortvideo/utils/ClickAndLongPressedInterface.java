/**
 * TuSDKVideoDemo
 * LongPressedAndClickInterface.java
 *
 * @author  XiaShengCui
 * @Date  Jun 1, 2017 7:34:44 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.utils;

/**
 * 点击长按回调
 * 
 * @author XiaShengCui
 *
 */
public interface ClickAndLongPressedInterface {
	
    // 长按状态按下
	void onLongPressedDown();
	
	// 长按状态释放
	void onLongPressedUp();
	
	// 点击状态
	void onClick();
}
