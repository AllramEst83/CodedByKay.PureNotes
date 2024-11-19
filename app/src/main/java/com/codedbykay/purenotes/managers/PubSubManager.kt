package com.codedbykay.purenotes.managers

import com.codedbykay.purenotes.services.PubSubService
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

object PubSubManager {
    private var pubSubService: PubSubService? = null
    private var isStarted = false
    private var isInitialized = false
    private lateinit var deviceId: String
    private var sharedKey: String? = null

    fun initialize(
        projectId: String,
        deviceId: String,
        credentialsStream: InputStream,
        toDoViewModel: ToDoViewModel,
        toDoGroupViewModel: ToDoGroupViewModel
    ) {
        if (!isInitialized) {
            this.deviceId = deviceId
            pubSubService = PubSubService(
                projectId = projectId,
                deviceId = deviceId,
                credentialsStream = credentialsStream,
                toDoViewModel = toDoViewModel,
                toDoGroupViewModel = toDoGroupViewModel
            )
            isInitialized = true
        }
    }

    fun start(sharedKey: String) {
        ensureInitialized()

        if (isStarted) return
        val pubSubService = pubSubService ?: return

        isStarted = true
        this.sharedKey = sharedKey

        CoroutineScope(Dispatchers.IO).launch {
            val topicName = pubSubService.createTopic(sharedKey)
            pubSubService.createSubscription(topicName, deviceId)
            pubSubService.listenToSubscription(deviceId)
        }
    }

    fun stop(sharedKey: String) {
        ensureInitialized()

        if (!isStarted) return
        val pubSubService = pubSubService ?: return

        isStarted = false

        CoroutineScope(Dispatchers.IO).launch {
            pubSubService.deleteSubscription(deviceId)
            pubSubService.deleteTopic(sharedKey)
        }
    }

    fun publishMessage(
        action: String,
        resourceType: String,
        resourceId: String,
        content: Map<String, String>? = null
    ) {
        ensureInitialized()

        val pubSubService = pubSubService ?: return
        val topicName = pubSubService.getTopicName(sharedKey ?: return)
        pubSubService.publishMessage(
            topicName = topicName,
            action = action,
            resourceType = resourceType,
            resourceId = resourceId,
            content = content
        )
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("PubSubManager is not initialized. Call initialize() first.")
        }
    }
}
