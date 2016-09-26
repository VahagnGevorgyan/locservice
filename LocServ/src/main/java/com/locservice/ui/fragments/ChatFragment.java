package com.locservice.ui.fragments;

import com.locservice.R;
import com.locservice.ui.emojicon.EmojiconsFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.locservice.ui.emojicon.EmojiconGridFragment;
import com.locservice.ui.emojicon.emoji.Emojicon;
import com.locservice.ui.utils.KeyBoardHelper;
import com.locservice.utils.Utils;

public class ChatFragment extends Fragment implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
	
	private View rootView;
	private FragmentActivity mContext;
	private ImageView imageViewBack;
	private ImageView imageViewCall;
	private ImageView imageViewAttachFile;
	private ImageView imageViewFace;
	private ImageView imageViewAvatar;
	private ListView listViewChat;
//	private ChatAdapter adapter;
//	private List<Chat> chatList;
	
	EditText mEditEmojicon;
	private boolean emojiEnabled = false;
	private boolean keyboardShown = false;
	private LinearLayout lnLayEmogParent;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_chat, container, false);
		mContext = getActivity();
		mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
//		listViewChat = (ListView) rootView.findViewById(R.id.listViewChat);
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		imageViewCall = (ImageView) rootView.findViewById(R.id.imageViewCall);
		imageViewAttachFile = (ImageView) rootView.findViewById(R.id.imageViewAttachFile);
		imageViewFace = (ImageView) rootView.findViewById(R.id.imageViewFace);
		imageViewAvatar = (ImageView) rootView.findViewById(R.id.imageViewAvatar);
		imageViewBack.setOnClickListener(this);
		imageViewCall.setOnClickListener(this);
		imageViewAttachFile.setOnClickListener(this);
		imageViewFace.setOnClickListener(this);
		imageViewAvatar.setOnClickListener(this);
		
//		chatList = new ArrayList<Chat>();
//		addChatList(chatList,
//				null,
//				getResources().getString(R.string.str_chat_first_message),
//				null,
//				getResources().getString(R.string.str_call),
//				false);
//		adapter = new ChatAdapter(mContext, chatList);
//		listViewChat.setAdapter(adapter);
		
		mEditEmojicon = (EditText) rootView.findViewById(R.id.editEmojicon);
		lnLayEmogParent = (LinearLayout) rootView.findViewById(R.id.lnLayEmogParent);
		((EmojiconEditText)mEditEmojicon).setActivityForEditText(mContext);
		mEditEmojicon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				keyboardShown = true;
				
				if(emojiEnabled){
//				   hideEmojis();	
				}
//				KeyBoardHelper.Show(mEditEmojicon, getApplicationContext());
			}
		});	
		
		return rootView;
	}
	
//	public void addChatList (List<Chat> list,Bitmap imageBitmap, String message, String operatorOrTime, String doSomethingText, boolean isUser ) {
//		Chat chat = new Chat();
//		if (message != null) chat.setMessage(message);
//		chat.setUser(isUser);
//		if (isUser) {
//			if (operatorOrTime != null) chat.setTime(operatorOrTime);
//			if (imageBitmap != null) chat.setUserImageBitmap(imageBitmap);
//		} else {
//			if (operatorOrTime != null) chat.setOperatorName(operatorOrTime);
//			if (imageBitmap != null) chat.setServiceImageBitmap(imageBitmap);
//			if (doSomethingText != null) chat.setDoSomethingText(doSomethingText);
//		}
//		if (list != null) {
//			list.add(chat);
//		}
//	}
	
	@Override
	public void onClick(View v) {
		// check network status
		if (Utils.checkNetworkStatus(mContext)) {

			switch (v.getId()) {
			case R.id.imageViewBack :
				if (mContext != null) {
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mEditEmojicon.getWindowToken(), 0);
					mContext.onBackPressed();
				}
				break;
			case R.id.imageViewCall :
				Toast.makeText(mContext, "Call", Toast.LENGTH_SHORT).show();
				break;
			case R.id.imageViewAttachFile :
				Toast.makeText(mContext, "Attach File", Toast.LENGTH_SHORT).show();
				break;
			case R.id.imageViewFace :
	//			EmojiconsFragment.input(mEditEmojicon, emojicon);
	//			// activating send icon
	//			activateSendButton(true);
	//			emojiEnabled = true;
				// must show emojies
				if(lnLayEmogParent.getVisibility() == View.GONE){
					// hide keyboard and show emojis
					emojiEnabled = true;
					if(keyboardShown){
					   KeyBoardHelper.Hide(mEditEmojicon, mContext);
					}
	//				imgViewEmoji.setImageResource(R.drawable.ic_keyboard_normal);
					lnLayEmogParent.setVisibility(View.VISIBLE);

				}else{
					if(lnLayEmogParent.getVisibility() == View.VISIBLE){
						// hiding emojis field
						emojiEnabled = false;
						keyboardShown = true;
	//					hideEmojis();
							KeyBoardHelper.Show(mEditEmojicon, mContext);
					}
				}
				break;
			case R.id.imageViewAvatar :
				if (!mEditEmojicon.getText().toString().isEmpty()) {
	//				addChatList(chatList,
	//						BitmapFactory.decodeResource(getResources(), R.drawable.profile_photo),
	//						mEditEmojicon.getText().toString(),
	//						"Today, 17:30",
	//						null,
	//						true);
	//				adapter.notifyDataSetChanged();
					mEditEmojicon.setText("");
				}
				break;

			}
		}
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(mEditEmojicon);
        if(mEditEmojicon.getText().toString().trim().equals("")){
        	// deactivating send message button
//        	activateSendButton(false);
        	emojiEnabled = false;
        }
		
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(mEditEmojicon, emojicon);
        // activating send icon
//        activateSendButton(true);
        emojiEnabled = true;
		
	}

}
