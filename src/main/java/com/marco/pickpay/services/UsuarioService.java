package com.marco.pickpay.services;

import java.util.List;
import com.marco.pickpay.records.UsuarioRecord;

public interface UsuarioService {

    public UsuarioRecord criarUsuario(UsuarioRecord usuarioRecord);

    public UsuarioRecord atualizarUsuario(Long usuarioId, UsuarioRecord usuarioRecord);

    public void deletarUsuario(Long usuarioId);

    public UsuarioRecord buscarUsuarioPorId(Long usuarioId);

    public UsuarioRecord buscarUsuarioPorEmail(String email);

    public UsuarioRecord buscarUsuarioPorCpfCnpj(String cpfCnpj);

    public List<UsuarioRecord> buscarUsuarios();

}
