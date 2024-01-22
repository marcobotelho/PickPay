package com.marco.pickpay.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.mappers.UsuarioMapper;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.repositories.TransferenciaRepository;
import com.marco.pickpay.repositories.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferenciaServiceIT {

        @Autowired
        private TransferenciaRepository transferenciaRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private TransferenciaServiceImpl transferenciaService;

        private UsuarioRecord remetenteRecord;

        private UsuarioRecord destinatarioRecord;

        private TransferenciaRecord transferenciaRecord;

        private UsuarioModel remetenteModel;

        private UsuarioModel destinatarioModel;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);

                remetenteRecord = new UsuarioRecord(null, "Nome Remetente", "remetente@email.com",
                                "senharemetente", "111111111", TipoUsuario.COMUM, 100.0);

                destinatarioRecord = new UsuarioRecord(null, "Nome Destinatario",
                                "destinatario@email.com", "senhadestinatario", "222222222",
                                TipoUsuario.LOJISTA, 0.0);

                remetenteModel = usuarioRepository.save(UsuarioMapper.toModel(remetenteRecord));

                destinatarioModel =
                                usuarioRepository.save(UsuarioMapper.toModel(destinatarioRecord));

                transferenciaRecord = new TransferenciaRecord(null, remetenteModel.getId(),
                                destinatarioModel.getId(), 50.0);

        }

        @AfterEach
        public void tearDown() {
                usuarioRepository.deleteById(remetenteModel.getId());
                usuarioRepository.deleteById(destinatarioModel.getId());
        }

        @Test
        public void testTransferirValor_WhenRemetenteExistsAndHasSufficientBalance_ShouldTransferSuccessfully() {
                // Arrange

                // Act
                TransferenciaRecord result =
                                transferenciaService.transferirValor(transferenciaRecord);

                // Assert
                assertNotNull(result);
                // assertEquals(transferenciaRecord.id(), result.id());
                assertEquals(transferenciaRecord.remetenteId(), result.remetenteId());
                assertEquals(transferenciaRecord.destinatarioId(), result.destinatarioId());
                assertEquals(transferenciaRecord.valor(), result.valor(), 0.001);

                transferenciaRepository.deleteById(result.id());
        }

        @Test
        public void testTransferirValor_WhenRemetenteNotExists_ShouldThrowNoSuchElementException() {
                Long remetenteId = 0L;

                // Arrange
                transferenciaRecord = new TransferenciaRecord(null, remetenteId,
                                transferenciaRecord.destinatarioId(), transferenciaRecord.valor());

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
                // Arrange
                Long destinatarioId = 0L;

                transferenciaRecord =
                                new TransferenciaRecord(null, transferenciaRecord.remetenteId(),
                                                destinatarioId, transferenciaRecord.valor());

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
                remetenteModel.setTipoUsuario(TipoUsuario.LOJISTA);
                remetenteModel = usuarioRepository.save(remetenteModel);

                transferenciaRecord = new TransferenciaRecord(null, remetenteModel.getId(),
                                destinatarioModel.getId(), transferenciaRecord.valor());

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
                TransferenciaRecord transferenciaRecord = new TransferenciaRecord(null,
                                remetenteModel.getId(), destinatarioModel.getId(), 150.0);

                // Act
                var result = assertThrows(RuntimeException.class,
                                () -> transferenciaService.transferirValor(transferenciaRecord));

                // Assert
                // Exception expected
                assertEquals("Remetente com saldo insuficiente", result.getMessage());
        }
}
