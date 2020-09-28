package com.codeoftheweb.salvo;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/games")
    public ResponseEntity<Object> createGame(Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>("NO esta autorizado", HttpStatus.UNAUTHORIZED);
        }

        Player  player  = playerRepository.findByUserName(authentication.getName());

        if(player ==  null){
            return new ResponseEntity<>("NO esta autorizado", HttpStatus.UNAUTHORIZED);
        }

        Game  game  = gameRepo.save(new Game());

        GamePlayer  gamePlayer  = gamePlayerRepo.save(new GamePlayer(game,player));

        return new ResponseEntity<>(makeMap("gpid",gamePlayer.getId()),HttpStatus.CREATED);
    }

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();


        if (!isGuest(authentication)){
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.toDTO());}
        else {
            dto.put("player", null);
        }
            dto.put("games", gameRepo.findAll().stream()
                    .map(Game::toDTO)
                    .collect(toSet()));
            return dto;



    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication){

        ResponseEntity<Map<String, Object>> response;
        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId).orElse(null);

        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "NO esta autorizado"), HttpStatus.UNAUTHORIZED);
        }else if (gamePlayer == null){
            response = new ResponseEntity<>(makeMap("error", "This Game Player doesn't exist"), HttpStatus.FORBIDDEN);
        }else if(gamePlayer.getShips().size() >= 5){
            response = new ResponseEntity<>(makeMap("error", "You can't have more than 5 ships"), HttpStatus.FORBIDDEN);
        }else if(ships.size() < 5){
            response = new ResponseEntity<>(makeMap("error", "You can't have less than 5 ships"), HttpStatus.FORBIDDEN);
        }else{
            ships.forEach(ship -> {
                gamePlayer.addShip(ship);
            });
            gamePlayerRepo.save(gamePlayer);
            response = new ResponseEntity<>(HttpStatus.CREATED);
        }
        return response;
    }

    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String,Object>> addSalvos(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication){

        ResponseEntity<Map<String, Object>> response;
        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId).orElse(null);

        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Not Authorized"), HttpStatus.UNAUTHORIZED);
        }else if (gamePlayer == null) {
            response = new ResponseEntity<>(makeMap("error", "This Game Player doesn't exist"), HttpStatus.UNAUTHORIZED);
        }else if (gamePlayer.getSalvoes().stream().anyMatch(item -> item.getTurnNumber() == salvo.getTurnNumber())){
            response = new ResponseEntity<>(makeMap("error", "You have already submitted this salvo"), HttpStatus.FORBIDDEN);
        }else {
            gamePlayer.addSalvoes(salvo);
            gamePlayerRepo.save(gamePlayer);
            response = new ResponseEntity<>(HttpStatus.CREATED);
        }
    return response;

    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication){

        ResponseEntity<Map<String, Object>> response;
        Game gameState = gameRepo.findById(gameId).orElse(null);

        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "NO esta autorizado"), HttpStatus.UNAUTHORIZED);
        }else if (gameState == null){
            response = new ResponseEntity<>(makeMap("error", "game Id doesn't exist"), HttpStatus.FORBIDDEN);
        }else if(gameState.getPlayers().size() > 1){
            response = new ResponseEntity<>(makeMap("error", "game is full"), HttpStatus.FORBIDDEN);
        }else{
            Player player = playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = gamePlayerRepo.save(new GamePlayer(gameState, player));
            response = new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Map<String, Object>> gameView(@PathVariable long id, Authentication authentication) {

        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "User is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        Optional<GamePlayer> gamePlayerOptional = gamePlayerRepo.findById(id);

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Player == Null"), HttpStatus.UNAUTHORIZED);
        }

        if (!gamePlayerOptional.isPresent()) {
            return new ResponseEntity<>(makeMap("error", "GamePlayer == null"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayerOptional.get().getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "El ID de GamePlayer es diferente al del player"), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>((gamePlayerOptional.get().game_view()), HttpStatus.OK);
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password){
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}