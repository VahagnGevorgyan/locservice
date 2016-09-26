package com.locservice.api.entities;

import com.locservice.ui.utils.ScrollType;

/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ServiceOptionsBase {

	private String title;
	private String[] scrollItems;
	private boolean isCildSeat;
	private ScrollType itemType;
	
	public boolean isCildSeat() {
		return isCildSeat;
	}
	public void setCildSeat(boolean isCildSeat) {
		this.isCildSeat = isCildSeat;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String[] getScrollItems() {
		return scrollItems;
	}
	public void setScrollItems(String[] scrollItems) {
		this.scrollItems = scrollItems;
	}

	public ScrollType getItemType() {
		return itemType;
	}

	public void setItemType(ScrollType itemType) {
		this.itemType = itemType;
	}
}
