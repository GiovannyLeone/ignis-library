package com.papirotech.biblioteca.config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
@Service
public class JwtService {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration}") private long expiration;
    private SecretKey getKey() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
    public String gerarToken(UserDetails u) { return gerarToken(new HashMap<>(), u); }
    public String gerarToken(Map<String,Object> extra, UserDetails u) {
        return Jwts.builder().claims(extra).subject(u.getUsername())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getKey()).compact();
    }
    public String extrairUsername(String token) { return extrairClaim(token, Claims::getSubject); }
    public boolean isTokenValido(String token, UserDetails u) {
        return extrairUsername(token).equals(u.getUsername()) && !extrairExpiracao(token).before(new Date());
    }
    private Date extrairExpiracao(String t) { return extrairClaim(t, Claims::getExpiration); }
    public <T> T extrairClaim(String token, Function<Claims,T> fn) {
        return fn.apply(Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload());
    }
}
