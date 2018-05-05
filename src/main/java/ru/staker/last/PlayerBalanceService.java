package ru.staker.last;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface PlayerBalanceService {

    CompletableFuture<PlayerBalance> getBalance(Player player);

    void save(PlayerBalance player);

}
