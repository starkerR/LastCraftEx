package ru.staker.last;

import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

public class MysqlPlayerRepository implements PlayerRepository {

    private MysqlDatabase database;

    public MysqlPlayerRepository(MysqlDatabase database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<PlayerBalance> getBalance(Player player) {
        String sql = "select * from players where name=?";
        PreparedStatement preparedStatement = database.prepareStatement(sql, player.getName());
        CompletableFuture<ResultSet> query = database.query(preparedStatement);
        return query.thenApply(resultSet -> {
            try {
                String name = resultSet.getString("name");
                int money = resultSet.getInt("money");
                return new PlayerBalance(name, money);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void save(PlayerBalance balance) {
        String query = "insert into players(name, money) value(?,?)";
        PreparedStatement preparedStatement = database.prepareStatement(query, balance.getName(), String.valueOf(balance.getAmount()));
        database.execute(preparedStatement);
    }
}
