package com.rance.chatui.util;


public class Constants {
    public static final String TAG = "rance";
    /** 0x001-接受消息  0x002-发送消息**/
    public static final int CHAT_ITEM_TYPE_LEFT = 0x001;
    public static final int CHAT_ITEM_TYPE_RIGHT = 0x002;
    public static final int PRIVATE_CHAT_REQUEST = 0x003;
    public static final int BROAD_PEER = 0x004;
    public static final int STATE_CHANGE = 0x005;
    public static final int ENTER_CHATROOM = 0x006;
    /** 0x003-发送中  0x004-发送失败  0x005-发送成功**/
    public static final int CHAT_ITEM_SENDING = 0x007;
    public static final int CHAT_ITEM_SEND_ERROR = 0x008;
    public static final int CHAT_ITEM_SEND_SUCCESS = 0x009;

    public static final int ON_LINE = 1;
    public static final int OFF_LINE = 2;
    public static final int BUSY = 3;

    public static final int MESSAGE_READ = 0x400 + 1;

    public static final int SERVER = 100;
    public static final int CLIENT = 200;

}
