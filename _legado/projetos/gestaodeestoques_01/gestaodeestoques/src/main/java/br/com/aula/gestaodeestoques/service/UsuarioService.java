package br.com.aula.gestaodeestoques.service;

import br.com.aula.gestaodeestoques.dto.UsuarioDTO;
import br.com.aula.gestaodeestoques.dto.UsuarioFormDTO;
import java.util.List;

public interface UsuarioService {
    UsuarioDTO create(UsuarioFormDTO usuarioFormDTO);
    List<UsuarioDTO> findAll();
    UsuarioDTO findById(Long id);
    UsuarioDTO update(Long id, UsuarioFormDTO usuarioFormDTO);
    void delete(Long id);
}