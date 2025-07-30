package com.advocacia.api.domain.process;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "processo_judicial")
@NoArgsConstructor
@AllArgsConstructor
public class Process {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_processo")
    private String id;
    
    @Column(name = "numero_processo", nullable = false, unique = true)
    private String numeroProcesso;
    
    @Column(name = "tribunal", nullable = false)
    private String tribunal;
    
    @Column(name = "vara", nullable = false)
    private String vara;
    
    @Column(name = "cliente", nullable = false)
    private String cliente;
    
    @Column(name = "parte_contraria")
    private String parteContraria;
    
    @Column(name = "advogado_responsavel")
    private String advogadoResponsavel;
    
    @Column(name = "status_processo")
    @Enumerated(EnumType.STRING)
    private StatusProcess status;
    
    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    @Column(name = "valor_causa")
    private Double valorCausa;
    
    @Column(name = "tipo_acao")
    private String tipoAcao;
    
    @PrePersist
    protected void onCreate() {
        dataInicio = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (status == null) {
            status = StatusProcess.EM_ANDAMENTO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
} 