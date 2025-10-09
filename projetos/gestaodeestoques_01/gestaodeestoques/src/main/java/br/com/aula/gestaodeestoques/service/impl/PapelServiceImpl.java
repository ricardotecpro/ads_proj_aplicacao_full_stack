package br.com.aula.gestaodeestoques.service.impl;

import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.repository.PapelRepository;
import br.com.aula.gestaodeestoques.service.PapelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PapelServiceImpl implements PapelService {

    private final PapelRepository papelRepository;

    public PapelServiceImpl(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Papel> findAll() {
        // Converte o Iterable retornado pelo findAll() para uma List
        return StreamSupport.stream(papelRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}