/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.hp.groupchat.shared;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author hp
 */
public class ServerUtils {

    public static String dateLog() {
        return new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());

    }

    public static String simpleDate() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }
}
