package com.locservice.ui.helpers;

import java.util.Stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.fragments.ChatFragment;
import com.locservice.ui.fragments.DriverFragment;
import com.locservice.ui.fragments.FavoriteAddressesFragment;
import com.locservice.ui.fragments.MapViewFragment;
import com.locservice.ui.fragments.OrderHistoryFragment;
import com.locservice.ui.fragments.OrderInfoFragment;
import com.locservice.ui.fragments.OrderRateFragment;
import com.locservice.ui.fragments.ProfileFragment;
import com.locservice.ui.fragments.RewardsFragment;
import com.locservice.ui.fragments.SearchFragment;
import com.locservice.ui.fragments.SocialNetworkFragment;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.Logger;

public class FragmentHelper {
	
	private static final String TAG = FragmentHelper.class.getSimpleName();
	
	private static FragmentHelper mInstance = null;
	
	
	private Stack<StackItem> fragmentStack = new Stack<StackItem>();

	public static FragmentHelper getInstance() {
		if(mInstance == null)
			mInstance = new FragmentHelper();
		return mInstance;
	}
	
	public FragmentHelper() {
		
	}
	
	
	/**
	 * Method for creating subsequent fragment.
	 * @param context - fragment context
	 * @param type - fragment type
	 * @param args - bundle arguments
	 */
	public Fragment openFragment(FragmentActivity context, FragmentTypes type, Bundle args) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: FragmentHelper.openFragment : type : " + type);
		Fragment fragment = null;
        switch (type) {
            case MAP:
                fragment = new MapViewFragment();
                fragment.setArguments(args);
                break;
            case ORDER:
            	// TODO CREATE ORDER FRAGMENT
                break;
            case ORDER_HISTORY:
            	fragment = new OrderHistoryFragment();
            	fragment.setArguments(args);
                break;
            case ORDER_RATE :
            	fragment = new OrderRateFragment();
            	fragment.setArguments(args);
            	break;
            case SEARCH :
            	fragment = new SearchFragment();
            	fragment.setArguments(args);
            	break;
            case ORDER_INFO :
            	fragment = new OrderInfoFragment();
            	fragment.setArguments(args);
            	break;
            case DRIVER_INFO :
            	fragment = new DriverFragment();
            	fragment.setArguments(args);
            	break;
            case REWARDS :
            	fragment = new RewardsFragment();
            	fragment.setArguments(args);
            	break;
            case PROFILE  :
            	fragment = new ProfileFragment();
            	fragment.setArguments(args);
            	break;
            case FAVORITE_ADDRESSES :
            	fragment = new FavoriteAddressesFragment();
            	fragment.setArguments(args);
            	break;
            case SOCIAL_NETWORK :
            	fragment = new SocialNetworkFragment();
            	fragment.setArguments(args);
            	break;
            case CHAT :
            	fragment = new ChatFragment();
            	fragment.setArguments(args);
            	break;
            default:
            	// MAP FRAGMENT
            	fragment = new MapViewFragment();
                fragment.setArguments(args);
                break;
        }
        
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        ft.add(R.id.content_frame, fragment);
//        if(!isFirst) {
//        	fragmentStack.lastElement().getFragmentItem().onPause();
//        	ft.hide(fragmentStack.lastElement().getFragmentItem());
//        }
//        StackItem stackItem = new StackItem();
//        stackItem.setFragmentItem(fragment);
//        stackItem.setFragmentType(type);
//        fragmentStack.push(stackItem);
//        ft.commit();
        
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        
        return fragment;
	}
	
	public class StackItem {
		Fragment fragmentItem;
		boolean menu;
		FragmentTypes fragmentType;
		public FragmentTypes getFragmentType() {
			return fragmentType;
		}
		public void setFragmentType(FragmentTypes fragmentType) {
			this.fragmentType = fragmentType;
		}
		public Fragment getFragmentItem() {
			return fragmentItem;
		}
		public void setFragmentItem(Fragment fragmentItem) {
			this.fragmentItem = fragmentItem;
		}
		public boolean isMenu() {
			return menu;
		}
		public void setMenu(boolean menu) {
			this.menu = menu;
		}
	}
	
	/**
	 * Implement fragment back functionality
	 */
	public void fragmentOnBack() {
//		FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
//		fragmentStack.lastElement().getFragmentItem().onPause();
//		ft.remove(fragmentStack.pop().getFragmentItem());
//		fragmentStack.lastElement().getFragmentItem().onResume();
//		ft.show(fragmentStack.lastElement().getFragmentItem());
//		ft.commit();
//		if(fragmentStack.lastElement().isMenu()) {
//			((MainActivity)mContext).OpenMenuDrawer();
//		} else {
//			((MainActivity)mContext).CloseMenuDrawer();
//		}
	}
	
	/**
	 * Method for getting stack of fragments
	 * @return stack
	 */
	public Stack<StackItem> getFragmentStack() {
		return fragmentStack;
	} 
	
	/**
	 * Method for getting fragment string via type
	 * @param fragmentType
	 * @return
	 */
	public static String getFragmentTag(FragmentTypes fragmentType) {
		switch (fragmentType) {
			case MAP:
				return CMAppGlobals.FRAGMENT_MAP;
			case ORDER:
				return CMAppGlobals.FRAGMENT_ORDER;
			case ORDER_HISTORY:
				return CMAppGlobals.FRAGMENT_ORDER_HISTORY;
			case ORDER_RATE:
				return CMAppGlobals.FRAGMENT_ORDER_RATE;
			default:
				break;
		}
		return CMAppGlobals.FRAGMENT_NONE;
	}

}
