package com.advocacia.api.domain.process;

public enum StatusProcess {
    EM_ANDAMENTO("Em Andamento"),
    SUSPENSO("Suspenso"),
    ARQUIVADO("Arquivado"),
    FINALIZADO("Finalizado"),
    APELACAO("Apelação"),
    RECURSO("Recurso"),
    SENTENCA("Sentença"),
    EXECUCAO("Execução");
    
    private final String descricao;
    
    StatusProcess(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
} 