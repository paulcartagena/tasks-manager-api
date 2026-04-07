package com.tasksmanager.api.repository;

import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwnerOrMembersContaining(User owner, User member);

    @Query("SELECT p FROM Project p WHERE p.id = :projectId AND (p.owner = :user OR :user MEMBER OF p.members)")
    Optional<Project> findByIdAndOwnerOrMember(@Param("projectId") Long projectId, @Param("user") User user);

    Optional<Project> findByIdAndOwner(Long id, User owner);
}
