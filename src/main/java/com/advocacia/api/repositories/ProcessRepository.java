package com.advocacia.api.repositories;

import com.advocacia.api.domain.process.Process;
import com.advocacia.api.domain.process.StatusProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends JpaRepository<Process, String> {
    
    Optional<Process> findByNumeroProcesso(String numeroProcesso);
    
    boolean existsByNumeroProcesso(String numeroProcesso);
    
    List<Process> findByClienteContainingIgnoreCase(String cliente);
    
    List<Process> findByTribunal(String tribunal);
    
    List<Process> findByStatus(StatusProcess status);
    
    @Query("SELECT p FROM Process p WHERE p.numeroProcesso LIKE %?1% OR p.cliente LIKE %?1% OR p.parteContraria LIKE %?1%")
    List<Process> findByNumeroProcessoOrClienteOrParteContrariaContaining(String termo);
} 