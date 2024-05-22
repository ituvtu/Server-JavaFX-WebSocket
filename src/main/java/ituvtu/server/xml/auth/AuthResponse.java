package ituvtu.server.xml.auth;

import jakarta.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("unused")
@XmlRootElement
public class AuthResponse {
    private boolean authenticated;
    private String username; // Add this field

    public AuthResponse() {
    }

    public AuthResponse(boolean authenticated, String username) {
        this.authenticated = authenticated;
        this.username = username;
    }

    @SuppressWarnings("unused")
    public boolean isAuthenticated() {
        return authenticated;
    }

    @SuppressWarnings("unused")
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @SuppressWarnings("unused")
    public String getUsername() {
        return username;
    }

    @SuppressWarnings("unused")
    public void setUsername(String username) {
        this.username = username;
    }
}