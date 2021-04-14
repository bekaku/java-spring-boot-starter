package io.beka.service.impl;

import io.beka.model.entity.ApiClient;
import io.beka.service.JwtService;
import io.beka.util.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class DefaultJwtService implements JwtService {

    private String secret;
    private int sessionTime;

    Logger logger = LoggerFactory.getLogger(DefaultJwtService.class);

    @Autowired
    public DefaultJwtService(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.sessionTime}") int sessionTime) {
        this.secret = secret;
        this.sessionTime = sessionTime;
    }

    @Override
    public String toToken(String token, ApiClient apiClient) {
        return Jwts.builder()
                .setSubject(token)
                .setIssuedAt(new Date())
                .setExpiration(expireTimeFromNow())
                .signWith(SignatureAlgorithm.HS512, apiClient.getApiToken())
                .compact();
    }

    @Override
    public Optional<String> getSubFromToken(String token, ApiClient apiClient) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(apiClient.getApiToken()).parseClaimsJws(token);

            long expiredTime = claimsJws.getBody().getExpiration().getTime();
            long nowTime = System.currentTimeMillis();
            if(nowTime>expiredTime){
                return Optional.empty();
            }
            return Optional.ofNullable(claimsJws.getBody().getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Date expireTimeFromNow() {
        //        return new Date(System.currentTimeMillis() + sessionTime * 1000);
        return new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime * 1000L : DateUtil.MILLS_IN_DAY));
    }

    @Override
    public Date expireTimeOneDay() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_DAY);
    }

    @Override
    public Date expireTimeOneWeek() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_WEEK);
    }

    @Override
    public Date expireTimeOneMonth() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_MONTH);
    }

    @Override
    public Date ExpireTimeOneYear() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_YEAR);
    }

    @Override
    public int expireMillisec() {
        return sessionTime * 1000;
    }
}
