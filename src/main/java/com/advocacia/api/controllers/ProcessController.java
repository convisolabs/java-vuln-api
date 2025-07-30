package com.advocacia.api.controllers;

import com.advocacia.api.domain.process.Process;
import com.advocacia.api.domain.process.ProcessDTO;
import com.advocacia.api.domain.process.StatusProcess;
import com.advocacia.api.services.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/processos")
public class ProcessController {

    private final IProcessService processoService;

    @Autowired
    public ProcessController(IProcessService processoService) {
        this.processoService = processoService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarProcesso(@RequestBody @Valid ProcessDTO processoDTO) {
        try {
            Process processo = processoService.cadastrarProcesso(processoDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", processo.getId());
            response.put("numeroProcesso", processo.getNumeroProcesso());
            response.put("cliente", processo.getCliente());
            response.put("tribunal", processo.getTribunal());
            response.put("vara", processo.getVara());
            response.put("status", processo.getStatus());
            response.put("message", "Processo cadastrado com sucesso!");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        Optional<Process> processo = processoService.findById(id);
        if (processo.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(processo.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo não encontrado!");
    }

    @GetMapping("/numero/{numeroProcesso}")
    public ResponseEntity<Object> findByNumeroProcesso(@PathVariable String numeroProcesso) {
        Optional<Process> processo = processoService.findByNumeroProcesso(numeroProcesso);
        if (processo.isPresent()) {
            // VULNERABILIDADE XSS REFLETIDO - O número do processo é retornado sem sanitização
            Map<String, Object> response = new HashMap<>();
            response.put("id", processo.get().getId());
            response.put("numeroProcesso", numeroProcesso); // VULNERÁVEL: retorna o input do usuário sem sanitizar
            response.put("cliente", processo.get().getCliente());
            response.put("tribunal", processo.get().getTribunal());
            response.put("vara", processo.get().getVara());
            response.put("status", processo.get().getStatus());
            response.put("parteContraria", processo.get().getParteContraria());
            response.put("advogadoResponsavel", processo.get().getAdvogadoResponsavel());
            response.put("observacoes", processo.get().getObservacoes());
            response.put("valorCausa", processo.get().getValorCausa());
            response.put("tipoAcao", processo.get().getTipoAcao());
            response.put("dataInicio", processo.get().getDataInicio());
            response.put("dataAtualizacao", processo.get().getDataAtualizacao());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        // VULNERABILIDADE XSS REFLETIDO - Mensagem de erro também é vulnerável
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo com número '" + numeroProcesso + "' não encontrado!");
    }

    @GetMapping("/todos")
    public ResponseEntity<Object> findAll() {
        List<Process> processos = processoService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(processos);
    }

    @GetMapping("/cliente/{cliente}")
    public ResponseEntity<Object> findByCliente(@PathVariable String cliente) {
        List<Process> processos = processoService.findByCliente(cliente);
        return ResponseEntity.status(HttpStatus.OK).body(processos);
    }

    @GetMapping("/tribunal/{tribunal}")
    public ResponseEntity<Object> findByTribunal(@PathVariable String tribunal) {
        List<Process> processos = processoService.findByTribunal(tribunal);
        return ResponseEntity.status(HttpStatus.OK).body(processos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> findByStatus(@PathVariable String status) {
        try {
            StatusProcess statusProcesso = StatusProcess.valueOf(status.toUpperCase());
            List<Process> processos = processoService.findByStatus(statusProcesso);
            return ResponseEntity.status(HttpStatus.OK).body(processos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status inválido!");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Object> searchByTermo(@RequestParam String termo) {
        List<Process> processos = processoService.searchByTermo(termo);
        return ResponseEntity.status(HttpStatus.OK).body(processos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProcesso(@PathVariable String id, @RequestBody @Valid ProcessDTO processoDTO) {
        try {
            Process processo = processoService.updateProcesso(id, processoDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", processo.getId());
            response.put("numeroProcesso", processo.getNumeroProcesso());
            response.put("cliente", processo.getCliente());
            response.put("tribunal", processo.getTribunal());
            response.put("vara", processo.getVara());
            response.put("status", processo.getStatus());
            response.put("message", "Processo atualizado com sucesso!");
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable String id) {
        try {
            processoService.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Processo deletado com sucesso!");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/status-disponiveis")
    public ResponseEntity<Object> getStatusDisponiveis() {
        StatusProcess[] status = StatusProcess.values();
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
} 