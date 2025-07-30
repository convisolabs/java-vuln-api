package com.advocacia.api.services;

import com.advocacia.api.domain.process.Process;
import com.advocacia.api.domain.process.ProcessDTO;
import com.advocacia.api.domain.process.StatusProcess;
import com.advocacia.api.repositories.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessServiceImpl implements IProcessService {
    
    private final ProcessRepository processoRepository;
    
    @Autowired
    public ProcessServiceImpl(ProcessRepository processoRepository) {
        this.processoRepository = processoRepository;
    }
    
    @Override
    public Process cadastrarProcesso(ProcessDTO processoDTO) {
        // Verificar se já existe um processo com o mesmo número
        if (processoRepository.existsByNumeroProcesso(processoDTO.getNumeroProcesso())) {
            throw new RuntimeException("Já existe um processo com o número: " + processoDTO.getNumeroProcesso());
        }
        
        Process processo = new Process();
        processo.setNumeroProcesso(processoDTO.getNumeroProcesso());
        processo.setTribunal(processoDTO.getTribunal());
        processo.setVara(processoDTO.getVara());
        processo.setCliente(processoDTO.getCliente());
        processo.setParteContraria(processoDTO.getParteContraria());
        processo.setAdvogadoResponsavel(processoDTO.getAdvogadoResponsavel());
        processo.setStatus(processoDTO.getStatus() != null ? processoDTO.getStatus() : StatusProcess.EM_ANDAMENTO);
        processo.setObservacoes(processoDTO.getObservacoes());
        processo.setValorCausa(processoDTO.getValorCausa());
        processo.setTipoAcao(processoDTO.getTipoAcao());
        
        return processoRepository.save(processo);
    }
    
    @Override
    public Optional<Process> findById(String id) {
        return processoRepository.findById(id);
    }
    
    @Override
    public Optional<Process> findByNumeroProcesso(String numeroProcesso) {
        return processoRepository.findByNumeroProcesso(numeroProcesso);
    }
    
    @Override
    public List<Process> findAll() {
        return processoRepository.findAll();
    }
    
    @Override
    public List<Process> findByCliente(String cliente) {
        return processoRepository.findByClienteContainingIgnoreCase(cliente);
    }
    
    @Override
    public List<Process> findByTribunal(String tribunal) {
        return processoRepository.findByTribunal(tribunal);
    }
    
    @Override
    public List<Process> findByStatus(StatusProcess status) {
        return processoRepository.findByStatus(status);
    }
    
    @Override
    public List<Process> searchByTermo(String termo) {
        return processoRepository.findByNumeroProcessoOrClienteOrParteContrariaContaining(termo);
    }
    
    @Override
    public Process updateProcesso(String id, ProcessDTO processoDTO) {
        Optional<Process> processoExistente = processoRepository.findById(id);
        if (processoExistente.isEmpty()) {
            throw new RuntimeException("Processo não encontrado com o ID: " + id);
        }
        
        Process processo = processoExistente.get();
        
        if (!processo.getNumeroProcesso().equals(processoDTO.getNumeroProcesso()) && 
            processoRepository.existsByNumeroProcesso(processoDTO.getNumeroProcesso())) {
            throw new RuntimeException("Já existe um processo com o número: " + processoDTO.getNumeroProcesso());
        }
        
        processo.setNumeroProcesso(processoDTO.getNumeroProcesso());
        processo.setTribunal(processoDTO.getTribunal());
        processo.setVara(processoDTO.getVara());
        processo.setCliente(processoDTO.getCliente());
        processo.setParteContraria(processoDTO.getParteContraria());
        processo.setAdvogadoResponsavel(processoDTO.getAdvogadoResponsavel());
        processo.setStatus(processoDTO.getStatus() != null ? processoDTO.getStatus() : processo.getStatus());
        processo.setObservacoes(processoDTO.getObservacoes());
        processo.setValorCausa(processoDTO.getValorCausa());
        processo.setTipoAcao(processoDTO.getTipoAcao());
        
        return processoRepository.save(processo);
    }
    
    @Override
    public void deleteById(String id) {
        if (!processoRepository.existsById(id)) {
            throw new RuntimeException("Processo não encontrado com o ID: " + id);
        }
        processoRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByNumeroProcesso(String numeroProcesso) {
        return processoRepository.existsByNumeroProcesso(numeroProcesso);
    }
} 