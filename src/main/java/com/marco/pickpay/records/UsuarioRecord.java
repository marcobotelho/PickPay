package com.marco.pickpay.records;

import com.marco.pickpay.enums.TipoUsuario;

public record UsuarioRecord(Long id, String nome, String email, String senha, String cpfCnpj,
                TipoUsuario tipoUsuario, Double saldo) {

}
