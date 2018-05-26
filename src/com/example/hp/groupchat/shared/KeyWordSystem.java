/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.hp.groupchat.shared;

/**
 * @author hp
 */
public class KeyWordSystem {

    /*
        Status connection
     */
    public static final int CONNECTED = 0;//"_Server_Connected#";
    public static final int CLOSE_CONNECTION = -1;// "_Close_Connection#";


    /*
        Messages types
     */
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_QUERY = 2;//"Query#";
    public static final int TYPE_QUERY_RESULT = 3;//"Qu"
    public static final int TYPE_IMG = 4;//"_File_Transfer#";
    public static final int TYPE_JSON = 5;
    public static final int TYPE_SET_ID = 6;
    public static final int TYPE_LOCATION=7;
    public static final int TYPE_MAP=8;

    /*
        Command
     */
    public static final String COMMAND_LOCATION = "LOCATION";

    /*
        Generic text
     */
    public static final String BOT_NAME = "Bot";
    public static final String MSG_DISCONNECTED = " has disconnected";
    public static final String MSG_CONNECTED = " has connected";

}
