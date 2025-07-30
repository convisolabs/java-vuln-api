package com.advocacia.api.repositories;

import com.advocacia.api.domain.processo.ProcessoJudicial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoJudicialRepository extends JpaRepository<ProcessoJudicial, String> {
    
    Optional<ProcessoJudicial> findByNumeroProcesso(String numeroProcesso);
    
    boolean existsByNumeroProcesso(String numeroProcesso);
    
    List<ProcessoJudicial> findByClienteContainingIgnoreCase(String cliente);
    
    List<ProcessoJudicial> findByTribunal(String tribunal);
    
    List<ProcessoJudicial> findByStatus(com.advocacia.api.domain.processo.StatusProcesso status);
    
    @Query("SELECT p FROM ProcessoJudicial p WHERE p.numeroProcesso LIKE %?1% OR p.cliente LIKE %?1% OR p.parteContraria LIKE %?1%")
    List<ProcessoJudicial> findByNumeroProcessoOrClienteOrParteContrariaContaining(String termo);
} 