package com.parkchanwoo.data

import com.parkchanwoo.data.collections.Note
import com.parkchanwoo.data.collections.User
import org.litote.kmongo.coroutine.coroutine
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