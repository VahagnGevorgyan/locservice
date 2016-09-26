package com.locservice.ui.helpers;

import android.text.TextUtils;

import com.locservice.CMApplication;

public enum OrderState {

//	ORDER_REGISTER,
//	DRIVER_SEARCH,
//	DRIVER_FOUND,
//	DRIVER_ONWAY,
//	DRIVER_ARRIVED,
//	DRIVER_TRIP,
//	ORDER_FINISH,


	UNKNOWN(com.locservice.R.string.os_u),
	SC(com.locservice.R.string.os_sc),
	R(com.locservice.R.string.os_r),
	A(com.locservice.R.string.os_a),
	RC(com.locservice.R.string.os_rc),
	BR(com.locservice.R.string.os_br),
	OW(com.locservice.R.string.os_ow),
	AR(com.locservice.R.string.os_ar),
	CC(com.locservice.R.string.os_cc),
	CP(com.locservice.R.string.os_cp),
	NC(com.locservice.R.string.os_nc),
	LATER_R(com.locservice.R.string.os_later_r),
	ASAP_R(com.locservice.R.string.os_asap_r),
	LATER_A(com.locservice.R.string.os_later_a),
	ASAP_A_BUSY(com.locservice.R.string.os_asap_a_busy),
	ASAP_A_THIS(com.locservice.R.string.os_asap_a_this),
	ORDER_CONFIRM(com.locservice.R.string.os_order_confirm);

	public static final int STATUS_ACTIVE = 0;
	public static final int STATUS_DONE = 1;

	private int mState;

	private OrderState(int state) {
		this.mState = state;
	}

	@Override
	public String toString() {
		return CMApplication.getAppContext().getString(this.mState);
	}

	public static OrderState getStateByOrdinal(int state) {
		for (OrderState orderState : values()) {
			if (orderState.ordinal() == state) {
				return orderState;
			}
		}
		return UNKNOWN;
	}

	public static int getStatusByOrdinal(int state) {
		if (state == CP.ordinal() || state == CC.ordinal()
				|| state == NC.ordinal())
			return STATUS_DONE;
		else
			return STATUS_ACTIVE;
	}

	public static String getStatusStringByState(OrderState state) {
		if (state != null) {
			switch (state) {
				case A:
					return "A";
				case R:
					return "R";
				case OW:
					return "OW";
				case AR:
					return "AR";
				case BR:
					return "BR";
				case CC:
					return "CC";
				case CP:
					return "CP";
				case NC:
					return "NC";
				case RC:
					return "RC";
				case SC:
					return "SC";
				case LATER_A:
					return "LATER_A";
				case ASAP_R:
					return "ASAP_R";
				case LATER_R:
					return "LATER_R";
				case ASAP_A_BUSY:
					return "ASAP_A_BUSY";
				case ASAP_A_THIS:
					return "ASAP_A_THIS";
			}
		}

		return "";
	}

	public static OrderState getStateByStatus(String status) {
		OrderState state = UNKNOWN;
		if(status != null) {
			if (TextUtils.equals(status, "A")) {
				state = A;
			} else if (TextUtils.equals(status, "R")) {
				state = R;
			} else if (TextUtils.equals(status, "OW")) {
				state = OW;
			} else if (TextUtils.equals(status, "AR")) {
				state = AR;
			} else if (TextUtils.equals(status, "BR")) {
				state = BR;
			} else if (TextUtils.equals(status, "CC")) {
				state = CC;
			} else if (TextUtils.equals(status, "CP")) {
				state = CP;
			} else if (TextUtils.equals(status, "NC")) {
				state = NC;
			} else if (TextUtils.equals(status, "RC")) {
				state = RC;
			} else if (TextUtils.equals(status, "SC")) {
				state = SC;
			} else if (TextUtils.equals(status, "SC")) {
				state = SC;
			} else if (TextUtils.equals(status, "LATER_A")) {
				state = LATER_A;
			} else if (TextUtils.equals(status, "ASAP_R")) {
				state = ASAP_R;
			} else if (TextUtils.equals(status, "LATER_R")) {
				state = LATER_R;
			} else if (TextUtils.equals(status, "ASAP_A_BUSY")) {
				state = ASAP_A_BUSY;
			} else if (TextUtils.equals(status, "ASAP_A_THIS")) {
				state = ASAP_A_THIS;
			}
		}

		return state;
	}
}

