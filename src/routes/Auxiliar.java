/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routes;

/**
 *
 * @author DeuSe
 */
public class Auxiliar {

    private final String ruta;
    private final String latitud;
    private final String longitud;
    private final double distancia;

    public Auxiliar(String ruta, String array[], Double distancia) {
        this.ruta = ruta;
        this.latitud = array[1];
        this.longitud = array[2];
        this.distancia = distancia;
    }

    public String getRuta() {
        return this.ruta;
    }

    public String getLatitud() {
        return this.latitud;
    }

    public String getLongitud() {
        return this.longitud;
    }

    public double getDistancia() {
        return this.distancia;
    }

}
