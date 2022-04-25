package com.XiangQi.XiangQiBE.Models;

import javax.validation.constraints.NotBlank;
import com.mongodb.lang.Nullable;
import org.hibernate.validator.internal.util.stereotypes.Immutable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Match {
    public enum State {
        WAITING,
        PLAYING,
        FINISHED
    }

    @Id
    @Immutable
    private String id;

    @Indexed(unique = true)
    @Setter
    private String user1;
    @Indexed(unique = true)
    @Setter
    private String user2;

    // Set by server
    @Nullable
    @Setter
    private String blackPlayer;
    @Nullable
    @Setter
    private String whitePlayer;

    @NotBlank
    @Setter
    private String board;
    @NotBlank
    @Setter
    private String[] moves;

    @Nullable
    @Setter
    private String winner;
    @NotBlank
    @Setter
    private State state;
}
