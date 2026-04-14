package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.Block;
import com.desafiodunnas.sgcc.repository.BlockRepository;
import com.desafiodunnas.sgcc.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST exclusivo para fornecer dados de forma assíncrona (JSON)
 * para os menus em cascata das views do sistema.
 */
@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
public class UnitApiController {

    private final UnitRepository unitRepository;
    private final BlockRepository blockRepository;

    public record UnitDTO(Long id, String identifier) {}

    /**
     * Busca unidades pertencentes a um bloco e um andar específicos,
     * ignorando nomenclaturas fora do padrão estabelecido.
     *
     * @param blocoId ID do bloco selecionado.
     * @param andar   Número do andar selecionado.
     * @return Retorna uma lista de unidades no formato JSON.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarUnidadesPorAndar(@RequestParam Long blocoId, @RequestParam Integer andar) {
        try {
            System.out.println("Buscando unidades - ID do bloco: " + blocoId + " | Andar: " + andar);

            Block block = blockRepository.findById(blocoId)
                    .orElseThrow(() -> new IllegalArgumentException("Bloco não encontrado"));

            List<UnitDTO> unidades = unitRepository.findByBlockId(blocoId).stream()
                    .filter(unit -> {
                        try {
                            String sufixo = unit.getIdentifier().substring(block.getIdentifier().length());
                            return sufixo.startsWith("-" + andar + "-");
                        } catch (Exception ex) {
                            return false;
                        }
                    })
                    .map(unit -> new UnitDTO(unit.getId(), unit.getIdentifier()))
                    .collect(Collectors.toList());

            System.out.println("Unidades encontradas: " + unidades.size());
            return ResponseEntity.ok(unidades);

        } catch (Exception e) {
            System.err.println("Erro interno na API de unidades: " + e.getMessage());
            e.printStackTrace(System.err);
            return ResponseEntity.internalServerError().body("Erro ao processar a busca no servidor.");
        }
    }
}