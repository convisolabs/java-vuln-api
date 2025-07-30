package com.advocacia.api.domain.processo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoJudicialDTO {
    
    @NotBlank(message = "Número do processo é obrigatório")
    private String numeroProcesso;
    
    @NotBlank(message = "Tribunal é obrigatório")
    private String tribunal;
    
    @NotBlank(message = "Vara é obrigatória")
    private String vara;
    
    @NotBlank(message = "Cliente é obrigatório")
    private String cliente;
    
    private String parteContraria;
    
    private String advogadoResponsavel;
    
    private StatusProcesso status;
    
    private String observacoes;
    
    private Double valorCausa;
    
    private String tipoAcao;
} 