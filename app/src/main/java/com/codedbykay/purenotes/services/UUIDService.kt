package com.codedbykay.purenotes.services

import java.util.UUID

class UUIDService {

    /**
     * Generates a new UUID.
     * @return a newly generated UUID
     */
    fun generateUUID(): UUID {
        return UUID.randomUUID()
    }

    /**
     * Converts a UUID to its String representation.
     * @param uuid the UUID to convert
     * @return the String representation of the UUID
     */
    fun toString(uuid: UUID): String {
        return uuid.toString()
    }

    /**
     * Converts a String to a UUID.
     * @param uuidString the String representation of the UUID
     * @return the UUID object, or null if the string is not a valid UUID
     */
    fun fromString(uuidString: String?): UUID? {
        return try {
            uuidString?.let { UUID.fromString(it) }
        } catch (e: IllegalArgumentException) {
            null // Returns null if the string is not a valid UUID format
        }
    }

    /**
     * Checks if a given String is a valid UUID format.
     * @param uuidString the String to validate
     * @return true if the string is a valid UUID, false otherwise
     */
    fun isValidUUID(uuidString: String?): Boolean {
        return try {
            uuidString != null && UUID.fromString(uuidString) != null
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
