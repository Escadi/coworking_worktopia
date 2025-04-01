package Clases;

/**
 * Clase Usuarios.
 */
public class Usuarios {
    private Integer id_usuario;
    private String nombre;
    private String email;
    private String contrasenia;
    private String categoria;

    public Usuarios(Integer id_usuario, String nombre, String email, String contrasenia, String categoria) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.categoria = categoria;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "id_usuario=" + id_usuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
