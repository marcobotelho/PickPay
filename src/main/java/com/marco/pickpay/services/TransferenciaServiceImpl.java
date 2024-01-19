package com.marco.pickpay.services;

import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.mappers.TransferenciaMapper;
import com.marco.pickpay.models.TransferenciaModel;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.repositories.TransferenciaRepository;
import com.marco.pickpay.repositories.UsuarioRepository;

@Service
@Transactional
public class TransferenciaServiceImpl implements TransferenciaService {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void atualizaSaldos(UsuarioModel remetente, UsuarioModel destinatario, double valor) {
        remetente.setSaldo(remetente.getSaldo() - valor);
        destinatario.setSaldo(destinatario.getSaldo() + valor);
        usuarioRepository.save(remetente);
        usuarioRepository.save(destinatario);
    }

    @Override
    public TransferenciaRecord transferirValor(TransferenciaRecord transferenciaRecord) {
        Long remetenteId = transferenciaRecord.remetenteId();
        if (remetenteId == null) {
            throw new RuntimeException("Id do Remetente não pode ser vazio");
        }
        UsuarioModel remetente = usuarioRepository.findById(remetenteId)
                .orElseThrow(() -> new NoSuchElementException("Remetente com id '"
                        + transferenciaRecord.remetenteId() + "' não encontrado"));
        if (remetente.getTipoUsuario().equals(TipoUsuario.LOJISTA)) {
            throw new RuntimeException("Lojistas não podem transferir dinheiro");
        }
        if (remetente.getSaldo() < transferenciaRecord.valor()) {
            throw new RuntimeException("Remetente com saldo insuficiente");
        }

        Long destinatarioId = transferenciaRecord.destinatarioId();
        if (destinatarioId == null) {
            throw new RuntimeException("Id do Destinatário não pode ser vazio");
        }
        UsuarioModel destinatario = usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new NoSuchElementException("Destinatario com id '"
                        + transferenciaRecord.destinatarioId() + "' não encontrado"));

        TransferenciaModel transferenciaModel = TransferenciaMapper.toModel(null, remetente,
                destinatario, transferenciaRecord.valor());

        if (transferenciaModel == null) {
            throw new RuntimeException("Erro ao efetuar transferência");
        }
        transferenciaModel = transferenciaRepository.save(transferenciaModel);

        atualizaSaldos(remetente, destinatario, transferenciaRecord.valor());

        return TransferenciaMapper.toRecord(transferenciaModel);
    }


}
