package com.marco.pickpay.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import com.marco.pickpay.records.TransferenciaRecord;
import com.marco.pickpay.records.UsuarioRecord;
import com.marco.pickpay.repositories.TransferenciaRepository;
import com.marco.pickpay.repositories.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TrasferenciaControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private TransferenciaRepository transferenciaRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private UsuarioRecord remetenteRecord;

        private UsuarioRecord destinatarioRecord;

        private TransferenciaRecord transferenciaRecord;

        private UsuarioModel remetenteModel;

        private UsuarioModel destinatarioModel;

        private String url = "/api/transferencias";

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);

                remetenteRecord = new UsuarioRecord(null, "Nome Remetente", "remetente@email.com",
                                "senharemetente", "111111111", TipoUsuario.COMUM, 150.0); 

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
                transferenciaRepository.deleteById(transferenciaRecord.id());
                usuarioRepository.deleteById(remetenteModel.getId());
                usuarioRepository.deleteById(destinatarioModel.getId());
        }

        @Test
        public void testTransferirValor() throws Exception {

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("UTF-8")
                                                .content(objectMapper.writeValueAsString(
                                                                transferenciaRecord)))
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.remetenteId")
                                                .value(transferenciaRecord.remetenteId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.destinatarioId")
                                                .value(transferenciaRecord.destinatarioId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.valor")
                                                .value(transferenciaRecord.valor()))
                                .andDo(MockMvcResultHandlers.print()).andReturn();

                String responseContent = result.getResponse().getContentAsString();
                JsonNode jsonResponse = objectMapper.readTree(responseContent);
                transferenciaRecord = new TransferenciaRecord(jsonResponse.get("id").asLong(),
                                jsonResponse.get("remetenteId").asLong(),
                                jsonResponse.get("destinatarioId").asLong(),
                                jsonResponse.get("valor").asDouble());
        }
}
