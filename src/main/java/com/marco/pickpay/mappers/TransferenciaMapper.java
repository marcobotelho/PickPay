package com.marco.pickpay.mappers;

import java.util.List;
import com.marco.pickpay.models.TransferenciaModel;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.TransferenciaRecord;

public class TransferenciaMapper {

    public static TransferenciaModel toModel(UsuarioModel remetente, UsuarioModel destinatario,
            Double valor) {
        return new TransferenciaModel(remetente, destinatario, valor);
    }

    public static TransferenciaRecord toRecord(TransferenciaModel transferenciaModel) {
        Long remetenteId = transferenciaModel.getRemetente().getId();
        Long destinatarioId = transferenciaModel.getDestinatario().getId();
        Double valor = transferenciaModel.getValor();
        return new TransferenciaRecord(remetenteId, destinatarioId, valor);
    }

    public static List<TransferenciaRecord> toRecords(
            List<TransferenciaModel> transferenciasModel) {
        return transferenciasModel.stream().map(TransferenciaMapper::toRecord).toList();
    }
}
