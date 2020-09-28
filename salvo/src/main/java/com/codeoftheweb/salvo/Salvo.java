package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;                                            //propiedades

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_player_id")
    private GamePlayer gamePlayer;

    private int turnNumber;

    @ElementCollection
    private List<String> locations;

    public Salvo(){} // constructores

    public Salvo(int turnNumber, List<String> locations){
        this.turnNumber = turnNumber;
        this.locations = locations;
    };
    //metodos

    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", turnNumber);
        dto.put("locations", locations);
        return dto;
    }

    //getters y setters


    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public List<String> getLocations() {
        return locations;
    }
}
