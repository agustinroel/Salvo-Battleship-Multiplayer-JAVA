package com.codeoftheweb.salvo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.time.*;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
    //List<Game> findById(long id);
}