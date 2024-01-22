package com.marco.pickpay.mappers;

import java.util.List;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.UsuarioRecord;

public class UsuarioMapper {

    public static UsuarioModel toModel(UsuarioRecord usuarioRecord) {
        return new UsuarioModel(usuarioRecord.id(), usuarioRecord.nome(), usuarioRecord.email(), usuarioRecord.senha(),
                usuarioRecord.cpfCnpj(), usuarioRecord.tipoUsuario(), usuarioRecord.saldo());
    }

    public static UsuarioRecord toRecord(UsuarioModel usuarioModel) {
        return new UsuarioRecord(usuarioModel.getId(), usuarioModel.getNome(), usuarioModel.getEmail(),
                usuarioModel.getSenha(), usuarioModel.getCpfCnpj(), usuarioModel.getTipoUsuario(),
                usuarioModel.getSaldo());
    }

    public static List<UsuarioRecord> toRecords(List<UsuarioModel> usuarioModels) {
        return usuarioModels.stream().map(UsuarioMapper::toRecord).toList();
    }
}


