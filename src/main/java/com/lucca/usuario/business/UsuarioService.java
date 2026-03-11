package com.lucca.usuario.business;

import com.lucca.usuario.business.converter.UsuarioConverter;
import com.lucca.usuario.business.dto.UsuarioDTO;
import com.lucca.usuario.infrastructure.Entity.Usuario;
import com.lucca.usuario.infrastructure.Exceptions.ConflictException;
import com.lucca.usuario.infrastructure.Exceptions.ResourceNotFoundException;
import com.lucca.usuario.infrastructure.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExiste(String email){
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email já cadastrado!" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email já cadastrado!" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail (String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                ()  -> new ResourceNotFoundException("Email não encontrado! " + email));
    }

    public void deletaUsuarioPorEmail (String email){
        usuarioRepository.deleteByEmail(email);
    }


}
