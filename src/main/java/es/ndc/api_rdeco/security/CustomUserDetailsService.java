package es.ndc.api_rdeco.security;

import es.ndc.api_rdeco.entities.RoleEntity;
import es.ndc.api_rdeco.entities.UserEntity;
import es.ndc.api_rdeco.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("Nombre no encontrado"));
        return buildUserDetails(user);
    }

    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(UserEntity user) {
        Collection<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRole());
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), authorities);
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(RoleEntity role) {
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }
}
