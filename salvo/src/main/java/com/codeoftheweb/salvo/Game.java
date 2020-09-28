package com.codeoftheweb.salvo;


import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toSet;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime fecha;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> players;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    Set<Score> playerScores;

    public Game() {
        this.fecha = LocalDateTime.now();
    }

    public Game(LocalDateTime fecha) {
        this.fecha = fecha;

    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", id);
        dto.put("created", fecha);
        dto.put("gamePlayers", players.stream()
                .map(GamePlayer::toDTO)
                .collect(toSet()));

        return dto;
    }

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public Set<GamePlayer> getPlayers() {
        return players;
    }
}
