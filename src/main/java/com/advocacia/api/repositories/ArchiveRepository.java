package com.advocacia.api.repositories;

import com.advocacia.api.domain.archive.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, String> {

    Archive findByLinkArchive(String linkArchive);
    Optional<Archive> findById(String id);
    void deleteById(String id);
    @Query("select a from Archive a where a.linkArchive like CONCAT('%',?1,'%')")
    List<Archive> findAllModel(String pathModel);

    Archive findByName(String name);
}