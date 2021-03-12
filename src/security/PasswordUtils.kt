package com.parkchanwoo.security

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

// hash functions

fun getHashWithSalt(stringToHash: String, saltLength: Int = 32): String {
    // generate random salt
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
    val saltAsHex = Hex.encodeHexString(salt)
    val hash = DigestUtils.sha256Hex("$saltAsHex$stringToHash") // put two strings together
    return "$saltAsHex:$hash"
}

fun checkHashForPassword(password: String, hashWithSalt: String): Boolean {
    // separate salt & hash
    val hashAndSalt = hashWithSalt.split(":")
    val salt = hashAndSalt[0]
    val hash = hashAndSalt[1]

    // prepend salt to user's input password
    val passwordHash = DigestUtils.sha256Hex("$salt$password")
    return hash == passwordHash
}