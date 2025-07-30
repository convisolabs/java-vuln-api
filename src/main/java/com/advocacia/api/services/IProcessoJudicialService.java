package com.advocacia.api.services;

import com.advocacia.api.domain.processo.ProcessoJudicial;
import com.advocacia.api.domain.processo.ProcessoJudicialDTO;

import java.util.List;
import java.util.Optional;

public interface IProcessoJudicialService {
    
    ProcessoJudicial cadastrarProcesso(ProcessoJudicialDTO processoDTO);
    
    Optional<ProcessoJudicial> findById(String id);
    
    Optional<ProcessoJudicial> findByNumeroProcesso(String numeroProcesso);
    
    List<ProcessoJudicial> findAll();
    
    List<ProcessoJudicial> findByCliente(String cliente);
    
    List<ProcessoJudicial> findByTribunal(String tribunal);
    
    List<ProcessoJudicial> findByStatus(com.advocacia.api.domain.processo.StatusProcesso status);
    
    List<ProcessoJudicial> searchByTermo(String termo);
    
    ProcessoJudicial updateProcesso(String id, ProcessoJudicialDTO processoDTO);
    
    void deleteById(String id);
    
    boolean existsByNumeroProcesso(String numeroProcesso);
} 