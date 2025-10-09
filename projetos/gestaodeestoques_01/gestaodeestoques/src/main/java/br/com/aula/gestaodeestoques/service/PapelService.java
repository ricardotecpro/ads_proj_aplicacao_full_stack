package br.com.aula.gestaodeestoques.service;

import br.com.aula.gestaodeestoques.model.Papel;
import java.util.List;

public interface PapelService {
    List<Papel> findAll();
}