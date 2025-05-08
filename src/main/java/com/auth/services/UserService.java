package com.auth.services;

import com.auth.models.CustomUser;
import com.auth.services.errors.NotFoundError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private List<CustomUser> userList = new ArrayList<>();

    private PasswordEncoder passwordEncoder;

    UserService() {
        passwordEncoder = new BCryptPasswordEncoder();
        ArrayList default_creds = new ArrayList<>();
        CustomUser user_one = new CustomUser("user_one", passwordEncoder.encode("password"), default_creds);
        userList.add(user_one);
        CustomUser user_two = new CustomUser("user_two", passwordEncoder.encode("password"), default_creds);
        userList.add(user_two);
    }

    public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Result<CustomUser> found = findByUsername(username);
        if (found == null) {
            throw new UsernameNotFoundException(username);
        }
        if (found.isSuccess()) {
            return found.getValue();
        }
        throw new UsernameNotFoundException(username);
    }

    public Result<CustomUser> findByUsername(String username) {
        // Remplacez ceci par une recherche en base de données
        for (CustomUser user : userList) {
            if (user.getUsername().equals(username)) {
                return Result.success(user);
            }
        }
        return Result.failure(new NotFoundError("User not found"));
    }
}
