//package com.softuni.easypeasyrecipes_app.service.session;
//
//import com.softuni.easypeasyrecipes_app.model.entity.User;
//import com.softuni.easypeasyrecipes_app.model.entity.UserRole;
//import com.softuni.easypeasyrecipes_app.repository.UserRepository;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Set;
//
//@Service
//public class UserDetailService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public UserDetailService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
////    @Override
////    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
////        return userRepository.findByUsername(username).map(this::mapToUserDetails)
////                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
////    }
////
////    private UserDetails mapToUserDetails(User user) {
////        return org.springframework.security.core.userdetails.User
////                .withUsername(user.getUsername())
////                .password(user.getPassword())
////                .authorities(mapAuthorities(user.getRoles()))
////                .build();
////    }
////    private List<SimpleGrantedAuthority> mapAuthorities(Set<UserRole> roles) {
////        return roles.stream()
////                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
////                .toList();
////    }
//}
