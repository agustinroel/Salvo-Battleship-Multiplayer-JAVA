package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String shipType;        //propiedades

    @ElementCollection
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_player_id")
    private GamePlayer gamePlayer;

    public Ship() { }   //constructor

    public Ship (String shipType, List<String> locations) {  //constructor
        this.shipType = shipType;
        this.locations = locations;
    }

    //metodos

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", shipType);
        dto.put("location", locations);
        return dto;
    }

    //getter y setters

    public GamePlayer getGamePlayer() {
       return this.gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public long getId() { //getter
        return id;
    }

    public void setId(long id) { //setter
        this.id = id;
    }

    public String getShipType() { //getter
        return shipType;
    }

    public void setShipType(String shipType) { //setter
        this.shipType = shipType;
    }

    public List<String> getLocations() {
        return locations;
    }
}


