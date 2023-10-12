package com.marco.pickpay.controllers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.mappers.UsuarioMapper;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    private UsuarioModel usuarioModel;

    private UsuarioRecord usuarioRecord;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioModel = new UsuarioModel();
        usuarioModel.setId(1l);
        usuarioModel.setNome("Nome");
        usuarioModel.setEmail("Email");
        usuarioModel.setSenha("Senha");
        usuarioModel.setCpfCnpj("CpfCnpj");
        usuarioModel.setTipoUsuario(TipoUsuario.COMUM);
        usuarioModel.setSaldo(50.0);
        usuarioRecord = UsuarioMapper.toRecord(usuarioModel);
    }

    @Test
    public void testCriarUsuario_Success() {
        // Arrange
        when(usuarioService.criarUsuario(usuarioRecord)).thenReturn(usuarioRecord);

        // Act
        ResponseEntity<UsuarioRecord> response = usuarioController.criarUsuario(usuarioRecord);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuarioRecord, response.getBody());
        verify(usuarioService, times(1)).criarUsuario(usuarioRecord);
        assertDoesNotThrow(() -> usuarioService.criarUsuario(usuarioRecord));
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void testAtualizarUsuario_Success() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioService.atualizarUsuario(usuarioId, usuarioRecord)).thenReturn(usuarioRecord);

        // Act
        ResponseEntity<UsuarioRecord> responseEntity =
                usuarioController.atualizarUsuario(usuarioId, usuarioRecord);

        // Assert
        assertEquals(usuarioRecord, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(usuarioService, times(1)).atualizarUsuario(usuarioId, usuarioRecord);
    }

    @Test
    public void testDeletarUsuarioPorId() {
        // Arrange
        Long usuarioId = 123L;
        // Act
        ResponseEntity<Void> response = usuarioController.deletarUsuario(usuarioId);
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testBuscarUsuarioPorId() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioService.buscarUsuarioPorId(usuarioId)).thenReturn(usuarioRecord);

        // Act
        ResponseEntity<UsuarioRecord> response = usuarioController.buscarUsuarioPorId(usuarioId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioRecord, response.getBody());
    }

    @Test
    public void testBuscarUsuarioPorEmail() {
        // Arrange
        String usuarioEmail = usuarioRecord.email();
        when(usuarioService.buscarUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioRecord);

        // Act
        ResponseEntity<UsuarioRecord> response =
                usuarioController.buscarUsuarioPorEmail(usuarioEmail);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioRecord, response.getBody());
    }

    @Test
    public void testBuscarUsuarioPorCpfCnpj() {
        // Arrange
        String usuarioCpfCnpj = usuarioRecord.cpfCnpj();
        when(usuarioService.buscarUsuarioPorCpfCnpj(usuarioCpfCnpj)).thenReturn(usuarioRecord);

        // Act
        ResponseEntity<UsuarioRecord> response =
                usuarioController.buscarUsuarioPorCpfCnpj(usuarioCpfCnpj);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioRecord, response.getBody());
    }

    @Test
    void buscarUsuarios() {
        // Arrange
        List<UsuarioRecord> usuarios = Collections.singletonList(usuarioRecord);
        when(usuarioService.buscarUsuarios()).thenReturn(usuarios);
        // Act
        ResponseEntity<List<UsuarioRecord>> response = usuarioController.buscarUsuarios();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarios, response.getBody());
    }
}
