package com.example.demo.service;

import com.example.demo.model.entity.Usuario;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

// =========================================================================
// SERVICIO DE AUTENTICACIÓN DE DOBLE PASO (2FA)
// Encargado de generar claves secretas, URLs de QR y validar códigos TOTP.
// No necesita repositorio porque solo hace cálculos, no guarda datos.
// =========================================================================
@Service
public class TwoFactorAuthService {

    // Instancia de la librería que hace el cálculo matemático de los códigos
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    // =========================================================================
    // VALIDACIÓN DE CÓDIGOS
    // =========================================================================

    /**
     * Verifica si el código de 6 dígitos coincide con la clave secreta del usuario.
     * La librería maneja automáticamente la tolerancia de tiempo (+/- 30 segundos).
     */
    public boolean validarCodigo(Usuario usuario, int codigoIngresado) {
        if (usuario == null || usuario.getSecretKey2fa() == null || usuario.getSecretKey2fa().isEmpty()) {
            return false;
        }
        return gAuth.authorize(usuario.getSecretKey2fa(), codigoIngresado);
    }

    // =========================================================================
    // GENERACIÓN DE CONFIGURACIÓN (para activar el 2FA desde el perfil)
    // =========================================================================

    /**
     * Genera una nueva clave secreta aleatoria para el usuario.
     */
    public String generarClaveSecreta() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    /**
     * Genera la URL otpauth:// que se convierte en código QR para escanear.
     */
    public String generarUrlQR(String nombreApp, String username, String secretKey) {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secretKey).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(nombreApp, username, key);
    }
}