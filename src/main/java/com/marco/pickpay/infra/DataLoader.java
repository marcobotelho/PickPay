package com.marco.pickpay.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.services.UsuarioService;

@Component
public class DataLoader implements CommandLineRunner {
	
	@Autowired
	private UsuarioService usuarioService;

	@Override
	public void run(String... args) throws Exception {
		UsuarioRecord usuarioRecord = null; 
				
		usuarioRecord = new UsuarioRecord(null, "Usuario Um", "usuarioum@email.com", "123", "111111111", TipoUsuario.COMUM, 150.0);
		usuarioService.criarUsuario(usuarioRecord);
		
		usuarioRecord = new UsuarioRecord(null, "Usuario Dois", "usuariodois@email.com", "123", "222222222", TipoUsuario.LOJISTA, 50.0);
		usuarioService.criarUsuario(usuarioRecord);
		
		usuarioRecord = new UsuarioRecord(null, "Usuario Tres", "usuariotres@email.com", "123", "333333333", TipoUsuario.COMUM, 80.0);
		usuarioService.criarUsuario(usuarioRecord);
	}
}
