package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toSet;


@Entity
public class GamePlayer {

    //PROPIEDADES

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();

    //CONSTRUCTORES

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.fecha = LocalDateTime.now();
    }

    //MÉTODOS



    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }


    public void addSalvoes(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpid", id);
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        if (this.getScore().isPresent()) {
            dto.put("score", this.getScore().get().getPoints());
        } else {
            dto.put("score", null);
        }
        return dto;
    }

    public Map<String, Object> playerIdDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();  //DTO
        dto.put("id", player.getId());
        dto.put("salvoes", salvoes.stream()
                .map(Salvo::salvoDTO)
                .collect(toSet()));
        return dto;
    }

    public Map<String, Object> game_view() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("created", fecha);
        dto.put("gamePlayers", game.players.stream()
                .map(GamePlayer::toDTO)
                .collect(toSet()));
        dto.put("ships", ships.stream()
                .map(Ship::shipDTO)
                .collect(toSet()));
        dto.put("playerSalvoes", game.players.stream()
                .map(GamePlayer::playerIdDTO)
                .collect(toSet()));


        return dto;
    }

    //GETTERS Y SETTERS

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Optional<Score> getScore(){
        return this.player.getGameScores(this.game);
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }


}