package com.belov.agregator

import at.favre.lib.crypto.bcrypt.*

    // This will create password hashes and return them
    fun createHash(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    // This will verify if password matches the hash from database (а надо ли?)
    fun verifyHash(password: String, hash: String): BCrypt.Result {
        return BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray())
    }

