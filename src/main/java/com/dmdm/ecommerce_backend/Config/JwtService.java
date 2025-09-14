package com.dmdm.ecommerce_backend.Config;

import com.dmdm.ecommerce_backend.Entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // Generate signing key from the secret
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate token using the User entity
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())                         // Set the user identity
                .claim("roles", user.getRole())                         // Optional claims
                .setIssuedAt(new Date())                                // Token issue time
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256)       // Signature
                .compact();
    }
    // Extract username from token (we stored it in the 'subject' field)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generic method to extract any claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Validate token against user details
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);

        // Get the value of the "roles" claim
        // The return type is Object because claims are stored as a generic map
        Object rolesObject = claims.get("roles");

        if (rolesObject instanceof Collection<?>) {
            return ((Collection<?>) rolesObject).stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of(); // empty list if roles claim not present
    }


    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Get expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract full claims from token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
