package com.marco.pickpay.models;

import com.marco.pickpay.enums.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "usuario")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "O nome deve ser informado")
    private String nome;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "O email deve ser informado")
    @Email(message = "O email deve ser válido")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "A senha deve ser informada")
    private String senha;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "O cpf/cnpj deve ser informado")
    private String cpfCnpj;

    @Column(nullable = false)
    @NotNull(message = "O tipo de usuario deve ser informado")
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @Column(nullable = false)
    @NotNull(message = "O saldo deve ser informado")
    private Double saldo;

    public UsuarioModel(Long id, String nome, String email, String senha, String cpfCnpj,
            TipoUsuario tipoUsuario, Double saldo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpfCnpj = cpfCnpj;
        this.tipoUsuario = tipoUsuario;
        this.saldo = saldo;
    }

    public UsuarioModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "UsuarioModel [id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha
                + ", cpfCnpj=" + cpfCnpj + ", tipoUsuario=" + tipoUsuario + ", saldo=" + saldo
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UsuarioModel other = (UsuarioModel) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
