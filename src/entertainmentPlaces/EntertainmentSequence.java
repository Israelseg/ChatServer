/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entertainmentPlaces;

import chatserver.ChatServerThread;

/**
 *
 * @author hp
 */
public class EntertainmentSequence extends Thread {

    private final String order;
    private final String[] orderSplit;
    private final int method;
    private final String subtype_consult = "Entertainment_Dialog";
    /*
        Dialog
     */
    private final String dialog_1 = "Escriba la opción que requiera para buscar:";
    /*
        Opciones de respuesta
     */
    private ChatServerThread userThread;
    private static final String ENTERTAINMENT_WORDS[] = {"ACUARIO", "GALERÍA DE ARTE",
        "BAR ", "SALÓN DE BELLEZA", "CAFETERÍA", "CASINO", "MUSEO", "CINE",
        "CLUB NOCTURNO", "PARQUE", "SPA", "ZOOLOGICO", "PUNTO DE INTERÉS", "TODOS"};

    public EntertainmentSequence(String order,ChatServerThread userThread) {
        this.order = order;
        orderSplit = order.split("#");
        this.userThread=userThread;
        /*
            Pasa directo si contiene tipo, caso contrario inicia mini dialogo
       
         */
        if (orderSplit[0].contains("Type")) {
            method = 1;
        } else {
            method = 2;
        }

    }

    @Override
    public void run() {
        boolean flag=true;
        while (flag) {
            if (method == 1) {
                /*
                inicia peticion a google places
                 */
                flag=false;
            } else {

            }
        }
    }

}
