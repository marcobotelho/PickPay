package com.marco.pickpay.services;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.marco.pickpay.mappers.UsuarioMapper;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.repositories.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String getSenhaCripto(String senha) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(senha);
    }

    @Override
    public UsuarioRecord criarUsuario(UsuarioRecord usuarioRecord) {
        if (usuarioRecord.senha() == null) {
            throw new RuntimeException("Senha nula não é valida");
        }
        String senhaCripto = getSenhaCripto(usuarioRecord.senha());
        usuarioRecord = new UsuarioRecord(null, usuarioRecord.nome(), usuarioRecord.email(), senhaCripto,
                usuarioRecord.cpfCnpj(), usuarioRecord.tipoUsuario(), usuarioRecord.saldo());

        UsuarioModel usuarioModel = UsuarioMapper.toModel(usuarioRecord);
        usuarioModel = usuarioRepository.save(usuarioModel);

        return UsuarioMapper.toRecord(usuarioModel);
    }

    @Override
    public UsuarioRecord atualizarUsuario(Long usuarioId, UsuarioRecord usuarioRecord) {
        UsuarioModel usuarioModel =
                usuarioRepository.findById(usuarioId).orElseThrow(() -> new NoSuchElementException(
                        "Usuário com id '" + usuarioId + "' não encontrado"));
        if (usuarioRecord.senha() == null) {
            throw new RuntimeException("Senha nula não é valida");
        }
        String senhaCripto = getSenhaCripto(usuarioRecord.senha());

        usuarioModel.setId(usuarioId);
        usuarioModel.setNome(usuarioRecord.nome());
        usuarioModel.setEmail(usuarioRecord.email());
        usuarioModel.setSenha(senhaCripto);
        usuarioModel.setCpfCnpj(usuarioRecord.cpfCnpj());
        usuarioModel.setTipoUsuario(usuarioRecord.tipoUsuario());
        usuarioModel.setSaldo(usuarioRecord.saldo());

        usuarioModel = usuarioRepository.save(usuarioModel);

        return UsuarioMapper.toRecord(usuarioModel);
    }

    @Override
    public void deletarUsuario(Long usuarioId) {
        usuarioRepository.deleteById(usuarioId);
    }

    @Override
    public UsuarioRecord buscarUsuarioPorId(Long usuarioId) {
        UsuarioModel usuarioModel =
                usuarioRepository.findById(usuarioId).orElseThrow(() -> new NoSuchElementException(
                        "Usuário com id '" + usuarioId + "' não encontrado"));
        return UsuarioMapper.toRecord(usuarioModel);
    }

    @Override
    public UsuarioRecord buscarUsuarioPorEmail(String email) {
        UsuarioModel usuarioModel =
                usuarioRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException(
                        "Usuário com email '" + email + "' não encontrado"));
        return UsuarioMapper.toRecord(usuarioModel);
    }

    @Override
    public UsuarioRecord buscarUsuarioPorCpfCnpj(String cpfCnpj) {
        UsuarioModel usuarioModel = usuarioRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new NoSuchElementException(
                        "Usuário com CPF/CNPJ '" + cpfCnpj + "' não encontrado"));
        return UsuarioMapper.toRecord(usuarioModel);
    }

    @Override
    public List<UsuarioRecord> buscarUsuarios() {
        return UsuarioMapper.toRecords(usuarioRepository.findAll());
    }

}
