package pl.GabrielW.catan_server.api;

import org.springframework.web.bind.annotation.*;
import pl.GabrielW.catan_server.api.dto.GameActionRequest;
import pl.GabrielW.catan_server.api.dto.GameStateResponse;
import pl.GabrielW.catan_server.gameEngine.Game;
import pl.GabrielW.catan_server.service.GameService;

import java.util.UUID;

@RestController
@RequestMapping( "api/games" )
public class GameController {

    private final GameService gameService;

    public GameController( GameService gameService ){
        this.gameService = gameService;

    }

    @PostMapping("/{id}" )
    public GameStateResponse getGame( @PathVariable UUID id ){
        return GameStateResponse.from( id, gameService.getGame(id) );
    }

    @PostMapping( "/{id}/actions" )
    public GameStateResponse performAction(
            @PathVariable UUID id,
            @RequestBody GameActionRequest action
            ){
        Game game = gameService.makeAction( id, action );
        return GameStateResponse.from( id, game );
    }


}
