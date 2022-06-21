package com.XiangQi.XiangQiBE.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.XiangQi.XiangQiBE.Models.Match;
import com.XiangQi.XiangQiBE.Repositories.MatchesRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MatchService {
    MatchesRepo repo;

    public List<Match> getPlayerMatches(String username) {
        var matches = repo.findByPlayer(username);
        ArrayList<Match> top20 = new ArrayList<>();
        for (int i = matches.size() - 1; i >=  matches.size() - 20 && i >= 0; i--) {
            top20.add(matches.get(i));
        }

        return top20;
    }
}
