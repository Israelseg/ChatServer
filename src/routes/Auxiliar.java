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
    private final double distanciaTotal;

    public Auxiliar(String ruta, String arreglo[], Double distanciaTotal) {
        this.ruta = ruta;
        this.latitud = arreglo[1];
        this.longitud = arreglo[2];
        this.distanciaTotal = distanciaTotal;
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

    public double getDistanciaTotal() {
        return this.distanciaTotal;
    }

}
