package com.lucca.usuario.business;

import com.lucca.usuario.business.converter.UsuarioConverter;
import com.lucca.usuario.business.dto.UsuarioDTO;
import com.lucca.usuario.infrastructure.Entity.Usuario;
import com.lucca.usuario.infrastructure.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }
}
