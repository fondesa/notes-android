package com.fondesa.notes.notes.impl

import com.fondesa.notes.log.api.Log
import com.fondesa.notes.notes.api.Draft
import com.fondesa.notes.notes.api.Note
import com.fondesa.notes.notes.api.NotesInteractor
import com.fondesa.notes.ui.api.qualifiers.ScreenScope
import javax.inject.Inject

@ScreenScope
class NotesPresenter @Inject constructor(
    private val view: NotesContract.View,
    private val notesInteractor: NotesInteractor
) : NotesContract.Presenter {

    private val noteScreenContent = NoteScreenContent(
        title = "",
        description = ""
    )
    private var buttonState = NoteButtonState.UNDEFINED
        set(value) {
            view.renderButtonState(value)
            field = value
        }

    private var isNoteScreenShown: Boolean = false
    private var pendingNoteScreenId: Int? = null

    override fun attach() {
        buttonState = NoteButtonState.ADD
        view.hideListContainer()
        view.hideZeroElementsView()
        val notes = notesInteractor.getAllNotes()
        if (notes.isEmpty()) {
            view.showZeroElementsView()
        } else {
            view.showListContainer()
            view.showNoteList(notes)
        }
    }

    override fun addButtonClicked() {
        // Since we are creating a new note, we don't need any id.
        pendingNoteScreenId = null
        view.showNoteScreenTitle("")
        view.showNoteScreenDescription("")
        view.showNoteScreen()
    }

    override fun doneButtonClicked() {
        view.hideNoteScreen()

        val draft = noteScreenContent.toDraft()

        val pendingNoteScreenId = pendingNoteScreenId
        if (pendingNoteScreenId != null) {
            // Update the note.
            notesInteractor.updateNote(pendingNoteScreenId, draft)
        } else {
            // Insert the note.
            notesInteractor.insertNote(draft)
        }

        val notes = notesInteractor.getAllNotes()
        view.hideZeroElementsView()
        view.showListContainer()
        view.showNoteList(notes)

        view.showNoteScreenTitle("")
        view.showNoteScreenDescription("")
    }

    override fun cancelButtonClicked() {
        view.hideNoteScreen()
    }

    override fun backPressed() {
        if (isNoteScreenShown) {
            view.hideNoteScreen()
        } else {
            view.executeBackPress()
        }
    }

    override fun pressedOutsideNoteScreen() {
        if (!isNoteScreenShown) {
            Log.e("The presenter received the touch outside with the note screen hidden.")
            return
        }
        view.hideNoteScreen()
    }

    override fun noteScreenShown() {
        isNoteScreenShown = true
        buttonState = if (noteScreenContent.isValid) {
            NoteButtonState.DONE
        } else {
            NoteButtonState.CANCEL
        }
    }

    override fun noteScreenHidden() {
        isNoteScreenShown = false
        buttonState = NoteButtonState.ADD
    }

    override fun noteScreenTitleChanged(title: String) {
        noteScreenContent.title = title
        buttonState = if (noteScreenContent.isValid) {
            NoteButtonState.DONE
        } else {
            NoteButtonState.CANCEL
        }
    }

    override fun noteScreenDescriptionChanged(description: String) {
        noteScreenContent.description = description
        buttonState = if (noteScreenContent.isValid) {
            NoteButtonState.DONE
        } else {
            NoteButtonState.CANCEL
        }
    }

    override fun noteClicked(note: Note) {
        // Save the note id to identify the note which should be updated.
        pendingNoteScreenId = note.id
        view.showNoteScreenTitle(note.title)
        view.showNoteScreenDescription(note.description)
        view.showNoteScreen()
    }

    private data class NoteScreenContent(
        var title: String,
        var description: String
    ) {

        val isValid: Boolean get() = title.isNotBlank() || description.isNotBlank()

        fun toDraft(): Draft = Draft(title, description)
    }
}