package com.example.coffeemachine.security;

import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security UserDetails implementation.
 * Wraps the User entity for Spring Security authentication and authorization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private Long facilityId;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Create UserPrincipal from User entity.
     *
     * @param user the User entity
     * @return UserPrincipal instance
     */
    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPasswordHash(),
            user.getRole(),
            user.getFacility() != null ? user.getFacility().getId() : null,
            authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Check if user has ADMIN role.
     *
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }

    /**
     * Check if user has FACILITY role.
     *
     * @return true if facility user, false otherwise
     */
    public boolean isFacilityUser() {
        return UserRole.FACILITY.equals(role);
    }
}