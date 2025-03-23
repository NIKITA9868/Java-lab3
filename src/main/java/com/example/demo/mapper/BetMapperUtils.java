package com.example.demo.mapper;

import com.example.demo.dto.BetDto;
import com.example.demo.entity.Bet;


public class BetMapperUtils {

    private BetMapperUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static BetDto converttobetdto(Bet bet) {
        BetDto betDto = new BetDto();
        betDto.setId(bet.getId());
        betDto.setAmount(bet.getAmount());
        betDto.setPlayerId(bet.getPlayer().getId());
        return betDto;
    }
}
