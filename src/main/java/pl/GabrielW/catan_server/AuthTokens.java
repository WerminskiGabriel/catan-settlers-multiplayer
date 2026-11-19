package pl.GabrielW.catan_server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public final class AuthTokens {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom( );

    public static String generateToken( ) {
        byte[] bytes = new byte[24];
        SECURE_RANDOM.nextBytes( bytes );
        return Base64.getUrlEncoder( ).withoutPadding( ).encodeToString( bytes );
    }

    public static String hashToken( String rawToken ) {
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            byte[] hashBytes = digest.digest( rawToken.getBytes( StandardCharsets.UTF_8 ) );
            return HexFormat.of( ).formatHex( hashBytes );
        }
        catch( NoSuchAlgorithmException e ) {
            throw new IllegalStateException( "SHA-256 not available" , e );
        }
    }

    public static boolean verifyToken( String rawToken , String storedHashedToken ) {
        if( rawToken == null || storedHashedToken == null ) {
            return false;
        }
        String computedHash = hashToken( rawToken );
        return MessageDigest.isEqual( computedHash.getBytes( StandardCharsets.UTF_8 ) ,
                storedHashedToken.getBytes( StandardCharsets.UTF_8 ) );
    }
}