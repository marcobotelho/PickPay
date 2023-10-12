package com.marco.pickpay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transferencia")
public class TransferenciaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "remetenteId", nullable = false,
            foreignKey = @ForeignKey(name = "FK_TRANSFERENCIA_REMETENTE_ID"))
    private UsuarioModel remetente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatarioId", nullable = false,
            foreignKey = @ForeignKey(name = "FK_TRANSFERENCIA_DESTINATARIO_ID"))
    private UsuarioModel destinatario;

    private Double valor;

    public TransferenciaModel(UsuarioModel remetente, UsuarioModel destinatario, Double valor) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.valor = valor;
    }

    public TransferenciaModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioModel getRemetente() {
        return remetente;
    }

    public void setRemetente(UsuarioModel remetente) {
        this.remetente = remetente;
    }

    public UsuarioModel getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(UsuarioModel destinatario) {
        this.destinatario = destinatario;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
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
        TransferenciaModel other = (TransferenciaModel) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TransferenciaModel [id=" + id + ", remetente=" + remetente + ", destinatario="
                + destinatario + ", valor=" + valor + "]";
    }
}
