package com.XiangQi.XiangQiBE.Security;

import java.util.Collection;
import java.util.Date;
import com.XiangQi.XiangQiBE.Models.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerDetail implements UserDetails {
    private String username;
    private String email;
    private String id;
    private Date logoutDate;

    @JsonIgnore
    private String password;

    @Autowired
    public PlayerDetail(Player player) {
        username = player.getUsername();
        email = player.getEmail();
        id = player.getId();
        password = player.getPassword();
        logoutDate = player.getLogoutDate();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
