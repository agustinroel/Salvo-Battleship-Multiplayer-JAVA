package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.*;
import java.util.Arrays;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    /*@Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, ScoreRepository scoreRepo) {
        return (args) -> {
            // save a couple of players
            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder.encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder.encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder.encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder.encode("mole"));
            Game game1 = new Game(LocalDateTime.now());
            Game game2 = new Game(LocalDateTime.now().plusHours(1));
            Game game3 = new Game(LocalDateTime.now().plusHours(2));
            Game game4 = new Game(LocalDateTime.now().plusHours(3));
            Game game5 = new Game(LocalDateTime.now().plusHours(4));
            Game game6 = new Game(LocalDateTime.now().plusHours(5));
            Game game7 = new Game(LocalDateTime.now().plusHours(6));
            Game game8 = new Game(LocalDateTime.now().plusHours(7));
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);
            gameRepository.save(game7);
            gameRepository.save(game8);
            scoreRepo.save(new Score(player1, game1, 1));
            scoreRepo.save(new Score(player2, game1, 0));
            scoreRepo.save(new Score(player1, game2, 0.5));
            scoreRepo.save(new Score(player2, game2, 0.5));
            scoreRepo.save(new Score(player2, game3, 1));
            scoreRepo.save(new Score(player4, game3, 0));
            scoreRepo.save(new Score(player2, game4, 0.5));
            scoreRepo.save(new Score(player1, game4, 0.5));
            GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
            gamePlayer1.addShip(new Ship("destroyer", Arrays.asList("H2", "H3", "H4")));
            gamePlayer1.addShip(new Ship("submarine", Arrays.asList("E1", "F1", "G1")));
            gamePlayer1.addShip(new Ship("patrol-boat", Arrays.asList("B4", "B5")));
            gamePlayer1.addSalvoes(new Salvo(1, Arrays.asList("B5", "C5", "F1")));
            gamePlayer1.addSalvoes(new Salvo(2, Arrays.asList("F2", "D5")));
            gamePlayerRepository.save(gamePlayer1);
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
            gamePlayer2.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer2.addShip(new Ship("patrol-boat", Arrays.asList("F1", "F2")));
            gamePlayer2.addSalvoes(new Salvo(1, Arrays.asList("B4", "B5", "B6")));
            gamePlayer2.addSalvoes(new Salvo(2, Arrays.asList("E1", "H3", "A2")));
            gamePlayerRepository.save(gamePlayer2);
            GamePlayer gamePlayer3 = new GamePlayer(game2, player1);
            gamePlayer3.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer3.addShip(new Ship("patrol-boat", Arrays.asList("C6", "C7")));
            gamePlayer3.addSalvoes(new Salvo(1, Arrays.asList("A2", "A4", "G6")));
            gamePlayer3.addSalvoes(new Salvo(2, Arrays.asList("A3", "H6")));
            gamePlayerRepository.save(gamePlayer3);
            GamePlayer gamePlayer4 = new GamePlayer(game2, player2);
            gamePlayer4.addShip(new Ship("submarine", Arrays.asList("A2", "A3", "A4")));
            gamePlayer4.addShip(new Ship("patrol-boat", Arrays.asList("G6", "H6")));
            gamePlayer4.addSalvoes(new Salvo(1, Arrays.asList("B5", "D5", "C7")));
            gamePlayer4.addSalvoes(new Salvo(2, Arrays.asList("C5", "C6")));
            gamePlayerRepository.save(gamePlayer4);
            GamePlayer gamePlayer5 = new GamePlayer(game3, player2);
            gamePlayer5.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer5.addShip(new Ship("patrol-boat", Arrays.asList("C6", "C7")));
            gamePlayer5.addSalvoes(new Salvo(1, Arrays.asList("G6", "H6", "A4")));
            gamePlayer5.addSalvoes(new Salvo(2, Arrays.asList("A2", "A3", "D8")));
            gamePlayerRepository.save(gamePlayer5);
            GamePlayer gamePlayer6 = new GamePlayer(game3, player4);
            gamePlayer6.addShip(new Ship("submarine", Arrays.asList("A2", "A3", "A4")));
            gamePlayer6.addShip(new Ship("patrol-boat", Arrays.asList("G6", "H6")));
            gamePlayer6.addSalvoes(new Salvo(1, Arrays.asList("H1", "H2", "H3")));
            gamePlayer6.addSalvoes(new Salvo(2, Arrays.asList("E1", "F2", "G3")));
            gamePlayerRepository.save(gamePlayer6);
            GamePlayer gamePlayer7 = new GamePlayer(game4, player2);
            gamePlayer7.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer7.addShip(new Ship("patrol-boat", Arrays.asList("C6", "C7")));
            gamePlayer7.addSalvoes(new Salvo(1, Arrays.asList("A3", "A4", "F7")));
            gamePlayer7.addSalvoes(new Salvo(2, Arrays.asList("A2", "G6", "H6")));
            gamePlayerRepository.save(gamePlayer7);
            GamePlayer gamePlayer8 = new GamePlayer(game4, player1);
            gamePlayer8.addShip(new Ship("submarine", Arrays.asList("A2", "A3", "A4")));
            gamePlayer8.addShip(new Ship("patrol-boat", Arrays.asList("G6", "H6")));
            gamePlayer8.addSalvoes(new Salvo(1, Arrays.asList("B5", "C6", "H1")));
            gamePlayer8.addSalvoes(new Salvo(2, Arrays.asList("C5", "C7", "D5")));
            gamePlayerRepository.save(gamePlayer8);
            GamePlayer gamePlayer9 = new GamePlayer(game5, player4);
            gamePlayer9.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer9.addShip(new Ship("patrol-boat", Arrays.asList("C6", "C7")));
            gamePlayer9.addSalvoes(new Salvo(1, Arrays.asList("A1", "A2", "A3")));
            gamePlayer9.addSalvoes(new Salvo(2, Arrays.asList("G6", "G7", "G8")));
            gamePlayerRepository.save(gamePlayer9);
            GamePlayer gamePlayer10 = new GamePlayer(game5, player1);
            gamePlayer10.addShip(new Ship("submarine", Arrays.asList("A2", "A3", "A4")));
            gamePlayer10.addShip(new Ship("patrol-boat", Arrays.asList("G6", "H6")));
            gamePlayer10.addSalvoes(new Salvo(1, Arrays.asList("B5", "B6", "C7")));
            gamePlayer10.addSalvoes(new Salvo(2, Arrays.asList("C6", "D6", "E6")));
            gamePlayer10.addSalvoes(new Salvo(3, Arrays.asList("H1", "H8")));
            gamePlayerRepository.save(gamePlayer10);
            GamePlayer gamePlayer11 = new GamePlayer(game6, player3);
            gamePlayer11.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer11.addShip(new Ship("patrol-boat", Arrays.asList("C6", "C7")));
            gamePlayerRepository.save(gamePlayer11);
            GamePlayer gamePlayer12 = new GamePlayer(game8, player3);
            gamePlayer12.addShip(new Ship("destroyer", Arrays.asList("B5", "C5", "D5")));
            gamePlayer12.addShip(new Ship("patrol-boat", Arrays.asList("G6", "H6")));
            gamePlayerRepository.save(gamePlayer12);
            GamePlayer gamePlayer13 = new GamePlayer(game8, player4);
            gamePlayer13.addShip(new Ship("submarine", Arrays.asList("A2", "A3", "A4")));
            gamePlayer13.addShip(new Ship("patrol-boat", Arrays.asList("G6", "H6")));
            gamePlayerRepository.save(gamePlayer13);
        };
    }*/
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userName-> {
            Player player = playerRepository.findByUserName(userName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + userName);
            }
        });
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/login", "/api/games", "/api/players").permitAll()
                .antMatchers("/api/**", "/web/game.html").hasAuthority("USER")
                .antMatchers( "/web/**").permitAll()
                .and()
                    .formLogin()
                    .usernameParameter("email")
                    .passwordParameter("pwd")
                    .loginPage("/api/login")
                    .defaultSuccessUrl("/web/games.html")
                .and()
                    .logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

    }
}

