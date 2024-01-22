package com.marco.pickpay.services;

import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.mappers.TransferenciaMapper;
import com.marco.pickpay.models.TransferenciaModel;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.records.WebserviceRecord;
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
        UsuarioModel remetente = usuarioRepository.findById(transferenciaRecord.remetenteId())
                .orElseThrow(() -> new NoSuchElementException("Remetente com id '"
                        + transferenciaRecord.remetenteId() + "' não encontrado"));
        if (remetente.getTipoUsuario().equals(TipoUsuario.LOJISTA)) {
            throw new RuntimeException("Lojistas não podem transferir dinheiro");
        }
        if (remetente.getSaldo() < transferenciaRecord.valor()) {
            throw new RuntimeException("Remetente com saldo insuficiente");
        }

        UsuarioModel destinatario = usuarioRepository.findById(transferenciaRecord.destinatarioId())
                .orElseThrow(() -> new NoSuchElementException("Destinatario com id '"
                        + transferenciaRecord.destinatarioId() + "' não encontrado"));

        TransferenciaModel transferenciaModel = TransferenciaMapper.toModel(null, remetente,
                destinatario, transferenciaRecord.valor());
        
        // Validar autorização
    	WebserviceRecord webserviceAuthorization = getJsonFromApi("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc");
    	System.out.println("Autorização: " + webserviceAuthorization.message());
		if (!webserviceAuthorization.message().equalsIgnoreCase("autorizado")) {
			throw new RuntimeException("Transferência não autorizada");
		}

        transferenciaModel = transferenciaRepository.save(transferenciaModel);

        atualizaSaldos(remetente, destinatario, transferenciaRecord.valor());
        
        // Validar notificação
        WebserviceRecord webserviceNotification = getJsonFromApi("https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6");
		System.out.println("Notificação: " + webserviceNotification.message());
		if (!webserviceNotification.message().equalsIgnoreCase("true")) {
			throw new RuntimeException("Transferência não notificada");
		}

        return TransferenciaMapper.toRecord(transferenciaModel);
    }
    
    private WebserviceRecord getJsonFromApi(String url) {
    	try {			
    		// Create a new WebClient
    		WebClient webClient = WebClient.create();
    		
    		// Send a GET request to the API endpoint and retrieve the JSON response
    		WebserviceRecord webserviceRecord = webClient.get()
    				.uri(url)
    				.retrieve()
    				.bodyToMono(WebserviceRecord.class)
    				.block();
    		
    		// Return the JSON data
    		return webserviceRecord;
		} catch (Exception e) {
			throw new RuntimeException("Erro ao acessar webservice: " + url);
		}
    }
	
    
    
     
}
