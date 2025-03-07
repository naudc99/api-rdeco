package es.ndc.api_rdeco.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import es.ndc.api_rdeco.entities.RoleEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtilityService {

    @Value("classpath:jwtKeys/private_key.pem")
    private Resource privateKeyResource;

    @Value("classpath:jwtKeys/public_key.pem")
    private Resource publicKeyResource;

    public String generateJWT(Long userId, RoleEntity role) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        PrivateKey privateKey = loadPrivateKey(privateKeyResource);

        JWSSigner signer = new RSASSASigner(privateKey);
        Date now = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .claim("role", role.getName())
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 14400000))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public JWTClaimsSet parseJWT(String jwt) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ParseException, JOSEException {
        PublicKey publicKey = loadPublicKey(publicKeyResource);
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

        if (!signedJWT.verify(verifier))
            throw new JOSEException("Invalid signature");

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        if (claimsSet.getExpirationTime().before(new Date()))
            extendJWTExpiration(jwt);

        return claimsSet;
    }

    public String extendJWTExpiration(String expiredToken) throws ParseException, JOSEException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        SignedJWT expiredJWT = SignedJWT.parse(expiredToken);
        PublicKey publicKey = loadPublicKey(publicKeyResource);
        if (expiredJWT.verify(new RSASSAVerifier((RSAPublicKey) publicKey))) {
            JWTClaimsSet claims = expiredJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();
            Date now = new Date();
            if (expirationTime != null && expirationTime.before(now)) {
                Date newExpirationTime = new Date(now.getTime() + 14400000);
                JWTClaimsSet newClaims = new JWTClaimsSet.Builder(claims)
                        .expirationTime(newExpirationTime)
                        .build();
                SignedJWT newJWT = new SignedJWT(expiredJWT.getHeader(), newClaims);
                PrivateKey privateKey = loadPrivateKey(privateKeyResource);
                newJWT.sign(new RSASSASigner(privateKey));
                return newJWT.serialize();
            }
        }
        return expiredToken;
    }

    private PrivateKey loadPrivateKey(Resource resource) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        String privateKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodeKey = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodeKey));
    }

    private PublicKey loadPublicKey(Resource resource) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        String publicKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodeKey = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(new X509EncodedKeySpec(decodeKey));
    }
}
