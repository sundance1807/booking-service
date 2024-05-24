package com.booking_service.security;

import com.booking_service.model.Role;
import com.booking_service.model.entity.BookingUser;
import com.booking_service.repository.BookingUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingUserDetailsService implements UserDetailsService {
    private final BookingUserRepository bookingUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BookingUser bookingUser = bookingUserRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username + " not found."));
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(bookingUser.getRole().toString()));
        return new User(bookingUser.getUsername(), bookingUser.getPassword(), authorities);
    }
}
