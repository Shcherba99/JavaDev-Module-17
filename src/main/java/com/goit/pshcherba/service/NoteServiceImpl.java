package com.goit.pshcherba.service;

import com.goit.pshcherba.entity.Note;
import com.goit.pshcherba.exception.NoteDeleteException;
import com.goit.pshcherba.exception.NoteUpdateException;
import com.goit.pshcherba.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing notes.
 * Provides basic CRUD operations for working with notes.
 */
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;


    /**
     * Retrieves a list of all notes.
     *
     * @return a list containing all notes in the service.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Note> noteListAll() {
        return noteRepository.findAll();
    }


    /**
     * Adds a new note to the service.
     * Generates a unique ID for the note and stores it in the map.
     *
     * @param note the note to add.
     * @return the added note with its generated ID.
     */
    @Override
    @Transactional
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    /**
     * Deletes a note by its ID.
     * If no note with the given ID exists, a {@link NoteDeleteException} is thrown.
     *
     * @param id the ID of the note to delete.
     * @throws NoteDeleteException if no note with the given ID is found.
     */
    @Override
    @Transactional
    public void deleteNoteById(long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteDeleteException(id));
        noteRepository.delete(note);
    }


    /**
     * Updates an existing note.
     * If the note with the given ID does not exist, a {@link NoteUpdateException} is thrown.
     *
     * @param note the note containing updated data.
     * @throws NoteUpdateException if no note with the given ID is found.
     */
    @Override
    @Transactional
    public void editNote(Note note) {
        long id = note.getId();
        Note existingNote = noteRepository.findById(id).orElseThrow(() -> new NoteUpdateException(id));
        Optional.ofNullable(note.getTitle()).ifPresent(existingNote::setTitle);
        Optional.ofNullable(note.getContent()).ifPresent(existingNote::setContent);
    }
}
