package ru.staker.last;

import org.bukkit.entity.Player;

import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

public interface PlayerRepository {

    CompletableFuture<PlayerBalance> getBalance(Player player);

    void save(PlayerBalance player);

}
