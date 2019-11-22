package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void saveUser(User user) {
        if (!user.getPassword().startsWith("$2a$10$")){
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }
    public User getUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        return optUser.orElse(null);
    }

    public void setMightLimitById(int might,int limit, long id) {
        userRepository.setUserMightLimitById(might,limit,id);
    }

    public boolean checkPassword(String password, String encoded){
        return bCryptPasswordEncoder.matches(password, encoded);
    }

}
