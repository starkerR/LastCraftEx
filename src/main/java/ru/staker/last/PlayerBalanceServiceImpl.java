package ru.staker.last;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

public class PlayerBalanceServiceImpl implements PlayerBalanceService {

    private Map<Player, PlayerBalance> balances = new WeakHashMap<>();

    private PlayerRepository repository;

    public PlayerBalanceServiceImpl(PlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<PlayerBalance> getBalance(Player player) {
        PlayerBalance playerBalance = balances.get(player);
        if(playerBalance == null) {
            CompletableFuture<PlayerBalance> balanceFuture = repository.getBalance(player);
            balanceFuture.thenAccept(balance -> {
                balances.put(Bukkit.getPlayer(balance.getName()), balance);
            }).exceptionally(throwable -> { throw new RuntimeException(throwable); });
            return balanceFuture;
        }
        return CompletableFuture.completedFuture(playerBalance);
    }


    @Override
    public void save(PlayerBalance balance) {
        repository.save(balance);
    }
}
