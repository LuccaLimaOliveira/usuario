package com.lucca.usuario.business;

import com.lucca.usuario.business.converter.UsuarioConverter;
import com.lucca.usuario.business.dto.EnderecoDTO;
import com.lucca.usuario.business.dto.TelefoneDTO;
import com.lucca.usuario.business.dto.UsuarioDTO;
import com.lucca.usuario.infrastructure.Entity.Endereco;
import com.lucca.usuario.infrastructure.Entity.Telefone;
import com.lucca.usuario.infrastructure.Entity.Usuario;
import com.lucca.usuario.infrastructure.Exceptions.ConflictException;
import com.lucca.usuario.infrastructure.Exceptions.ResourceNotFoundException;
import com.lucca.usuario.infrastructure.Repository.EnderecoRepository;
import com.lucca.usuario.infrastructure.Repository.TelefoneRepository;
import com.lucca.usuario.infrastructure.Repository.UsuarioRepository;
import com.lucca.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

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

    public UsuarioDTO buscarUsuarioPorEmail (String email){
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                    ()  -> new ResourceNotFoundException("Email não encontrado! " + email)));
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Emmail não encontrado! ");
        }

    }

    public void deletaUsuarioPorEmail (String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){
        //Aqui buscamos o email do usuário através do token (Tira obrigatoriedade do email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //Busca os dados do usuário no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado!"));

        //Mesclou os dados que recebemos na requisiçõa DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //Salvou os dados do usuário convertido e depois pegou o retorno e converteu para usuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public EnderecoDTO atualizarDadosEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(
                () -> new ResourceNotFoundException("Id não encontrado! " + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return  usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaDadosTelefone(Long idTelefone, TelefoneDTO telefoneDTO){

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(
                () -> new ResourceNotFoundException("Id não encontrado! " + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, entity);

        return  usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

}
