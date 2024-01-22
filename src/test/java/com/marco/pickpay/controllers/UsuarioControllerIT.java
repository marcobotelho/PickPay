package com.marco.pickpay.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marco.pickpay.enums.TipoUsuario;
import com.marco.pickpay.mappers.UsuarioMapper;
import com.marco.pickpay.models.UsuarioModel;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.repositories.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        private UsuarioRecord usuarioRecord;

        private UsuarioModel usuarioModel;

        private String url = "/api/usuarios";

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);

                usuarioRecord = new UsuarioRecord(null, "Nome Teste Integracao",
                                "nometesteintegracao@email.com", "senhatesteintegracao",
                                "111111111", TipoUsuario.COMUM, 50.0);

                usuarioModel = usuarioRepository.save(UsuarioMapper.toModel(usuarioRecord));
                
                usuarioRecord = UsuarioMapper.toRecord(usuarioModel);
        }

        @AfterEach
        public void tearDown() {
                usuarioRepository.deleteById(usuarioModel.getId());
        }

        @Test
        public void testCreateUsuario() throws Exception {
                UsuarioRecord usuarioRecordNovo = new UsuarioRecord(null, "nome3", "nome3@example.com",
                                "senhanome3", "333", TipoUsuario.COMUM, 0.0);

                mockMvc.perform(MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(usuarioRecordNovo)))
                                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

                UsuarioModel usuarioModelNovo =
                                usuarioRepository.findByEmail(usuarioRecordNovo.email()).get();

                url = url.concat("/" + usuarioModelNovo.getId());
                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.nome")
                                                .value(usuarioRecordNovo.nome()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                                                .value(usuarioRecordNovo.email()))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.senha").value(
                                // passwordEncoder.encode(usuarioRecordNovo.senha())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.cpfCnpj")
                                                .value(usuarioRecordNovo.cpfCnpj()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.tipoUsuario")
                                                .value(usuarioRecordNovo.tipoUsuario().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo")
                                                .value(usuarioRecordNovo.saldo()))
                                .andDo(MockMvcResultHandlers.print()).andReturn();

                String responseContent = result.getResponse().getContentAsString();
                JsonNode jsonResponse = objectMapper.readTree(responseContent);
                String encryptedPassword = jsonResponse.get("senha").asText();

                // Verify if the raw password matches the encrypted password
                boolean passwordMatches = passwordEncoder.matches(usuarioRecordNovo.senha(),
                                encryptedPassword);

                // Assert the result of the password match
                assertTrue(passwordMatches);

                usuarioRepository.deleteById(usuarioModelNovo.getId());
        }

        @Test
        public void testCreateUsuario_QdoSenhaNula_RuntimeException() throws Exception {
                UsuarioRecord usuarioRecordNovo = new UsuarioRecord(null, "nome3", "nome3@example.com",
                                null, "333", TipoUsuario.COMUM, 0.0);

                mockMvc.perform(MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(usuarioRecordNovo)))
                                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem")
                                                .value("Senha nula não é valida"))
                                .andReturn();


        }

        @Test
        public void testUpdateUsuario() throws Exception {
                url = url.concat("/" + usuarioModel.getId());

                UsuarioRecord usuarioRecordUpdate =
                                new UsuarioRecord(1L, "nome22", "nome22@example.com", "senhanome22",
                                                "2222222", TipoUsuario.COMUM, 0.0);

                mockMvc.perform(MockMvcRequestBuilders.put(url)
                                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(usuarioRecordUpdate)))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andDo(MockMvcResultHandlers.print());

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.nome")
                                                .value(usuarioRecordUpdate.nome()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                                                .value(usuarioRecordUpdate.email()))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.senha").value(
                                // passwordEncoder.encode(usuarioRecordUpdate.senha())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.cpfCnpj")
                                                .value(usuarioRecordUpdate.cpfCnpj()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.tipoUsuario").value(
                                                usuarioRecordUpdate.tipoUsuario().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo")
                                                .value(usuarioRecordUpdate.saldo()))
                                .andDo(MockMvcResultHandlers.print()).andReturn();

                String responseContent = result.getResponse().getContentAsString();
                JsonNode jsonResponse = objectMapper.readTree(responseContent);
                String encryptedPassword = jsonResponse.get("senha").asText();

                // Verify if the raw password matches the encrypted password
                boolean passwordMatches = passwordEncoder.matches(usuarioRecordUpdate.senha(),
                                encryptedPassword);

                // Assert the result of the password match
                assertTrue(passwordMatches);

                usuarioRepository.deleteById(usuarioModel.getId());
        }

        @Test
        public void testUpdateUsuario_QdoSenhaNula_RuntimeException() throws Exception {
                url = url.concat("/" + usuarioModel.getId());

                UsuarioRecord usuarioRecordUpdate = new UsuarioRecord(1L, "nome22",
                                "nome22@example.com", null, "2222222", TipoUsuario.COMUM, 0.0);

                mockMvc.perform(MockMvcRequestBuilders.put(url)
                                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(usuarioRecordUpdate)))
                                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem")
                                                .value("Senha nula não é valida"))
                                .andDo(MockMvcResultHandlers.print());

        }

        @Test
        public void testUpdateUsuario_QdoIdNaoExiste_RuntimeException() throws Exception {
                Long usuarioId = 0L;
                url = url.concat("/" + usuarioId);

                UsuarioRecord usuarioRecordUpdate =
                                new UsuarioRecord(1L, "nome22", "nome22@example.com", "senhanome22",
                                                "2222222", TipoUsuario.COMUM, 0.0);

                mockMvc.perform(MockMvcRequestBuilders.put(url)
                                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(usuarioRecordUpdate)))
                                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem")
                                                .value("Usuário com id '" + usuarioId
                                                                + "' não encontrado"))
                                .andDo(MockMvcResultHandlers.print());

        }

        @Test
        public void testGetUsuario() throws Exception {
                url = url.concat("/" + usuarioModel.getId());
                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.content().json(
                                                objectMapper.writeValueAsString(usuarioRecord)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.nome")
                                                .value(usuarioRecord.nome()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                                                .value(usuarioRecord.email()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.senha")
                                                .value(usuarioRecord.senha()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.cpfCnpj")
                                                .value(usuarioRecord.cpfCnpj()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.tipoUsuario")
                                                .value(usuarioRecord.tipoUsuario().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo")
                                                .value(usuarioRecord.saldo()))
                                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void testGetUsuarios() throws Exception {
                List<UsuarioModel> usuarioModels = usuarioRepository.findAll();
                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                                                .value(usuarioModels.size()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome")
                                                .value(usuarioModels.get(0).getNome()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email")
                                                .value(usuarioModels.get(0).getEmail()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].senha")
                                                .value(usuarioModels.get(0).getSenha()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cpfCnpj")
                                                .value(usuarioModels.get(0).getCpfCnpj()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tipoUsuario").value(
                                                usuarioModels.get(0).getTipoUsuario().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].saldo")
                                                .value(usuarioModels.get(0).getSaldo()))
                                .andDo(MockMvcResultHandlers.print());

        }


        @Test
        public void testGetUsuarioPorEmail() throws Exception {
                url = url.concat("/email/" + usuarioRecord.email());
                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.content().json(
                                                objectMapper.writeValueAsString(usuarioRecord)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.nome")
                                                .value(usuarioRecord.nome()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                                                .value(usuarioRecord.email()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.senha")
                                                .value(usuarioRecord.senha()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.cpfCnpj")
                                                .value(usuarioRecord.cpfCnpj()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.tipoUsuario")
                                                .value(usuarioRecord.tipoUsuario().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo")
                                                .value(usuarioRecord.saldo()))
                                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void testGetUsuarioPorEmail_QdoNaoEncontrado_NoSuchElementException()
                        throws Exception {
                String usuarioEmail = "emailnaoexiste@teste.teste";
                url = url.concat("/email/" + usuarioEmail);
                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem")
                                                .value("Usuário com email '" + usuarioEmail
                                                                + "' não encontrado"))
                                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void testGetUsuarioPorCpfCnpj() throws Exception {
                url = url.concat("/cpf-cnpj/" + usuarioRecord.cpfCnpj());
                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.content().json(
                                                objectMapper.writeValueAsString(usuarioRecord)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.nome")
                                                .value(usuarioRecord.nome()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                                                .value(usuarioRecord.email()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.senha")
                                                .value(usuarioRecord.senha()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.cpfCnpj")
                                                .value(usuarioRecord.cpfCnpj()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.tipoUsuario")
                                                .value(usuarioRecord.tipoUsuario().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo")
                                                .value(usuarioRecord.saldo()))
                                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void testGetUsuarioPorCpfCnpj_QdoNaoEncontrado_NoSuchElementException()
                        throws Exception {
                String usuarioCpfCnpj = "emailnaoexiste@teste.teste";
                url = url.concat("/cpf-cnpj/" + usuarioCpfCnpj);
                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.content()
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem")
                                                .value("Usuário com CPF/CNPJ '" + usuarioCpfCnpj
                                                                + "' não encontrado"))
                                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void testDeleteUsuario() throws Exception {
                Long usuarioId = usuarioModel.getId();
                url = url.concat("/" + usuarioId);

                mockMvc.perform(MockMvcRequestBuilders.delete(url))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                mockMvc.perform(MockMvcRequestBuilders.get(url))
                                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem")
                                                .value("Usuário com id '" + usuarioId
                                                                + "' não encontrado"));
        }
}
