package com.marco.pickpay.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.models.TransferenciaModel;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.repositories.TransferenciaRepository;
import com.marco.pickpay.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class TransferenciaServiceTest {

        @Mock
        private TransferenciaRepository transferenciaRepository;

        @Mock
        private UsuarioRepository usuarioRepository;

        @InjectMocks
        private TransferenciaServiceImpl transferenciaService;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testTransferirValor_WhenRemetenteExistsAndHasSufficientBalance_ShouldTransferSuccessfully() {
                // Arrange
                UsuarioModel remetente = new UsuarioModel();
                remetente.setId(1L);
                remetente.setNome("Remetente");
                remetente.setEmail("remetente@remetente");
                remetente.setSenha("remetente");
                remetente.setCpfCnpj("1234567890");
                remetente.setTipoUsuario(TipoUsuario.COMUM);
                remetente.setSaldo(100.0);
                when(usuarioRepository.findById(remetente.getId()))
                                .thenReturn(Optional.of(remetente));

                UsuarioModel destinatario = new UsuarioModel();
                destinatario.setId(2L);
                destinatario.setNome("Destinatario");
                destinatario.setEmail("destinatario@destinatario");
                destinatario.setSenha("destinatario");
                destinatario.setCpfCnpj("0987654321");
                destinatario.setTipoUsuario(TipoUsuario.LOJISTA);
                destinatario.setSaldo(0.0);
                when(usuarioRepository.findById(destinatario.getId()))
                                .thenReturn(Optional.of(destinatario));

                TransferenciaRecord transferenciaRecord =
                                new TransferenciaRecord(null, 1L, 2L, 50.0);
                TransferenciaModel transferenciaModel = new TransferenciaModel(remetente,
                                destinatario, transferenciaRecord.valor());
                when(transferenciaRepository.save(transferenciaModel))
                                .thenReturn(transferenciaModel);

                // Act
                TransferenciaRecord result =
                                transferenciaService.transferirValor(transferenciaRecord);

                // Assert
                assertNotNull(result);
                assertEquals(1L, result.remetenteId());
                assertEquals(2L, result.destinatarioId());
                assertEquals(50.0, result.valor(), 0.001);
        }

        @Test
        public void testTransferirValor_WhenRemetenteNotExists_ShouldThrowNoSuchElementException() {
                Long remetenteId = 1L;
                Long destinatarioId = 2L;
                Double valor = 50.0;

                // Arrange
                when(usuarioRepository.findById(remetenteId)).thenReturn(Optional.empty());

                TransferenciaRecord transferenciaRecord =
                                new TransferenciaRecord(null, remetenteId, destinatarioId, valor);

                // Act
                var result = assertThrows(NoSuchElementException.class,
                                () -> transferenciaService.transferirValor(transferenciaRecord));

                // Assert
                // Exception expected
                assertEquals("Remetente com id '" + remetenteId + "' não encontrado",
                                result.getMessage());
        }

        @Test
        public void testTransferirValor_WhenDestinatarioNotExists_ShouldThrowNoSuchElementException() {
                Long remetenteId = 1L;
                Long destinatarioId = 2L;
                Double valor = 50.0;

                UsuarioModel remetente = new UsuarioModel();
                remetente.setId(remetenteId);
                remetente.setTipoUsuario(TipoUsuario.COMUM);
                remetente.setSaldo(100.0);

                // Arrange
                when(usuarioRepository.findById(remetenteId)).thenReturn(Optional.of(remetente));
                when(usuarioRepository.findById(destinatarioId)).thenReturn(Optional.empty());

                TransferenciaRecord transferenciaRecord =
                                new TransferenciaRecord(null, remetenteId, destinatarioId, valor);

                // Act
                var result = assertThrows(NoSuchElementException.class,
                                () -> transferenciaService.transferirValor(transferenciaRecord));

                // Assert
                // Exception expected
                assertEquals("Destinatario com id '" + destinatarioId + "' não encontrado",
                                result.getMessage());
        }

        @Test
        public void testTransferirValor_WhenLojistaTriesToTransfer_ShouldThrowRuntimeException() {
                // Arrange
                UsuarioModel remetente = new UsuarioModel();
                remetente.setTipoUsuario(TipoUsuario.LOJISTA);
                when(usuarioRepository.findById(any())).thenReturn(Optional.of(remetente));

                TransferenciaRecord transferenciaRecord =
                                new TransferenciaRecord(null, 1L, 2L, 50.0);

                // Act
                var result = assertThrows(RuntimeException.class,
                                () -> transferenciaService.transferirValor(transferenciaRecord));

                // Assert
                // Exception expected
                assertEquals("Lojistas não podem transferir dinheiro", result.getMessage());
        }

        @Test
        public void testTransferirValor_WhenRemetenteHasInsufficientBalance_ShouldThrowRuntimeException() {
                // Arrange
                UsuarioModel remetente = new UsuarioModel();
                remetente.setTipoUsuario(TipoUsuario.COMUM);
                remetente.setSaldo(100.0);
                when(usuarioRepository.findById(any())).thenReturn(Optional.of(remetente));

                TransferenciaRecord transferenciaRecord =
                                new TransferenciaRecord(null, 1L, 2L, 150.0);

                // Act
                var result = assertThrows(RuntimeException.class,
                                () -> transferenciaService.transferirValor(transferenciaRecord));

                // Assert
                // Exception expected
                assertEquals("Remetente com saldo insuficiente", result.getMessage());
        }
}
