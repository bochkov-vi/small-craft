package com.bochkov.smallcraft.wicket.security;

import com.bochkov.smallcraft.jpa.entity.RemberMeToken;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class PersistentTokenRepositoryImpl implements PersistentTokenRepository {

    @Autowired
    AccountRepository repository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        repository.findById(token.getUsername()).ifPresent(a -> {
            RemberMeToken t = new RemberMeToken();
            t.setTokenValue(token.getTokenValue())
                    .setDate(token.getDate());
            a.getTokens().put(token.getSeries(), t);
            repository.save(a);
        });
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        repository.findByTokenSeries(series).map(a -> a.updateToken(series, tokenValue, lastUsed)).ifPresent(
                a -> repository.save(a)
        );


    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return repository.findByTokenSeries(seriesId).map(account ->
                account.token(seriesId).map(t -> new PersistentRememberMeToken(
                        account.getId(),
                        seriesId,
                        t.getTokenValue(),
                        Date.from(t.getDateTime().atZone(ZoneId.systemDefault()).toInstant()))).orElse(null))
                .orElse(null);
    }

    @Override
    public void removeUserTokens(String username) {
        repository.findById(username).ifPresent(a -> {
            a.setTokens(null);
            repository.save(a);
        });
    }

}
