/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author hp
 */
public class MessageAnalyzer {

    /*  Five W
        * What? (¿Qué?)
        * How? (¿Cómo?)
        * When? (¿Cuándo?)
        * Who? (¿Quién?)
        * Where? (¿Dónde?)
        * why or for what? (¿Por qué o para qué?)

     */
    private static final String what = "Qué";
    private static final String how = "Cómo";
    private static final String when = "Cuándo";
    private static final String who = "Quién";
    private static final String where = "Dónde";
    private static final String why = "Por qué";
    private static final String esta = "esta";
    private static final String ITCM[] = {"ITCM", "TEC", "salon", "ISC", "IQ",
        "IGE", "MEC", "PET", "AMB", "TICS", "FF", "EE", "T2"};
    public static final String Nothing = "Nothing to do";
    private final Collator collator;

    private String text;

    public MessageAnalyzer(String msg) {
        collator = Collator.getInstance();
        collator.setStrength(Collator.NO_DECOMPOSITION);
        ArrayList<String> tokens = cleanText(msg);
        this.text = replaceWords(tokens);
    }

    public String getAction() {
        String response = "";
        int locating = isLocating();
        System.out.println(text + " " + locating);

        if (locating != -1) {
            response += "Locate ";
            String aboutTec = isAboutTec();
            if (!aboutTec.isEmpty()) {
                response += aboutTec;
            } else {
                response += text.substring(locating);
            }
        }
        return (response.isEmpty()) ? Nothing : response;
    }

    private int isLocating() {

        if (text.matches(".*Dónde.+")) {
            return text.indexOf(where);
        } else if (text.matches(".*Cómo llego a?.+")) {
            return text.indexOf(how);
        } else {
            return -1;
        }

    }

    private String isAboutTec() {
        StringBuilder stringContains = new StringBuilder();
        String[] textSplit = text.split(" ");
        for (String tokenText : textSplit) {
            for (String _itcm : ITCM) {
                if (tokenText.contains(_itcm)) {
                    stringContains.append(tokenText).append(" ");
                }
            }
        }

        return stringContains.toString();
    }

    private boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private ArrayList<String> cleanText(String msg) {
        ArrayList<String> split = new ArrayList<>();
        String replaceAll = msg.replaceAll("\\?", "").replaceAll("¿", "").
                replaceAll(",", " ").replaceAll("-", " ").replaceAll("_", " ");
        String[] split1 = replaceAll.split(" ");

        for (String string : split1) {
            if (string.contains(".")) {
                boolean numeric = isNumeric(string);
                if (!numeric) {
                    String[] split2 = string.split(".");
                    split.addAll(Arrays.asList(split2));
                } else {
                    split.add(string);
                }
            } else {
                split.add(string);
            }
        }
        return split;
    }

    private String replaceWords(ArrayList<String> tokens) {
        StringBuilder builder = new StringBuilder();
        tokens.forEach((token) -> {
            if (collator.compare(token, what) == 0) {
                builder.append(what).append(" ");
            } else if (collator.compare(token, how) == 0) {
                builder.append(how).append(" ");
            } else if (collator.compare(token, when) == 0) {
                builder.append(when).append(" ");
            } else if (collator.compare(token, who) == 0) {
                builder.append(who).append(" ");
            } else if (collator.compare(token, where) == 0) {
                builder.append(where).append(" ");
            } else if (collator.compare(token, why) == 0) {
                builder.append(why).append(" ");
            } else if (collator.compare(token, esta) == 0) {
                builder.append(esta).append(" ");
            } else {
                boolean almostOne = false;
                for (String _itcm : ITCM) {
                    if (collator.compare(token, _itcm) == 0) {
                        builder.append(_itcm).append(" ");
                        almostOne = true;
                    }
                }
                if (!almostOne) {
                    builder.append(token).append(" ");
                }
            }
        });
        return builder.toString();
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static void main(String[] args) {
        MessageAnalyzer messageHandler = new MessageAnalyzer("Hola amigos, donde esta "
                + "el tec de madero?");
        System.out.println(messageHandler.getAction());
    }
}
