package com.dmdm.ecommerce_backend.Config;


import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


// This class is not used explicitly because we manually handle login and JWT generation.
// However, it's still required by Spring Security's internal mechanisms (like DaoAuthenticationProvider)
// to represent authenticated users if we ever decide to delegate authentication to Spring.

public class UserDetailsImpl implements UserDetails {

    private final User user;

    // Constructor takes our custom User entity
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRole(); // Get the enum roles from our User

        // Convert each Role enum to Spring Security's required format:
        // e.g. "ROLE_ADMIN", "ROLE_USER"
        // "ROLE_" is a Spring convention. It looks for roles that start with "ROLE_"
        // .name() comes from the Enum class and returns the string of the enum constant
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Password is already hashed with BCrypt
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Spring uses this to identify the user
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Return false if you want to support expiring accounts
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Return false if you want to support locking users
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Return false if you want expirable passwords
    }

    @Override
    public boolean isEnabled() {
        return true; // Return false if user is deactivated or banned
    }

    // Optional: expose the original User entity (useful for accessing full info)
    public User getUser() {
        return user;
    }

}
