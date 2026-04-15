package lk.paymedia.student_management_system.service.impl;

import lk.paymedia.student_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        lk.paymedia.student_management_system.entity.User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        boolean isDisabled = !Boolean.TRUE.equals(userEntity.getEnabled());

        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .disabled(isDisabled)
                .authorities(userEntity.getUserRoles().stream()
                        .map(ur -> new SimpleGrantedAuthority(ur.getRole().getRoleType().name()))
                        .toList())
                .build();
    }
}
