package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.Coordinate;

import java.util.HashSet;

public record UpgradeDto( HashSet< Coordinate > coordinates , String playerNickName ) { }

