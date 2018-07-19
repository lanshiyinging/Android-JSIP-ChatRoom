package com.rance.chatui.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.rance.chatui.adapter.ChatAdapter;
import com.rance.chatui.adapter.CommonFragmentPagerAdapter;
import com.rance.chatui.enity.FullImageInfo;
import com.rance.chatui.enity.MessageInfo;
import com.rance.chatui.enity.PeerInfo;
import com.rance.chatui.ui.fragment.ChatEmotionFragment;
import com.rance.chatui.ui.fragment.ChatFunctionFragment;
import com.rance.chatui.ui.fragment.PeerClickListener;
import com.rance.chatui.ui.fragment.PeerListFragment;
import com.rance.chatui.util.Constants;
import com.rance.chatui.util.GlobalOnItemClickManagerUtils;
import com.rance.chatui.util.MediaManager;
import com.rance.chatui.widget.EmotionInputDetector;
import com.rance.chatui.widget.NoScrollViewPager;
import com.rance.chatui.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bupt.jsip_demo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;


public class ChatActivity extends AppCompatActivity implements Handler.Callback,
        PeerClickListener, MessageTarget{

    @Bind(R.id.chat_list)
    EasyRecyclerView chatList;
    @Bind(R.id.emotion_voice)
    ImageView emotionVoice;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.voice_text)
    TextView voiceText;
    @Bind(R.id.emotion_button)
    ImageView emotionButton;
    @Bind(R.id.emotion_add)
    ImageView emotionAdd;
    @Bind(R.id.emotion_send)
    StateButton emotionSend;
    @Bind(R.id.viewpager)
    NoScrollViewPager viewpager;
    @Bind(R.id.emotion_layout)
    RelativeLayout emotionLayout;


    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;
    private PeerListFragment peerListFragment;
    ArrayList<PeerInfo> peerInfoArrayList;

    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    //
    private Handler handler = new Handler(this);
    private SipProfile sipProfile;
    private String remoteIp;
    private String remotePort;
    private String toSip;
    private String localUser;
    private String localPort;
    private String localSip;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_ui);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);


        DeviceImpl.getInstance().setHandler(this.getHandler());
        sipProfile = DeviceImpl.getInstance().getSipProfile();

        Intent intent = getIntent();
        toSip = intent.getStringExtra("sip");
        localUser = intent.getStringExtra("local_user");
        remoteIp = intent.getStringExtra("remote_ip");
        remotePort = intent.getStringExtra("remote_port");
        localPort = intent.getStringExtra("local_port");
        localSip = "sip:"+ sipProfile.getSipUserName() + "@" + sipProfile.getLocalEndpoint();

        MessageInfo notifyServer = new MessageInfo();
        notifyServer.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        notifyServer.setType(Constants.ENTER_CHATROOM);
        notifyServer.setFrom(new PeerInfo(localSip, Constants.ON_LINE));
        notifyServer.setContent(localUser + "进入聊天室");
        DeviceImpl.getInstance().SendMessage(toSip, gson.toJson(notifyServer));

        initWidget();

    }

    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        LoadData();
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(ChatActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(view.getWidth());
            fullImageInfo.setHeight(view.getHeight());
            fullImageInfo.setImageUrl(messageInfos.get(position).getImageUrl());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(ChatActivity.this, FullImageActivity.class));
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (messageInfos.get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });
        }
    };

    /**
     * 构造聊天数据
     */
    private void LoadData() {

        messageInfos = new ArrayList<>();
        String username = sipProfile.getSipUserName();
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setContent(username + "你好，欢迎进入Jain-Sip C位出道聊天室");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
        messageInfos.add(messageInfo);
        chatAdapter.addAll(messageInfos);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageInfo messageInfo) {
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        if((messageInfo.getContent()!= null) && messageInfo.getContent().equals("quit")){
            messageInfo.setType(Constants.STATE_CHANGE);
            messageInfo.setFrom(new PeerInfo(localSip, Constants.OFF_LINE));
            messageInfo.setContent(localUser + "离开聊天室");
            DeviceImpl.getInstance().SendMessage(toSip, gson.toJson(messageInfo));
        }else {
            messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
            messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
            messageInfo.setFrom(new PeerInfo(localSip, Constants.ON_LINE));
            messageInfos.add(messageInfo);
            chatAdapter.add(messageInfo);
            chatList.scrollToPosition(chatAdapter.getCount() - 1);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                    chatAdapter.notifyDataSetChanged();
                }
            }, 10);
            String sendMessage = gson.toJson(messageInfo);
            DeviceImpl.getInstance().SendMessage(toSip, sendMessage);
        }

    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
        View rootView = findViewById(R.id.container_chat);
        rootView.setVisibility(View.VISIBLE);
        System.out.println(getSupportFragmentManager().beginTransaction().isEmpty());
        /*
        MessageInfo msg = new MessageInfo();
        msg.setType(Constants.STATE_CHANGE);
        msg.setFrom(new PeerInfo(localSip, Constants.OFF_LINE));
        msg.setContent(localUser + "离开聊天室");
        DeviceImpl.getInstance().SendMessage(toSip, gson.toJson(msg));
        */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_ui_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    /*点击显示好友列表*/
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_p:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                View rootView = findViewById(R.id.container_chat);
                rootView.setVisibility(View.GONE);
                peerListFragment = new PeerListFragment();
                peerListFragment.setPeers(peerInfoArrayList);

                fragmentTransaction.add(R.id.test, peerListFragment, "peers");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_READ:
                String readMessage = (String) msg.obj;
                String received = readMessage.split("\\|")[1];
                MessageInfo receivedMessage = gson.fromJson(received, MessageInfo.class);
                if(receivedMessage.getType() == Constants.PRIVATE_CHAT_REQUEST){
                    PeerInfo peer = receivedMessage.getFrom();
                    Intent intent = new Intent(this, ChatActivity_private.class);
                    intent.putExtra("peer", gson.toJson(peer));
                    intent.putExtra("user_type", Constants.CLIENT);
                    startActivity(intent);
                }else if (receivedMessage.getType() == Constants.BROAD_PEER){
                    peerInfoArrayList = gson.fromJson(receivedMessage.getContent(), new TypeToken<ArrayList<PeerInfo>>(){}.getType());
                }else{
                    MessageInfo showReceivedMessage = receivedMessage;
                    showReceivedMessage.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    showReceivedMessage.setContent("From " + receivedMessage.getFrom().getSipAddress() +  "\n" + receivedMessage.getContent());
                    messageInfos.add(receivedMessage);
                    chatAdapter.add(receivedMessage);
                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                }
        }
        return true;
    }



    @Override
    public void chatP2p(PeerInfo peerInfo) {
        if(peerInfo.getStatus() == Constants.ON_LINE) {
            Intent intent = new Intent(this, ChatActivity_private.class);
            intent.putExtra("peer", gson.toJson(peerInfo));
            intent.putExtra("user_type", Constants.SERVER);
            startActivity(intent);
        }else{
            Toast.makeText(this, peerInfo.getUsername() + "is offline", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Handler getHandler() {
        return handler;
    }


}
