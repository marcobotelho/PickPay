package com.marco.pickpay.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.marco.pickpay.models.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByEmail(String email);

    Optional<UsuarioModel> findByCpfCnpj(String cpfCnpj);

}
