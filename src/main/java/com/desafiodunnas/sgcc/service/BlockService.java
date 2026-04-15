package com.desafiodunnas.sgcc.service;

import com.desafiodunnas.sgcc.domain.Block;
import com.desafiodunnas.sgcc.domain.Unit;
import com.desafiodunnas.sgcc.repository.BlockRepository;
import com.desafiodunnas.sgcc.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por gerenciar a criação e modificação da infraestrutura física.
 */
@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final UnitRepository unitRepository;

    /**
     * Cria um novo bloco e gera automaticamente todas as unidades habitacionais
     * contidas nele com base na matriz de andares e apartamentos.
     * Utiliza inserção em lote para alto desempenho no banco, além de processamento assíncrono para não bloquear a thread principal.
     * Também limita a quantidade de andares e apartamentos para evitar sobrecarga de memória e garantir a viabilidade do processo.
     */
    @Async
    @Transactional
    public void createBlock(Block block) {
        try {
            if (block.getTotalFloors() < 1 || block.getTotalFloors() > 1000) {
                throw new IllegalArgumentException("O número de andares deve ser entre 1 e 1000.");
            }
            if (block.getApartmentsPerFloor() < 1 || block.getApartmentsPerFloor() > 10000) {
                throw new IllegalArgumentException("O número de apartamentos por andar deve ser entre 1 e 10000.");
            }

            System.out.println("Iniciando geração automática de unidades em segundo plano...");

            Block savedBlock = blockRepository.save(block);
            List<Unit> unitsToSave = new ArrayList<>();

            for (int floor = 1; floor <= savedBlock.getTotalFloors(); floor++) {
                for (int apt = 1; apt <= savedBlock.getApartmentsPerFloor(); apt++) {

                    String unitIdentifier = savedBlock.getIdentifier() + "-" + floor + "-" + String.format("%02d", apt);

                    Unit unit = Unit.builder()
                            .identifier(unitIdentifier)
                            .block(savedBlock)
                            .build();

                    unitsToSave.add(unit);
                }

                System.out.println("Geradas " + (floor * savedBlock.getApartmentsPerFloor()) + " unidades até o momento.");
                System.out.println("Restante: " + ((savedBlock.getTotalFloors() - floor) * savedBlock.getApartmentsPerFloor()) + " unidades.");
            }

            unitRepository.saveAll(unitsToSave);
            savedBlock.setUnits(unitsToSave);

            System.out.println("Geração de unidades concluída");

        } catch (Exception e) {
            System.err.println("Erro ao criar o bloco e gerar as unidades em lote");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na criação do bloco", e);
        }
    }

    public List<Block> findAllBlocks() {
        try {
            return blockRepository.findAll();
        } catch (Exception e) {
            System.err.println("Erro ao buscar a lista de blocos");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na busca de blocos", e);
        }
    }

    public List<Block> searchBlocks(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return blockRepository.findAll();
            }
            return blockRepository.findByIdentifierContainingIgnoreCase(keyword.trim());
        } catch (Exception e) {
            System.err.println("Erro ao pesquisar blocos");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na pesquisa de blocos", e);
        }
    }

    public Block findBlockById(Long id) {
        try {
            return blockRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado"));
        } catch (Exception e) {
            System.err.println("Erro ao buscar o bloco pelo ID");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na busca do bloco", e);
        }
    }

    /**
     * Atualiza as informações de um bloco existente.
     * Bloqueia a alteração d andares e unidades para manter a integridade,
     * mas permite a renomeação do bloco, propagando em cascata a nova nomenclatura
     * para todas as unidades contidas nele.
     */
    @Transactional
    public void updateBlock(Long id, Block updatedBlock) {
        try {
            Block existing = blockRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado"));

            if (!existing.getTotalFloors().equals(updatedBlock.getTotalFloors()) ||
                    !existing.getApartmentsPerFloor().equals(updatedBlock.getApartmentsPerFloor())) {
                throw new IllegalStateException(
                        "Não é permitido alterar a estrutura de andares/unidades de um bloco já criado.");
            }

            if (!existing.getIdentifier().equals(updatedBlock.getIdentifier())) {
                String oldPrefix = existing.getIdentifier() + "-";
                String newPrefix = updatedBlock.getIdentifier() + "-";

                existing.setIdentifier(updatedBlock.getIdentifier());

                for (Unit unit : existing.getUnits()) {
                    unit.setIdentifier(unit.getIdentifier().replaceFirst(oldPrefix, newPrefix));
                }

                unitRepository.saveAll(existing.getUnits());
            }

            blockRepository.save(existing);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o bloco");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na atualização do bloco: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteBlock(Long id) {
        try {
            blockRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Erro ao excluir o bloco");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha ao excluir. " +
                    "Verifique se o bloco possui moradores ou chamados vinculados a ele.", e);
        }
    }
}