package com.codedbykay.purenotes.utils

fun sanitizeTopicName(sharedKey: String): String {
    return sharedKey.replace("[^a-zA-Z0-9-_\\.]+".toRegex(), "_")
}

fun sanitizeSubscriptionId(subscriptionId: String): String {
    return subscriptionId
        .replace("[^a-zA-Z0-9_-]".toRegex(), "_") // Replace invalid characters with '_'
        .take(255) // Ensure the length is under 255 characters
        .let {
            if (it.firstOrNull()
                    ?.isLetter() != true
            ) "sub_$it" else it // Prefix with "sub_" if it doesn't start with a letter
        }
        .trimEnd('-') // Ensure it doesn't end with a hyphen
}
