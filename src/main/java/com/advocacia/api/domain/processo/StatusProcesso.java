package com.advocacia.api.domain.processo;

public enum StatusProcesso {
    EM_ANDAMENTO("Em Andamento"),
    SUSPENSO("Suspenso"),
    ARQUIVADO("Arquivado"),
    FINALIZADO("Finalizado"),
    APELACAO("Apelação"),
    RECURSO("Recurso"),
    SENTENCA("Sentença"),
    EXECUCAO("Execução");
    
    private final String descricao;
    
    StatusProcesso(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
} 