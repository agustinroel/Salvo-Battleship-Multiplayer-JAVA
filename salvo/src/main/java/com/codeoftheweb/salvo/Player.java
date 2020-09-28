package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> games;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> gameScores;

    public Player() {  //constructor
    }

    public Player(String user, String password) {
        userName = user;
        this.password = password;
    }  //constructor

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();  //DTO
        dto.put("id", this.id);
        dto.put("email", this.userName);
        return dto;
    }

    public long getId() {
        return id;
    }

    public Optional<Score> getGameScores(Game game) {
        return gameScores.stream().filter(p -> p.getGame().getId() == game.getId()).findFirst();
    }

    public String getUserName() {
        return userName;
    } //getter

    public void setUserName(String userName) {
        this.userName = userName;
    } //setter

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public List<Game> getGames() {
        return games.stream().map(sub -> sub.getGame()).collect(toList());
    }
}
