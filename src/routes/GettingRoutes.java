/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routes;

import chatserver.ChatServerThread;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeuSe
 */
public class GettingRoutes extends Thread {

    private final ChatServerThread user;
    private final Coordenada tec;

    private Message response;

    private final double radioTec = 0.00342;
    private final double cercaTec = 0.00852;

    public GettingRoutes(ChatServerThread user) {
        this.user = user;
        this.tec = new Coordenada(22.256172, -97.847386);
    }

    @Override
    public void run() {

        if (user.getLocation() == null) {

            response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_TEXT, "Lo sentimos, no pudimos obtener tu ubicación");

        } else {

            String[] coordenadas = user.getLocation().split(",");

            double latitud = Double.parseDouble(coordenadas[0]);
            double longitud = Double.parseDouble(coordenadas[1]);

            Coordenada usuario = new Coordenada(latitud, longitud);

            if (dentroTec(usuario)) {

                response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_TEXT, "Ya estás dentro del tecnológico");

            } else if (cercaTec(usuario)) {

                response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_RUTA, "");

            } else {

                Conectar objeto = new Conectar();
                Connection baseDatos = objeto.getConnection();

                String sql = "SELECT * FROM rutas";
                Statement st;

                try {
                    st = baseDatos.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    ArrayList<String> rutas = new ArrayList();
                    while (rs.next()) {
                        rutas.add(rs.getString(1) + "  " + rs.getString(2));
                    }
                    double minima = 999;
                    String coordenada[] = new String[3];
                    ArrayList<Auxiliar> auxiliar = new ArrayList();
                    for (int i = 0; i < rutas.size(); i++) {
                        sql = "SELECT A.ID_Coordenada, A.Latitud, A.Longitud FROM coordenadas A, paradas B WHERE A.ID_Coordenada = B.ID_Coordenada AND B.ID_Ruta = '" + rutas.get(i).split("  ")[0] + "'";
                        rs = st.executeQuery(sql);
                        while (rs.next()) {
                            Coordenada ruta = new Coordenada(rs.getDouble(2), rs.getDouble(3));
                            double distancia = usuario.calcularDistancia(ruta);
                            if (distancia < minima) {
                                minima = distancia;
                                coordenada[0] = rs.getString(1);
                                coordenada[1] = rs.getString(2);
                                coordenada[2] = rs.getString(3);
                            }
                        }
                        Coordenada paradaCercana = new Coordenada(Double.parseDouble(coordenada[1]), Double.parseDouble(coordenada[2]));
                        double distanciaTotal = minima + paradaCercana.calcularDistancia(tec);
                        auxiliar.add(new Auxiliar(rutas.get(i), coordenada, distanciaTotal));
                        minima = 999;
                    }
                    double menor = auxiliar.get(0).getDistanciaTotal();
                    int resultado = 0;
                    for (int i = 1; i < auxiliar.size(); i++) {
                        double dis = auxiliar.get(i).getDistanciaTotal();
                        if (dis < menor) {
                            menor = dis;
                            resultado = i;
                        }
                    }
                    String texto = auxiliar.get(resultado).getRuta() + "   " + auxiliar.get(resultado).getLatitud() + "," + auxiliar.get(resultado).getLongitud();
                    System.out.println(texto);
                    response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_RUTA, texto);
                } catch (SQLException ex) {
                    Logger.getLogger(GettingRoutes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        try {
            user.send(response);
        } catch (Throwable ex) {
            Logger.getLogger(GettingRoutes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean dentroTec(Coordenada usuario) {
        double distancia = usuario.calcularDistancia(tec);
        return distancia < radioTec;
    }

    private boolean cercaTec(Coordenada usuario) {
        double distancia = usuario.calcularDistancia(tec);
        return distancia < cercaTec;
    }

}
