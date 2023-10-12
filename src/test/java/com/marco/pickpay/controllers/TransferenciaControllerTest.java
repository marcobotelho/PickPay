package com.marco.pickpay.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.services.TransferenciaService;

@ExtendWith(MockitoExtension.class)
public class TransferenciaControllerTest {

    @InjectMocks
    private TransferenciaController transferenciaController;

    @Mock
    private TransferenciaService transferenciaService;

    private TransferenciaRecord transferenciaRecord;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transferenciaRecord = new TransferenciaRecord(1L, 2L, 50.0);
    }

    @Test
    public void testTransferirValorComSucesso() {
        // Test case 1: Verify that the transferenciaService.transferirValor() method is called
        when(transferenciaService.transferirValor(transferenciaRecord)).thenReturn(transferenciaRecord);

        ResponseEntity<TransferenciaRecord> response =
                transferenciaController.transferirValor(transferenciaRecord);

        assertEquals(transferenciaRecord, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


}
