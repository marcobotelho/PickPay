package com.marco.pickpay.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.services.UsuarioService;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioRecord> criarUsuario(@RequestBody UsuarioRecord usuarioRecord) {
        UsuarioRecord createdUsuario = usuarioService.criarUsuario(usuarioRecord);
        return new ResponseEntity<>(createdUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioRecord> atualizarUsuario(@PathVariable Long usuarioId,
            @RequestBody UsuarioRecord usuarioRecord) {
        UsuarioRecord updatedUsuario = usuarioService.atualizarUsuario(usuarioId, usuarioRecord);
        return new ResponseEntity<>(updatedUsuario, HttpStatus.OK);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long usuarioId) {
        usuarioService.deletarUsuario(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioRecord> buscarUsuarioPorId(@PathVariable Long usuarioId) {
        UsuarioRecord usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioRecord> buscarUsuarioPorEmail(@PathVariable String email) {
        UsuarioRecord usuario = usuarioService.buscarUsuarioPorEmail(email);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping("/cpf-cnpj/{cpf-cnpj}")
    public ResponseEntity<UsuarioRecord> buscarUsuarioPorCpfCnpj(
            @PathVariable(name = "cpf-cnpj") String cpfCnpj) {
        UsuarioRecord usuario = usuarioService.buscarUsuarioPorCpfCnpj(cpfCnpj);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRecord>> buscarUsuarios() {
        List<UsuarioRecord> usuarios = usuarioService.buscarUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
}
