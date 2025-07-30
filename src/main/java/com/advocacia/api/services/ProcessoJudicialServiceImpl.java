package com.advocacia.api.services;

import com.advocacia.api.domain.processo.ProcessoJudicial;
import com.advocacia.api.domain.processo.ProcessoJudicialDTO;
import com.advocacia.api.domain.processo.StatusProcesso;
import com.advocacia.api.repositories.ProcessoJudicialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessoJudicialServiceImpl implements IProcessoJudicialService {
    
    private final ProcessoJudicialRepository processoRepository;
    
    @Autowired
    public ProcessoJudicialServiceImpl(ProcessoJudicialRepository processoRepository) {
        this.processoRepository = processoRepository;
    }
    
    @Override
    public ProcessoJudicial cadastrarProcesso(ProcessoJudicialDTO processoDTO) {
        // Verificar se já existe um processo com o mesmo número
        if (processoRepository.existsByNumeroProcesso(processoDTO.getNumeroProcesso())) {
            throw new RuntimeException("Já existe um processo com o número: " + processoDTO.getNumeroProcesso());
        }
        
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setNumeroProcesso(processoDTO.getNumeroProcesso());
        processo.setTribunal(processoDTO.getTribunal());
        processo.setVara(processoDTO.getVara());
        processo.setCliente(processoDTO.getCliente());
        processo.setParteContraria(processoDTO.getParteContraria());
        processo.setAdvogadoResponsavel(processoDTO.getAdvogadoResponsavel());
        processo.setStatus(processoDTO.getStatus() != null ? processoDTO.getStatus() : StatusProcesso.EM_ANDAMENTO);
        processo.setObservacoes(processoDTO.getObservacoes());
        processo.setValorCausa(processoDTO.getValorCausa());
        processo.setTipoAcao(processoDTO.getTipoAcao());
        
        return processoRepository.save(processo);
    }
    
    @Override
    public Optional<ProcessoJudicial> findById(String id) {
        return processoRepository.findById(id);
    }
    
    @Override
    public Optional<ProcessoJudicial> findByNumeroProcesso(String numeroProcesso) {
        return processoRepository.findByNumeroProcesso(numeroProcesso);
    }
    
    @Override
    public List<ProcessoJudicial> findAll() {
        return processoRepository.findAll();
    }
    
    @Override
    public List<ProcessoJudicial> findByCliente(String cliente) {
        return processoRepository.findByClienteContainingIgnoreCase(cliente);
    }
    
    @Override
    public List<ProcessoJudicial> findByTribunal(String tribunal) {
        return processoRepository.findByTribunal(tribunal);
    }
    
    @Override
    public List<ProcessoJudicial> findByStatus(StatusProcesso status) {
        return processoRepository.findByStatus(status);
    }
    
    @Override
    public List<ProcessoJudicial> searchByTermo(String termo) {
        return processoRepository.findByNumeroProcessoOrClienteOrParteContrariaContaining(termo);
    }
    
    @Override
    public ProcessoJudicial updateProcesso(String id, ProcessoJudicialDTO processoDTO) {
        Optional<ProcessoJudicial> processoExistente = processoRepository.findById(id);
        if (processoExistente.isEmpty()) {
            throw new RuntimeException("Processo não encontrado com o ID: " + id);
        }
        
        ProcessoJudicial processo = processoExistente.get();
        
        // Verificar se o novo número de processo já existe em outro registro
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