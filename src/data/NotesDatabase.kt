package com.parkchanwoo.data

import com.parkchanwoo.data.collections.Note
import com.parkchanwoo.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine // specify how to access db; in our case, use coroutine for all operations
private val database = client.getDatabase("NotesDatabase") // name of our database

// store collections in the db as variables
private val usersCollection = database.getCollection<User>()
private val notesCollection = database.getCollection<Note>()

// register new user to db. coroutines must be suspend functions
suspend fun registerUser(user: User): Boolean {
    return usersCollection.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    // search for user with email. null if none found
//    return usersCollection.findOne("{email: $email}") != null // not very readable; try below
    return usersCollection.findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = usersCollection.findOne(User::email eq email)?.password ?: return false // user doesn't exist
    return actualPassword == passwordToCheck
}

suspend fun getNotesForUser(email: String): List<Note> {
    return notesCollection.find(Note::owners contains email).toList()
}

suspend fun saveNote(note: Note): Boolean {
    val noteExists = notesCollection.findOneById(note.id) != null
    return if (noteExists) {
        notesCollection.updateOneById(note.id, note).wasAcknowledged() // update existing note by passing the id
    } else {
        notesCollection.insertOne(note).wasAcknowledged()
    }
}

suspend fun isOwnerOfNote(noteId: String, owner: String): Boolean {
    val note = notesCollection.findOneById(noteId) ?: return false
    return owner in note.owners
}

suspend fun addOwnerToNote(noteId: String, newOwner: String): Boolean {
    val owners = notesCollection.findOneById(noteId)?.owners ?: return false
    return notesCollection.updateOneById(noteId, setValue(Note::owners, owners + newOwner)).wasAcknowledged()
}

suspend fun deleteNoteForUser(email: String, noteId: String): Boolean {
    val note = notesCollection.findOne(Note::id eq noteId, Note::owners contains email) // comma: AND operation
    note?.let {note ->
        if (note.owners.size > 1) { // note has multiple owners
            // just remove email from owners list
            val newOwners = note.owners - email
            val updateResult = notesCollection.updateOne(Note::id eq note.id, setValue(Note::owners, newOwners))
            return updateResult.wasAcknowledged()
        }

        // note has only one user
        return notesCollection.deleteOneById(note.id).wasAcknowledged()
    } ?: return false // couldn't find note
}