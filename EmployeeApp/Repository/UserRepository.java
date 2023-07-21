package com.example.EmployeeApp.Repository;

import com.example.EmployeeApp.dbobjects.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String>
{
    Optional<Users> findUserByEmail(String email);
}
