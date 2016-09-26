package com.locservice.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.adapters.ChatRVAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ChatAddCommentData;
import com.locservice.api.entities.ChatGetCommentsData;
import com.locservice.api.entities.City;
import com.locservice.api.entities.Comment;
import com.locservice.api.manager.ChatManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.db.CityDBManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.emojicon.EmojiconGridFragment;
import com.locservice.ui.emojicon.EmojiconsFragment;
import com.locservice.ui.emojicon.emoji.Emojicon;
import com.locservice.ui.fragments.EmojiconEditText;
import com.locservice.ui.utils.KeyBoardHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity implements View.OnClickListener,
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        ICallBack, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private ChatActivity mContext;
    private RecyclerView recyclerViewChat;
    private List<Comment> chatList = new ArrayList<>();
    private EditText mEditEmojicon;
    private ChatRVAdapter adapter;
    private LinearLayout lnLayEmogParent;
    private boolean emojiEnabled = false;
    private boolean keyboardShown = false;
    private View viewShadow;
    private ChatManager chatManager;
    private LinearLayoutManager layoutManager;
    private CityDBManager cityDBManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean isKeyboardOpened = false;
    private long lastMessageId;
    private boolean isLast;
    private boolean isSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatActivity.onCreate ");

        //change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(ContextCompat.getColor(this, com.locservice.R.color.color_status_bar));
            window.setNavigationBarColor(ContextCompat.getColor(this, com.locservice.R.color.color_status_bar));
        }

        // check network status
        if (Utils.checkNetworkStatus(this)) {

            // Checking is user registered
            FrameLayout layoutMainContent = (FrameLayout) findViewById(R.id.layoutMainContent);
            RelativeLayout layoutRegisterContent = (RelativeLayout) findViewById(R.id.layoutRegisterContent);
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setOnRefreshListener(this);
            }
            String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
            if (!auth_token.equals("")) {
                if (layoutMainContent != null) {
                    layoutMainContent.setVisibility(View.VISIBLE);
                }
                if (layoutRegisterContent != null) {
                    layoutRegisterContent.setVisibility(View.GONE);
                }
            } else {
                if (layoutMainContent != null) {
                    layoutMainContent.setVisibility(View.GONE);
                }
                if (layoutRegisterContent != null) {
                    layoutRegisterContent.setVisibility(View.VISIBLE);
                }
                RelativeLayout layoutGoToRegister = (RelativeLayout) findViewById(R.id.layoutGoToRegister);
                if (layoutGoToRegister != null) {
                    layoutGoToRegister.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(mContext, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
                        }
                    });
                }
            }

            mContext = this;
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            viewShadow = mContext.findViewById(R.id.viewShadow);

            cityDBManager = new CityDBManager(this);

            recyclerViewChat = (RecyclerView) mContext.findViewById(R.id.recyclerViewChat);
            ImageView imageViewBack = (ImageView) mContext.findViewById(R.id.imageViewBack);
            ImageView imageViewCall = (ImageView) mContext.findViewById(R.id.imageViewCall);
            ImageView imageViewAttachFile = (ImageView) mContext.findViewById(R.id.imageViewAttachFile);
            ImageView imageViewFace = (ImageView) mContext.findViewById(R.id.imageViewFace);
            ImageView imageViewAvatar = (ImageView) mContext.findViewById(R.id.imageViewAvatar);
            if (imageViewBack != null) {
                imageViewBack.setOnClickListener(this);
            }
            if (imageViewCall != null) {
                imageViewCall.setOnClickListener(this);
            }
            if (imageViewAttachFile != null) {
                imageViewAttachFile.setOnClickListener(this);
            }
            if (imageViewFace != null) {
                imageViewFace.setOnClickListener(this);
            }
            if (imageViewAvatar != null) {
                imageViewAvatar.setOnClickListener(this);
            }


            chatList.add(createFirstItem());
            adapter = new ChatRVAdapter(mContext, chatList);
            layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
            if (!chatList.isEmpty()) {
                layoutManager.scrollToPosition(chatList.size() - 1);
            }
            recyclerViewChat.setLayoutManager(layoutManager);
            recyclerViewChat.setAdapter(adapter);

            mEditEmojicon = (EditText) mContext.findViewById(R.id.editEmojicon);
            lnLayEmogParent = (LinearLayout) mContext.findViewById(R.id.lnLayEmogParent);
            ((EmojiconEditText) mEditEmojicon).setActivityForEditText(mContext);
            mEditEmojicon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    keyboardShown = true;

                    if (emojiEnabled) {
                        hideEmojis();
                    }
                    KeyBoardHelper.Show(mEditEmojicon, getApplicationContext());
                }
            });

            recyclerViewChat.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (listIsAtTop()) {
                                viewShadow.setVisibility(View.GONE);
                            } else {
                                viewShadow.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 50);
                }
            });

            if (!auth_token.isEmpty()) {
                chatManager = new ChatManager(this);
                chatManager.GetComments(0);
            }

            final RelativeLayout layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);
            if (layoutContent != null) {
                layoutContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        int heightDiff = layoutContent.getRootView().getHeight() - layoutContent.getHeight();
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: ChatActivity.onGlobalLayout : getRootView : " + layoutContent.getRootView().getHeight()
                                    + " : getHeight : " + layoutContent.getHeight());
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: ChatActivity.onGlobalLayout : heightDiff : " + heightDiff);
                        if (heightDiff < layoutContent.getHeight() / 5) {
                            if (isKeyboardOpened) {
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: ChatActivity.onGlobalLayout : action : closed");
                            }
                            isKeyboardOpened = false;
                        } else {
                            if (!isKeyboardOpened) {
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: ChatActivity.onGlobalLayout : action : opened");
                                layoutManager.scrollToPosition(chatList.size() - 1);
                            }
                            isKeyboardOpened = true;
                        }
                    }
                });
            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CMAppGlobals.REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            onBackPressed();
        }
    }

    private boolean listIsAtTop() {
        return recyclerViewChat.getChildCount() == 0 || recyclerViewChat.getChildAt(0).getTop() == 0;
    }

    protected void hideEmojis() {
        if (lnLayEmogParent.getVisibility() == View.VISIBLE)
            lnLayEmogParent.setVisibility(View.GONE);

    }

    /**
     * Method for creating comment list first item
     *
     * @return - comment object
     */
    public Comment createFirstItem() {
        Comment comment = new Comment();
        comment.setComment(getResources().getString(R.string.str_chat_first_message));
        comment.setByuser(0);
        return comment;

    } // end method createFirstItem


    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(ChatActivity.this)) {

            switch (v.getId()) {
                case R.id.imageViewBack:
                    if (mContext != null) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mEditEmojicon.getWindowToken(), 0);
                        mContext.onBackPressed();
                    }
                    break;
                case R.id.imageViewCall:
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    // get current city phone number
                    String phoneNumber = getCurrentCityPhone();
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                    break;
                case R.id.imageViewAttachFile:
                    Toast.makeText(mContext, "Attach File", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.imageViewFace:
                    // must show emojies
                    if (lnLayEmogParent.getVisibility() == View.GONE) {
                        // hide keyboard and show emojis
                        emojiEnabled = true;
                        if (keyboardShown) {
                            KeyBoardHelper.Hide(mEditEmojicon, mContext);
                        }
                        lnLayEmogParent.setVisibility(View.VISIBLE);
                    } else {
                        if (lnLayEmogParent.getVisibility() == View.VISIBLE) {
                            // hiding emojis field
                            emojiEnabled = false;
                            keyboardShown = true;
                            hideEmojis();
                            KeyBoardHelper.Show(mEditEmojicon, mContext);
                        }
                    }
                    break;
                case R.id.imageViewAvatar:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: ChatActivity.onClick : System.currentTimeMillis : " + System.currentTimeMillis());
                    if (!mEditEmojicon.getText().toString().isEmpty()) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: ChatActivity.onClick : mEditEmojicon.getText : " + mEditEmojicon.getText().toString());
                        chatManager.AddComment(mEditEmojicon.getText().toString());
                        mEditEmojicon.setText("");
                    }
                    break;

            }
        }
    }

    /**
     * Method for getting current city phone number
     *
     * @return - current city phone
     */
    public String getCurrentCityPhone() {
        String last_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: ChatActivity.getCurrentCityPhone : last_city_id : " + last_city_id);

        String phoneNumber = "";
        int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");
        // get city by id
        City cityItem = cityDBManager.getCityByID(last_city_id, languageID);
        if (cityItem != null)
            phoneNumber = cityItem.getPhone();
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: ChatActivity.getCurrentCityPhone : phoneNumber : " + phoneNumber);

        return phoneNumber;

    } // end method getCurrentCityPhone

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
        if (mEditEmojicon.getText().toString().trim().equals("")) {
            // deactivating send message button
            emojiEnabled = false;
        }

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);
        // activating send icon
        emojiEnabled = true;

    }

    @Override
    public void onFailure(Throwable error, int requestType) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: ChatActivity.onFailure : requestType : " + requestType + " : error : " + error.getMessage());
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: ChatActivity.onSuccess : requestType : " + requestType + " : response : " + response);
        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_COMMENTS:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_COMMENTS")) {
                        if (response instanceof ChatGetCommentsData) {

                            ChatGetCommentsData chatData = (ChatGetCommentsData) response;
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatActivity.onSuccess : REQUEST_GET_COMMENTS : chatData : " + chatData);
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatActivity.onSuccess : REQUEST_GET_COMMENTS : commentList : " + chatData.getComments());

                            if (chatData.getIsLast() == 1) {
                                isLast = true;

                            }
                            List<Comment> currentCommentList = chatData.getComments();

                            if (isSwipe) {
                                isSwipe = false;
                                if (currentCommentList != null && !currentCommentList.isEmpty()) {
                                    List<Comment> lastCommentList = new ArrayList<>();
                                    if (!chatList.isEmpty()) {
                                        chatList.remove(0);
                                        lastCommentList.addAll(chatList);
                                    }
                                    chatList.clear();
                                    lastMessageId = currentCommentList.get(0).getId();
                                    chatList.add(createFirstItem());
                                    chatList.addAll(currentCommentList);
                                    if (!lastCommentList.isEmpty())
                                        chatList.addAll(lastCommentList);
                                    adapter.notifyDataSetChanged();
                                }

                                if (currentCommentList != null && !currentCommentList.isEmpty())
                                    layoutManager.scrollToPosition(currentCommentList.size());
                            } else {
                                if (currentCommentList != null && !currentCommentList.isEmpty()) {
                                    chatList.clear();
                                    lastMessageId = currentCommentList.get(0).getId();
                                    chatList.add(createFirstItem());
                                    chatList.addAll(currentCommentList);
                                    adapter.notifyDataSetChanged();
                                }
                                layoutManager.scrollToPosition(chatList.size() - 1);
                            }
                            if (mSwipeRefreshLayout != null) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_ADD_COMMENT:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_ADD_COMMENT")) {
                        if (response instanceof ChatAddCommentData) {

                            ChatAddCommentData orderCommentData = (ChatAddCommentData) response;
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_ADD_COMMENT orderCommentData : " + orderCommentData);
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_ADD_COMMENT result : " + orderCommentData.getResult());

                            if (orderCommentData.getResult() == 1) {
                                chatManager.GetComments(0);
                            } else {
                                Toast.makeText(ChatActivity.this, R.string.alert_add_order_comment, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public void onRefresh() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onRefresh : isLast : " + isLast);

        isSwipe = true;
        if (!isLast) {
            chatManager.GetComments(lastMessageId);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
