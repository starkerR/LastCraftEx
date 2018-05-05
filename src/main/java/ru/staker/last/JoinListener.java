package ru.staker.last;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class JoinListener implements Listener {

    private PlayerBalanceService balanceService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CompletableFuture<PlayerBalance> balanceFuture = balanceService.getBalance(player);
        balanceFuture.thenAccept(balance -> {
            ItemStack itemStack = new ItemStack(Material.STONE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(Collections.singletonList(String.format("Монет: %d", balance.getAmount())));
            itemStack.setItemMeta(itemMeta);
            player.getInventory().addItem(itemStack);
        });
    }

}
