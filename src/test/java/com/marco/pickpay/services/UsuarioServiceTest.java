package com.marco.pickpay.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.mappers.UsuarioMapper;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.repositories.UsuarioRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCriarUsuarioSucesso() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", "johndoe@example.com",
                "password", "1234567890", TipoUsuario.COMUM, 0.0);
        UsuarioModel usuarioModel = UsuarioMapper.toModel(usuarioRecord);
        Mockito.when(usuarioRepository.save(usuarioModel)).thenReturn(usuarioModel);
        UsuarioRecord result = usuarioService.criarUsuario(usuarioRecord);
        assertNotNull(result);
        assertEquals(usuarioRecord.nome(), result.nome());
        assertEquals(usuarioRecord.email(), result.email());
        assertEquals(usuarioRecord.senha(), result.senha());
        assertEquals(usuarioRecord.cpfCnpj(), result.cpfCnpj());
        assertEquals(usuarioRecord.tipoUsuario(), result.tipoUsuario());
        assertEquals(usuarioRecord.saldo(), result.saldo());

    }

    private Set<ConstraintViolation<Object>> getConstraintViolations(Object object) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator.validate(object);
    }

    @Test
    public void testCriarUsuarioErroValidacao() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("", "", "", "", null, null);
        UsuarioModel usuarioModel = UsuarioMapper.toModel(usuarioRecord);

        Set<ConstraintViolation<Object>> constraintViolations =
                getConstraintViolations(usuarioModel);

        String mensagemDeErro = "Erros de validação";

        ConstraintViolationException exception =
                new ConstraintViolationException(mensagemDeErro, constraintViolations);

        when(usuarioRepository.save(any(UsuarioModel.class))).thenThrow(exception);

        ConstraintViolationException result = assertThrows(ConstraintViolationException.class,
                () -> usuarioService.criarUsuario(usuarioRecord));

        verifyNoMoreInteractions(usuarioRepository);

        assertEquals(mensagemDeErro, result.getMessage());

        for (ConstraintViolation<?> violation : result.getConstraintViolations()) {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            String errorMessage = violation.getMessage();
            if (fieldName.equals("nome")) {
                assertEquals("O nome deve ser informado", errorMessage);
            } else if (fieldName.equals("email")) {
                assertEquals("O email deve ser informado", errorMessage);
            } else if (fieldName.equals("cpfCnpj")) {
                assertEquals("O cpf/cnpj deve ser informado", errorMessage);
            } else if (fieldName.equals("tipoUsuario")) {
                assertEquals("O tipo de usuario deve ser informado", errorMessage);
            } else if (fieldName.equals("saldo")) {
                assertEquals("O saldo deve ser informado", errorMessage);
            } else if (fieldName.equals("senha")) {
                assertEquals("A senha deve ser informada", errorMessage);
            }
        }
    }

    @Test
    public void testCriarUsuarioErroValidacaoEmail() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", "johndoe.example.com",
                "password", "1234567890", TipoUsuario.COMUM, 0.0);
        UsuarioModel usuarioModel = UsuarioMapper.toModel(usuarioRecord);

        Set<ConstraintViolation<Object>> constraintViolations =
                getConstraintViolations(usuarioModel);

        String mensagemDeErro = "Erro de validação email";

        ConstraintViolationException exception =
                new ConstraintViolationException(mensagemDeErro, constraintViolations);

        when(usuarioRepository.save(any(UsuarioModel.class))).thenThrow(exception);

        ConstraintViolationException result = assertThrows(ConstraintViolationException.class,
                () -> usuarioService.criarUsuario(usuarioRecord));

        verifyNoMoreInteractions(usuarioRepository);

        assertEquals(mensagemDeErro, result.getMessage());
        assertEquals(1, result.getConstraintViolations().size());

        for (ConstraintViolation<?> violation : result.getConstraintViolations()) {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            String errorMessage = violation.getMessage();
            if (fieldName.equals("email")) {
                assertEquals("O email deve ser válido", errorMessage);
            }
        }
    }

    @Test
    public void testCriarUsuarioErroValidacaoSenha() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", "johndoe@example.com", null,
                "1234567890", TipoUsuario.COMUM, 0.0);

        String mensagemDeErro = "Senha nula não é valida";

        RuntimeException result = assertThrows(RuntimeException.class,
                () -> usuarioService.criarUsuario(usuarioRecord));

        verifyNoInteractions(usuarioRepository);

        assertEquals(mensagemDeErro, result.getMessage());
    }

    @Test
    public void testAtualizarUsuario_Success() {
        // Arrange
        Long usuarioId = 1L;
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", "john.doe@example.com",
                "password", "1234567890", TipoUsuario.COMUM, 100.0);
        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setNome("Old Name");
        usuarioModel.setEmail("old.email@example.com");
        usuarioModel.setSenha("oldpassword");
        usuarioModel.setCpfCnpj("0987654321");
        usuarioModel.setTipoUsuario(TipoUsuario.LOJISTA);
        usuarioModel.setSaldo(50.0);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioModel));
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);

        // Act
        UsuarioRecord result = usuarioService.atualizarUsuario(usuarioId, usuarioRecord);

        // Assert
        assertEquals(usuarioRecord.nome(), result.nome());
        assertEquals(usuarioRecord.email(), result.email());
        Boolean senhaValida = passwordEncoder.matches(usuarioRecord.senha(), result.senha());
        assertTrue(senhaValida);
        assertEquals(usuarioRecord.cpfCnpj(), result.cpfCnpj());
        assertEquals(usuarioRecord.tipoUsuario(), result.tipoUsuario());
        assertEquals(usuarioRecord.saldo(), result.saldo(), 0.001);
    }

    @Test
    public void testAtualizarUsuario_NullPassword() {
        // Arrange
        Long usuarioId = 1L;
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", "john.doe@example.com", null,
                "1234567890", TipoUsuario.COMUM, 100.0);
        UsuarioModel usuarioModel = new UsuarioModel();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioModel));

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> usuarioService.atualizarUsuario(usuarioId, usuarioRecord));
    }

    @Test
    void testAtualizarUsuario_UsuarioNaoEncontrado() {
        final Long usuarioId = 2L;

        var result = Assertions.assertThrows(NoSuchElementException.class, () -> {
            usuarioService.atualizarUsuario(usuarioId, any(UsuarioRecord.class));
        });
        Assertions.assertEquals("Usuário com id '" + usuarioId + "' não encontrado",
                result.getMessage());
    }

    @Test
    public void testDeletarUsuarioComSucesso() {
        Long usuarioId = 1L;
        doNothing().when(usuarioRepository).deleteById(usuarioId);
        assertDoesNotThrow(() -> usuarioService.deletarUsuario(usuarioId));
        verify(usuarioRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    public void testDeletarUsuarioComErro() {
        Long usuarioId = 1L;
        doThrow(RuntimeException.class).when(usuarioRepository).deleteById(usuarioId);
        assertThrows(RuntimeException.class, () -> usuarioService.deletarUsuario(usuarioId));
        verify(usuarioRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    void testBuscarUsuarioPorIdComSucesso() {
        // Test case 1: Find existing user by ID
        final Long usuarioId = 1L;
        UsuarioRecord expectedUsuarioRecord = new UsuarioRecord("John Doe", "johndoe.example.com",
                "password", "1234567890", TipoUsuario.COMUM, 0.0);
        UsuarioModel usuarioModel = UsuarioMapper.toModel(expectedUsuarioRecord);
        usuarioModel.setId(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioModel));

        UsuarioRecord actualUsuarioRecord = usuarioService.buscarUsuarioPorId(usuarioId);

        Assertions.assertEquals(expectedUsuarioRecord, actualUsuarioRecord);
    }

    @Test
    void testBuscarUsuarioPorIdComErro() {
        final Long usuarioId = 2L;

        var result = Assertions.assertThrows(NoSuchElementException.class, () -> {
            usuarioService.buscarUsuarioPorId(usuarioId);
        });
        Assertions.assertEquals("Usuário com id '" + usuarioId + "' não encontrado",
                result.getMessage());
    }

    @Test
    public void testBuscarUsuarioPorEmailSucesso() {
        String usuarioEmail = "johndoe.example.com";
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", usuarioEmail, "password",
                "1234567890", TipoUsuario.COMUM, 0.0);
        UsuarioModel usuarioModel = UsuarioMapper.toModel(usuarioRecord);
        when(usuarioRepository.findByEmail(usuarioEmail)).thenReturn(Optional.of(usuarioModel));

        UsuarioRecord result = usuarioService.buscarUsuarioPorEmail(usuarioEmail);
        verifyNoMoreInteractions(usuarioRepository);
        assertNotNull(result);
        assertEquals(usuarioRecord, result);
    }

    @Test
    public void testBuscarUsuarioPorEmailErro() {
        String usuarioEmail = "johndoe.example.com";
        var result = assertThrows(NoSuchElementException.class, () -> {
            usuarioService.buscarUsuarioPorEmail(usuarioEmail);
        });
        Assertions.assertEquals("Usuário com email '" + usuarioEmail + "' não encontrado",
                result.getMessage());
    }

    @Test
    public void testBuscarUsuarioPorCpfCnpjSucesso() {
        String usuarioCpfCnpj = "133232323";
        UsuarioRecord usuarioRecord = new UsuarioRecord("John Doe", "johndoe.example.com",
                "password", usuarioCpfCnpj, TipoUsuario.COMUM, 0.0);
        UsuarioModel usuarioModel = UsuarioMapper.toModel(usuarioRecord);
        when(usuarioRepository.findByCpfCnpj(usuarioCpfCnpj)).thenReturn(Optional.of(usuarioModel));

        UsuarioRecord result = usuarioService.buscarUsuarioPorCpfCnpj(usuarioCpfCnpj);
        verifyNoMoreInteractions(usuarioRepository);
        assertNotNull(result);
        assertEquals(usuarioRecord, result);
    }

    @Test
    public void testBuscarUsuarioPorCpfCnpjErro() {
        String usuarioCpfCnpj = "133232323";
        var result = assertThrows(NoSuchElementException.class, () -> {
            usuarioService.buscarUsuarioPorCpfCnpj(usuarioCpfCnpj);
        });
        Assertions.assertEquals("Usuário com CPF/CNPJ '" + usuarioCpfCnpj + "' não encontrado",
                result.getMessage());
    }

    @Test
    public void testBuscarUsuarios() {
        // Arrange
        List<UsuarioModel> usuarioModels = new ArrayList<>();
        usuarioModels.add(new UsuarioModel("John", "Doe", "john.doe@example.com", "1554454",
                TipoUsuario.LOJISTA, 150.0));
        usuarioModels.add(new UsuarioModel("Jane", "Smith", "jane.smith@example.com", "232323",
                TipoUsuario.COMUM, 500.0));

        when(usuarioRepository.findAll()).thenReturn(usuarioModels);

        // Act
        List<UsuarioRecord> usuarioRecords = usuarioService.buscarUsuarios();

        // Assert
        assertNotNull(usuarioRecords);
        assertEquals(usuarioModels.size(), usuarioRecords.size());
        assertEquals(UsuarioMapper.toRecords(usuarioModels), usuarioRecords);
    }
}
