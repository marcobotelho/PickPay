package com.marco.pickpay.mappers;

import java.util.List;
import com.marco.pickpay.models.TransferenciaModel;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.TransferenciaRecord;

public class TransferenciaMapper {

    public static TransferenciaModel toModel(Long transferenciaId, UsuarioModel remetente,
            UsuarioModel destinatario, Double valor) {
        TransferenciaModel transferenciaModel = new TransferenciaModel();
        transferenciaModel.setId(transferenciaId);
        transferenciaModel.setRemetente(remetente);
        transferenciaModel.setDestinatario(destinatario);
        transferenciaModel.setValor(valor);
        return transferenciaModel;
    }

    public static TransferenciaRecord toRecord(TransferenciaModel transferenciaModel) {
        Long transferenciaId = transferenciaModel.getId();
        Long remetenteId = transferenciaModel.getRemetente().getId();
        Long destinatarioId = transferenciaModel.getDestinatario().getId();
        Double valor = transferenciaModel.getValor();
        return new TransferenciaRecord(transferenciaId, remetenteId, destinatarioId, valor);
    }

    public static List<TransferenciaRecord> toRecords(
            List<TransferenciaModel> transferenciasModel) {
        return transferenciasModel.stream().map(TransferenciaMapper::toRecord).toList();
    }
}
