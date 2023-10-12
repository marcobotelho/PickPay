package com.marco.pickpay.records;

import java.time.LocalDateTime;

public record ErroRecord(LocalDateTime data, String status, String erro, String mensagem,
        String url) {

}
