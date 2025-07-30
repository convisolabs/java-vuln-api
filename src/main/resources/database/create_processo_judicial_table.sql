-- Script para criar a tabela processo_judicial
-- Execute este script no banco de dados SQLite

CREATE TABLE IF NOT EXISTS processo_judicial (
    id_processo VARCHAR(255) PRIMARY KEY,
    numero_processo VARCHAR(255) NOT NULL UNIQUE,
    tribunal VARCHAR(255) NOT NULL,
    vara VARCHAR(255) NOT NULL,
    cliente VARCHAR(255) NOT NULL,
    parte_contraria VARCHAR(255),
    advogado_responsavel VARCHAR(255),
    status_processo VARCHAR(50),
    data_inicio DATETIME,
    data_atualizacao DATETIME,
    observacoes TEXT,
    valor_causa DECIMAL(15,2),
    tipo_acao VARCHAR(255)
);

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_processo_numero ON processo_judicial(numero_processo);
CREATE INDEX IF NOT EXISTS idx_processo_cliente ON processo_judicial(cliente);
CREATE INDEX IF NOT EXISTS idx_processo_tribunal ON processo_judicial(tribunal);
CREATE INDEX IF NOT EXISTS idx_processo_status ON processo_judicial(status_processo); 