package com.example.irblaster;

public class Constants {
    public static final int COMMAND_TYPE_RESET = 1;
    public static final int COMMAND_TYPE_OTA = 2;
    public static final int COMMAND_TYPE_REMOTE = 3;

    public static final int REMOTE_TYPE_AC = 1;
    public static final int REMOTE_TYPE_TV = 2;


    public static final int REMOTE_AC_ON = 0;
    public static final int REMOTE_AC_OFF = 1;
    public static final int REMOTE_AC_TEMP_UP = 2;
    public static final int REMOTE_AC_TEMP_DOWN = 3;
    public static final int[] REMOTE_AC_FAN_SPEED = {4, 5, 6, 7};
    public static final int REMOTE_AC_SWING_ON = 8;
    public static final int REMOTE_AC_SWING_OFF = 9;

    public static final String[] AC_FAN_SPEED_TEXT = {"Auto", "Low", "Medium", "High"};

    public static final int REMOTE_TV_ONOFF = 0;
    public static final int REMOTE_TV_CHANNEL_UP = 1;
    public static final int REMOTE_TV_CHANNEL_DOWN = 2;
    public static final int REMOTE_TV_VOLUME_UP = 3;
    public static final int REMOTE_TV_VOLUME_DOWN = 4;
    public static final int REMOTE_TV_MENU = 5;
    public static final int REMOTE_TV_OK = 6;
    public static final int REMOTE_TV_NAVIGATION_UP = 7;
    public static final int REMOTE_TV_NAVIGATION_DOWN = 8;
    public static final int REMOTE_TV_NAVIGATION_LEFT = 9;
    public static final int REMOTE_TV_NAVIGATION_RIGHT = 10;
}
