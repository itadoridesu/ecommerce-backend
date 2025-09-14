package com.dmdm.ecommerce_backend.Config;

import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// This class is not called in our current login flow because we authenticate users manually.
// Still, it's required by Spring Security and is used by the DaoAuthenticationProvider
// to load users by username during internal authentication processes. (again if we let spring
// handle logging users in, but we are not currently hehe)
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired // Inject the UserRepository
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Look up the user in the DB using the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Wrap it in a UserDetails object
        return new UserDetailsImpl(user);
    }
}
