/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        * WHY or for WHAT? (¿Por qué o para qué?)

     */
    //Generales
    private static final String WHAT = "Qué";
    private static final String HOW = "Cómo";
    private static final String WHEN = "Cuándo";
    private static final String WHO = "Quién";
    private static final String WHERE = "Dónde";
    private static final String WHY = "Por qué";
    private static final String ESTA = "ESTA";
    
    // ITCM keywords
    private static final String ITCM[] = {"ITCM", "TEC", "salon", "ISC", "IQ",
        "IGE", "MEC", "PET", "AMB", "TICS", "FF", "EE", "T2"};
    // entertaiment keywords
    public static final String ENTERTAINMENT_WORDS[] = {"AQUARIUM ", "ART_GALLERY ",
        "BAR ", "CAFE ", "CASINO ", "MUSEUM ", "MOVIE_THEATER ", "NIGHTCLUB ", 
        "PARK ", "SPA ", "ZOO ", "POINT_OF_INTEREST"};
    public static final String OPTIONS_ENTERTAINMENT[] = {"ACUARIO", "GALERÍAS",
        "BAR ", "CAFETERÍA", "CASINO", "MUSEO", "CINE", "NightClub", "PARQUE", 
        "SPA", "ZOOLOGICO", "PDI"};
    //office keywords
    public static final String OFICINAS_WORDS[] = {"COMPUTING_CENTER", "TEC_MANAG",
        "RR_HH", "STUNDENT_SERVICES", "SCHOOL_SERVICES", "PLANNING",
        "COMUNICATION_Y_DIFFUSION", "FINANCIAL_RESOURCES", "ADMIN_SERV_SUBADMIN", "ACADEMIC_SUBDIREC",
        "ACADEMIC_DIRECTION", "BOARDROOM", "SPORTS_COORD", "STUDENT_COUNCIL", "DIVISION_OF_STUDIES",
        "BASIC_SCIENCE_DEPT"};
    public static final String OPTIONS_OFICINAS[] = {"CENTRO_COMPUTO", "GESTION_TEC_VINC",
        "RECURSOS_HUMANOS", "SERVICIOS_ESTUDIANTILES", "SERVICIOS_ESCOLARES", "PLANEACION",
        "COMUNICACION_Y_DIFUSION", "RECURSOS_FINANCIEROS", "SUBDIRECCION_SERV_ADM", "SUBDIRECCION_ACADEMICA",
        "DIRECCION", "SALA_JUNTAS", "COORDINACION_DEPORTIVA", "CONSEJO_ESTUDIANTIL", "DIVISION_DE_ESTUDIOS",
        "DPTO_CIENCIAS_BASICAS"};
    // banks keywords
    public static final String BANKS_WORDS[] = {"BANKS ", "ATM ", "BANAMEX ", 
        "BANCOMER ", "HSBC ", "BANCO AZTECA ", "SANTANDER ", "BANREGIO ", 
        "BANJERCITO ", "BANORTE "};
    public static final String BANKS_SHOW_OPEN_OPTION[] = {"ABIERTOS"};
    
    public static final String NOTHING = "Nothing to do";

    private final Collator collator;

    private String text;

    /*
        Types response
     */
    public static final String TYPE_LOCATE = "LOCATE#";
    public static final String TYPE_LOCATE_ITCM = "LOCATE_ITCM#";
    public static final String TYPE_ENTERTAINMENT = "ENTERTAINMENT#";
    public static final String TYPE_TAG_ENTERTAINMENT = "TYPE_ENTERTAINMENT#";
    public static final String TYPE_OFICINAS = "OFICINAS#";
    public static final String TYPE_TAG_OFICINAS = "TYPE_OFICINAS#";

    public MessageAnalyzer(String msg) {
        collator = Collator.getInstance();
        collator.setStrength(Collator.NO_DECOMPOSITION);
        ArrayList<String> tokens = cleanText(msg);
        this.text = replaceWords(tokens);
    }

    public String[] getAction() {
        String[] action = new String[2];
        String response = "";
        int locating = isLocating();
        int entertainment;
        String entertainmentPlaces;
        int oficinas;
        String oficinasPlaces;

        if (locating != -1) {
            String aboutTec = isAboutTec();
            if (!aboutTec.isEmpty()) {
                action[0] = TYPE_LOCATE_ITCM;
                action[1] = aboutTec;
                return action;
            } else {
                action[0] = TYPE_LOCATE;
                action[1] = text.substring(locating);
                return action;
            }
        } else if ((entertainment = isEntertainment()) != -1) {
            action[0] = TYPE_ENTERTAINMENT;
            action[1] = text.substring(entertainment);
            return action;
        } else if (!(entertainmentPlaces = isEntertainmentPlaces()).isEmpty()) {
            action[0] = TYPE_TAG_ENTERTAINMENT;
            action[1] = entertainmentPlaces;
            return action;
        } else if((oficinas = isOficinas()) != -1){
            action[0] = TYPE_OFICINAS;
            action[1] = text.substring(oficinas);
            return action;
        } else if (!(oficinasPlaces = isAboutOficinas()).isEmpty()) {
            action[0] = TYPE_TAG_OFICINAS;
            action[1] = oficinasPlaces;
            return action;
        } else {
            action[0] = NOTHING;
            action[1] = NOTHING;
            return action;
        }
    }

    private int isLocating() {

        if (text.matches(".*Dónde.+")) {
            return text.indexOf(WHERE);
        } else if (text.matches(".*Cómo llego a?.+")) {
            return text.indexOf(HOW);
        } else {
            return -1;
        }
    }
    
    private int isEntertainment() {

        if (text.matches(".*Lugares de entretenimiento.*")) 
            return text.indexOf("Lugares");
        else 
            return -1;   
    }
    
    private int isOficinas(){
        if (text.matches(".*Oficinas.*")){
            return text.indexOf("Oficinas");
        } else {
            return -1;
        }
    }

    private String isEntertainmentPlaces() {
        StringBuilder stringContains = new StringBuilder();
        String[] textSplit = text.split(" ");

        for (String tokenText : textSplit) {

            for (String _places : ENTERTAINMENT_WORDS) {
                if (collator.compare(tokenText.trim(), _places.trim()) == 0 || tokenText.trim().equalsIgnoreCase(_places.trim())) {
                    stringContains.append(tokenText).append(" ");
                }
            }
            if (tokenText.equalsIgnoreCase("cercano") || tokenText.equalsIgnoreCase("cercanos")) {
                stringContains.append("cercano").append(" ");
            }
        }
        return stringContains.toString();
    }
    
    private String isAboutOficinas(){
        StringBuilder stringContains = new StringBuilder();
        String[] textSplit = text.split(" ");
        for (String tokenText : textSplit) {
            for (String _oficinas : OFICINAS_WORDS) {
                if (tokenText.contains(_oficinas)) {
                    stringContains.append(tokenText).append(" ");
                }
            }
        }
        return stringContains.toString();
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
            if (collator.compare(token, WHAT) == 0) {
                builder.append(WHAT).append(" ");
            } else if (collator.compare(token, HOW) == 0) {
                builder.append(HOW).append(" ");
            } else if (collator.compare(token, WHEN) == 0) {
                builder.append(WHEN).append(" ");
            } else if (collator.compare(token, WHO) == 0) {
                builder.append(WHO).append(" ");
            } else if (collator.compare(token, WHERE) == 0) {
                builder.append(WHERE).append(" ");
            } else if (collator.compare(token, WHY) == 0) {
                builder.append(WHY).append(" ");
            } else if (collator.compare(token, ESTA) == 0) {
                builder.append(ESTA).append(" ");
            } else {
                boolean almostOne = false;
                
                for (String _itcm : ITCM) {
                    if (collator.compare(token, _itcm) == 0) {
                        builder.append(_itcm).append(" ");
                        almostOne = true;
                    }
                }
                
                for (int i = 0; i < OPTIONS_ENTERTAINMENT.length; i++) {
                    
                    String _places = OPTIONS_ENTERTAINMENT[i];
                    if (collator.compare(token, _places) == 0 
                        || token.trim().equalsIgnoreCase(_places.trim())) 
                    {
                        builder.append(ENTERTAINMENT_WORDS[i]).append(" ");
                        almostOne = true;
                    }
                }
                
                for (String _oficinas : OPTIONS_OFICINAS) {
                    if (collator.compare(token, _oficinas) == 0) {
                        builder.append(_oficinas).append(" ");
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
        MessageAnalyzer messageHandler = new MessageAnalyzer("donde esta el centro de computo?");
        System.out.println(messageHandler.getAction());
    }
}
