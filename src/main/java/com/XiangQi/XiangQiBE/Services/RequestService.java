package com.XiangQi.XiangQiBE.Services;

import org.springframework.stereotype.Service;

import com.XiangQi.XiangQiBE.Components.Mailer;
import com.XiangQi.XiangQiBE.Models.Request;
import com.XiangQi.XiangQiBE.Repositories.PlayerRepo;
import com.XiangQi.XiangQiBE.Repositories.RequestRepo;
import com.XiangQi.XiangQiBE.Services.PlayerService.UsernameNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RequestService {
    RequestRepo repo;
    PlayerRepo playerRepo;
    PlayerService playerService;
    Mailer mailer;

    public class TokenNotFoundException extends Exception {
        TokenNotFoundException(String id) {
            super("Couldn't found token with id " + id);
        }
    }

    public void SendRequest(String username, Request.Type type) throws UsernameNotFoundException {
        // Save request, send request token over to email and tell user to go to that link with email
        Request req = new Request();
        final String email = playerService.get(username).getEmail();

        req.setType(type);
        req.setUsername(username);

        String endpoint = "";
        String message = "";
        String title = "";
        switch(type) {
            case CHANGE_PASSWORD:
                endpoint = "change-password";
                title = "Change password";
                message = "To change password on XiangQi game website please click ";
                break;
            case EMAIL_VERIFY:
                endpoint = "verify-email";
                title = "Verify email";
                message = "To verify your email on XiangQi game website please click ";
                break;
            default:
                break;
        }

        repo.save(req);
        mailer.SendHTMLEmail(email, title + " from XiangQi game", ""
                + " <div style='background: rgb(38, 38, 38); display: grid; text-align: center; border-radius: 10px; font-weight: bold'>"
                + "     <h1 style='color: #32a8a2'>" + title.toUpperCase() + "</h1>"
                + "     <br>"
                + "     <h3 style='color: white'>"
                + "         <span>" + message + "</span>"
                + "         <a style='color: #82fff9' href='http://localhost:3000/" + endpoint + "?token=" + req.getId() + "'>here.</h3>"
                + "     </p>"
                + "     <br>"
                + "     <h3 style='color: white'>If this email isn't intentional then just ignore this.</h3>"
                + " </div>");
    }

    public void ResolveRequest(String token, String params[]) throws TokenNotFoundException {
        Request req = repo.findById(token).orElseThrow(() -> new TokenNotFoundException(token));
        repo.delete(req);
        var result = playerRepo.findByUsername(req.getUsername());
        if (result.isEmpty()) return;
        var player = result.get();

        switch(req.getType()) {
            case CHANGE_PASSWORD:
                try {
                    player = playerService.changePassword(player.getUsername(), params[0]);
                } catch(Exception e) {}
                break;
            case EMAIL_VERIFY:
                player.validate();                
                break;
            default:
                break;
        }

        playerRepo.save(player);
    }
}
