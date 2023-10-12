package com.marco.pickpay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.services.TransferenciaService;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @PostMapping
    public ResponseEntity<TransferenciaRecord> transferirValor(
            @RequestBody TransferenciaRecord transferenciaRecord) {
        TransferenciaRecord transferencia =
                transferenciaService.transferirValor(transferenciaRecord);
        return new ResponseEntity<>(transferencia, HttpStatus.CREATED);
    }


}
