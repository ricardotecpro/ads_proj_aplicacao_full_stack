package br.com.aula.gestaodeestoques.service.impl;

import br.com.aula.gestaodeestoques.dto.UsuarioDTO;
import br.com.aula.gestaodeestoques.dto.UsuarioFormDTO;
import br.com.aula.gestaodeestoques.exception.ResourceNotFoundException;
import br.com.aula.gestaodeestoques.mapper.UsuarioMapper;
import br.com.aula.gestaodeestoques.model.Usuario;
import br.com.aula.gestaodeestoques.repository.PapelRepository;
import br.com.aula.gestaodeestoques.repository.UsuarioRepository;
import br.com.aula.gestaodeestoques.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PapelRepository papelRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioDTO create(UsuarioFormDTO formDTO) {
        // Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(formDTO.senha());
        Usuario usuario = usuarioMapper.toEntity(formDTO, senhaCriptografada);

        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Limpa papéis antigos (não aplicável na criação) e adiciona os novos
        usuarioRepository.limparPapeis(savedUsuario.id());
        formDTO.papeisIds().forEach(papelId -> {
            papelRepository.findById(papelId).orElseThrow(() -> new ResourceNotFoundException("Papel não encontrado: " + papelId));
            usuarioRepository.adicionarPapel(savedUsuario.id(), papelId);
        });

        return usuarioMapper.toDTO(savedUsuario, usuarioRepository.findPapeisByUsuarioId(savedUsuario.id()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return StreamSupport.stream(usuarioRepository.findAll().spliterator(), false)
                .map(user -> usuarioMapper.toDTO(user, usuarioRepository.findPapeisByUsuarioId(user.id())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id) {
        Usuario usuario = findUsuarioOrThrow(id);
        return usuarioMapper.toDTO(usuario, usuarioRepository.findPapeisByUsuarioId(id));
    }

    @Override
    @Transactional
    public UsuarioDTO update(Long id, UsuarioFormDTO formDTO) {
        Usuario usuarioExistente = findUsuarioOrThrow(id);

        String senha = usuarioExistente.senha();
        // Atualiza a senha apenas se uma nova for fornecida
        if (StringUtils.hasText(formDTO.senha())) {
            senha = passwordEncoder.encode(formDTO.senha());
        }

        Usuario usuarioParaAtualizar = new Usuario(id, formDTO.login(), senha, formDTO.ativo());
        Usuario savedUsuario = usuarioRepository.save(usuarioParaAtualizar);

        // Limpa os papéis antigos e adiciona os novos selecionados
        usuarioRepository.limparPapeis(savedUsuario.id());
        formDTO.papeisIds().forEach(papelId -> {
            papelRepository.findById(papelId).orElseThrow(() -> new ResourceNotFoundException("Papel não encontrado: " + papelId));
            usuarioRepository.adicionarPapel(savedUsuario.id(), papelId);
        });

        return usuarioMapper.toDTO(savedUsuario, usuarioRepository.findPapeisByUsuarioId(savedUsuario.id()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findUsuarioOrThrow(id);
        // Primeiro remove as associações na tabela de junção
        usuarioRepository.limparPapeis(id);
        // Depois deleta o usuário
        usuarioRepository.deleteById(id);
    }

    private Usuario findUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
    }
}