package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAll();

    @Modifying
    @Transactional
    @Query("update User u set u.might = ?1, u.limit = ?2 where u.id = ?3")
    void setUserMightLimitById(int might,int limit, Long userId);
}
