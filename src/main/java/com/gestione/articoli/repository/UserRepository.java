package com.gestione.articoli.repository;

import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndActiveInCompanyTrue(String username);
    List<User> findByRolesIsEmptyAndActiveInCompanyTrue();
    boolean existsByUsernameIgnoreCase(String username);
	long countByRolesContaining(Role admin);
    //   (esclusi username "admin" e "user")
    List<User> findByRolesContainingAndActiveInCompanyTrueAndUsernameNotIgnoreCaseAndUsernameNotIgnoreCase(
        Role role, String excluded1, String excluded2
    );

    //  Postazioni attive
    List<User> findByMachineUserTrueAndActiveInCompanyTrue();
    List<User>  findByRolesIsEmptyAndActiveInCompanyTrueOrderByUsernameAsc();
	List<User> findByRolesIsEmptyAndActiveInCompanyFalseOrderByUsernameAsc();
	List<User> findByActiveInCompanyFalseOrderByUsernameAsc();
	List<User> findByRolesAndActiveInCompanyFalse(Role role);
	List<User> findByRolesIsEmptyAndActiveInCompanyFalse();
	List<User> findByMachineUserTrueAndActiveInCompanyFalse();
    // Conta quanti utenti hanno il ruolo ADMIN
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") Role role);
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role AND u.activeInCompany = true")
    long countActiveByRole(@Param("role") Role role);
}
