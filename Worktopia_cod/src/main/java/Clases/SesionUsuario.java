package Clases;

/**
 * Clase que guarda la categoria del usuario que ha iniciado sesion.
 */

public class SesionUsuario {

    private static String categoriaUsuario;


    public static void setCategoriaUsuario(String categoria) {
        categoriaUsuario = categoria;
    }

    public static String getCategoriaUsuario() {
        return categoriaUsuario;
    }


}
