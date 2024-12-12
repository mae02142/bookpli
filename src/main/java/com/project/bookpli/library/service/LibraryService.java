package com.project.bookpli.library.service;

import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {
    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }


    public List<LibraryResponseDTO> getMyLibrary(Long userId) {
        return libraryRepository.getMyLibrary(userId);
    }
}
