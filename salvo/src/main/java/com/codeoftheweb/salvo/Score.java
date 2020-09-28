package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")  //propiedades
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private double points;

    private LocalDateTime finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;


    //constructores

    public Score(){}

    public Score(Player player, Game game, double points){
        this.player = player;
        this.game = game;
        this.points = points;
        this.finishDate = LocalDateTime.now();
    }
    //metodos


    //getters y setters

    public long getId() {
        return id;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public double getPoints() {
        return points;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }
}
