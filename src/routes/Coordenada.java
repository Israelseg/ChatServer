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
public class Coordenada {

    private double latitud;
    private double longitud;

    public Coordenada(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double calcularDistancia(Coordenada parametros) {
        double A = this.latitud - parametros.getLatitud();
        double B = this.longitud - parametros.getLongitud();
        double A2 = Math.pow(A, 2);
        double B2 = Math.pow(B, 2);
        double C = Math.sqrt(A2 + B2);
        return C;
    }

    public double getLatitud() {
        return this.latitud;
    }

    public double getLongitud() {
        return this.longitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

}
