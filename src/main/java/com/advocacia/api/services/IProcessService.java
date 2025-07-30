package com.advocacia.api.services;

import com.advocacia.api.domain.process.Process;
import com.advocacia.api.domain.process.ProcessDTO;
import com.advocacia.api.domain.process.StatusProcess;

import java.util.List;
import java.util.Optional;

public interface IProcessService {
    
    Process cadastrarProcesso(ProcessDTO processoDTO);
    
    Optional<Process> findById(String id);
    
    Optional<Process> findByNumeroProcesso(String numeroProcesso);
    
    List<Process> findAll();
    
    List<Process> findByCliente(String cliente);
    
    List<Process> findByTribunal(String tribunal);
    
    List<Process> findByStatus(StatusProcess status);
    
    List<Process> searchByTermo(String termo);
    
    Process updateProcesso(String id, ProcessDTO processoDTO);
    
    void deleteById(String id);
    
    boolean existsByNumeroProcesso(String numeroProcesso);
} 