package com.parkchanwoo.data

import com.parkchanwoo.data.collections.Note
import com.parkchanwoo.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

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