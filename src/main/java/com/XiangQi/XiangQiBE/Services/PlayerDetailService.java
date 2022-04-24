package com.XiangQi.XiangQiBE.Services;

import com.XiangQi.XiangQiBE.Repositories.PlayerRepo;
import com.XiangQi.XiangQiBE.Security.PlayerDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlayerDetailService implements UserDetailsService {
    @Autowired
    private PlayerRepo _playerRepo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        var players = _playerRepo.findByUsername(usernameOrEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new PlayerDetail(players);
    }
}
