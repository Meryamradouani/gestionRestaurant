package org.example.restaurant.manager.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    /**
     * Hash un mot de passe avec algorithme SHA-256 et sel aléatoire
     * @param password Mot de passe en clair
     * @return String au format "sel$hash" encodé en Base64
     * @throws NoSuchAlgorithmException Si l'algorithme SHA-256 n'est pas disponible
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Génération d'un sel aléatoire
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Création du hash avec sel
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());

        // Encodage en Base64
        return Base64.getEncoder().encodeToString(salt) +
                "$" +
                Base64.getEncoder().encodeToString(hashedPassword);
    }

    /**
     * Vérifie si un mot de passe correspond au hash stocké
     * @param password Mot de passe à vérifier
     * @param storedHash Hash stocké au format "sel$hash"
     * @return true si le mot de passe est valide
     */
    public static boolean verifyPassword(String password, String storedHash)
            throws NoSuchAlgorithmException {

        // Extraction du sel et du hash
        String[] parts = storedHash.split("\\$");
        if(parts.length != 2) throw new IllegalArgumentException("Hash invalide");

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] originalHash = Base64.getDecoder().decode(parts[1]);

        // Recréation du hash avec le sel original
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] testHash = md.digest(password.getBytes());

        // Comparaison des hashs
        return MessageDigest.isEqual(originalHash, testHash);
    }
}