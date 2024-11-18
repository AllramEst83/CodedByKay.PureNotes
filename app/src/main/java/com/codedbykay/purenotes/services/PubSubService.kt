package com.codedbykay.purenotes.services

import android.content.Context
import android.util.Log
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.db.todo.ToDo
import com.codedbykay.purenotes.utils.sanitizeSubscriptionId
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import com.codedbykay.purenotes.viewModels.ToDoViewModel
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.protobuf.ByteString
import com.google.protobuf.Duration
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.PushConfig
import com.google.pubsub.v1.SubscriptionName
import com.google.pubsub.v1.Topic
import com.google.pubsub.v1.TopicName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.Instant
import java.util.Date

class PubSubService(
    private val projectId: String,
    private val context: Context,
    private val deviceId: String,
    private val toDoViewModel: ToDoViewModel,
    private val toDoGroupViewModel: ToDoGroupViewModel
) {
    private val logTag = "PubSubService"
    private val credentials = ServiceAccountCredentials.fromStream(
        context.resources.openRawResource(R.raw.google_service_account)
    )

//https://raw.githubusercontent.com/GoogleCloudPlatform/kotlin-samples/refs/heads/main/pubsub/src/main/kotlin/PubSub.kt

    // Ensure the topic exists
    suspend fun createTopic(topicId: String, retentionSeconds: Long = 3600L): TopicName {
        val topicName = TopicName.of(projectId, topicId)
        return withContext(Dispatchers.IO) {
            try {
                TopicAdminClient.create(
                    TopicAdminSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build()
                ).use { client ->

                    // Set the message retention duration
                    val retentionDuration = Duration.newBuilder()
                        .setSeconds(retentionSeconds)
                        .build()

                    // Create the topic with the specified retention duration
                    val topic = Topic.newBuilder()
                        .setName(topicName.toString())
                        .setMessageRetentionDuration(retentionDuration)
                        .build()

                    client.createTopic(topic)
                    Log.i(logTag, "Topic '${topicName.topic}' created successfully.")
                }
            } catch (e: Exception) {
                Log.e(logTag, "Failed to create topic '${topicName.topic}': ${e.message}")
            }
            topicName
        }
    }

    // Ensure the subscription exists
    suspend fun createSubscription(topicName: TopicName, subscriptionId: String) {
        val sanitizedSubscriptionId = sanitizeSubscriptionId(subscriptionId)
        val subscriptionName = SubscriptionName.of(projectId, sanitizedSubscriptionId)
        withContext(Dispatchers.IO) {
            try {
                SubscriptionAdminClient.create(
                    SubscriptionAdminSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build()
                ).use { client ->
                    client.createSubscription(
                        subscriptionName,
                        topicName,
                        PushConfig.getDefaultInstance(),
                        10 // Acknowledgment deadline in seconds
                    )
                    Log.i(
                        logTag,
                        "Subscription '${subscriptionName.subscription}' created successfully."
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    logTag,
                    "Failed to create subscription '${subscriptionName.subscription}': ${e.message}"
                )
            }
        }
    }

    // Publish a message
    fun publishMessage(
        topicName: TopicName,
        action: String,
        resourceType: String,
        resourceId: String,
        content: Map<String, String>? = null
    ) {
        val publisher = Publisher.newBuilder(topicName)
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()

        try {
            val jsonMessage = JSONObject().apply {
                put("deviceId", deviceId)
                put("action", action)
                put("resourceType", resourceType)
                put("resourceId", resourceId)
                content?.forEach { (key, value) -> put(key, value) }
            }

            val pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(jsonMessage.toString()))
                .putAttributes("deviceId", deviceId)
                .build()

            val messageId = publisher.publish(pubsubMessage).get()
            Log.i(logTag, "Published message with ID: $messageId")
        } catch (e: Exception) {
            Log.e(logTag, "Failed to publish message: ${e.message}")
        } finally {
            publisher.shutdown()
        }
    }


    // Listen to subscription and process messages
    fun listenToSubscription(subscriptionId: String) {
        val sanitizedSubscriptionId = sanitizeSubscriptionId(subscriptionId)
        val subscriptionName = ProjectSubscriptionName.of(projectId, sanitizedSubscriptionId)

        class MessageReceiverExample : com.google.cloud.pubsub.v1.MessageReceiver {
            override fun receiveMessage(
                message: PubsubMessage,
                consumer: com.google.cloud.pubsub.v1.AckReplyConsumer
            ) {
                val data = JSONObject(message.data.toStringUtf8())
                println("Received message ID: ${message.messageId}")
                println("Data: ${data}")

                processMessage(data)
                consumer.ack()
            }
        }

        val subscriber = Subscriber.newBuilder(subscriptionName, MessageReceiverExample())
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                subscriber.startAsync().awaitRunning()
                Log.i(logTag, "Listening to subscription '$subscriptionId'...")

                kotlinx.coroutines.delay(Long.MAX_VALUE)
            } catch (e: Exception) {
                Log.e(logTag, "Failed to listen to subscription: ${e.message}")
            } finally {
                subscriber.stopAsync()
                Log.i(logTag, "Stopped listening to subscription '$subscriptionId'.")
            }
        }
    }


    suspend fun deleteTopic(topicId: String) {
        val topicName = TopicName.of(projectId, topicId)
        withContext(Dispatchers.IO) {
            try {
                TopicAdminClient.create(
                    TopicAdminSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build()
                ).use { client ->
                    client.deleteTopic(topicName)
                    Log.i(logTag, "Topic '${topicName.topic}' deleted successfully.")
                }
            } catch (e: Exception) {
                Log.e(logTag, "Failed to delete topic '${topicName.topic}': ${e.message}")
            }
        }
    }

    suspend fun deleteSubscription(subscriptionId: String) {
        val sanitizedSubscriptionId = sanitizeSubscriptionId(subscriptionId)
        val subscriptionName = SubscriptionName.of(projectId, sanitizedSubscriptionId)
        withContext(Dispatchers.IO) {
            try {
                SubscriptionAdminClient.create(
                    SubscriptionAdminSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build()
                ).use { client ->
                    client.deleteSubscription(subscriptionName)
                    Log.i(
                        logTag,
                        "Subscription '${subscriptionName.subscription}' deleted successfully."
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    logTag,
                    "Failed to delete subscription '${subscriptionName.subscription}': ${e.message}"
                )
            }
        }
    }


    // Process received messages and update the ViewModel
    private fun processMessage(data: JSONObject) {
        val action = data.getString("action")
        val resourceType = data.getString("resourceType")
        val resourceId = data.getString("resourceId")

        // Use CoroutineScope for executing suspend functions
        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                "create" -> when (resourceType) {
                    "group" -> toDoGroupViewModel.addGroup(
                        data.getString("title"),
                        Date.from(Instant.now())
                    )

                    "note" -> toDoViewModel.addToDo(
                        data.getString("title"),
                        data.getInt("groupId"),
                        data.optString("content", "")
                    )
                }

                "update" -> when (resourceType) {
                    "group" -> toDoGroupViewModel.updateGroup(
                        resourceId.toInt(),
                        data.getString("title")
                    )

                    "note" -> toDoViewModel.updateToDoAfterEdit(
                        ToDo(
                            id = resourceId.toInt(),
                            title = data.getString("title"),
                            content = data.getString("content"),
                            Date.from(Instant.now()),
                            done = false,
                            groupId = data.getInt("groupId")
                        )
                    )
                }

                "updateDoneStatus" -> toDoViewModel.updateToDoDone(
                    resourceId.toInt(),
                    data.getBoolean("doneStatus")
                )

                "delete" -> when (resourceType) {
                    "group" -> toDoGroupViewModel.deleteGroupById(resourceId.toInt())
                    "note" -> toDoViewModel.deleteToDo(
                        id = resourceId.toInt(),
                        notificationRequestCode = null,
                        notificationAction = null,
                        notificationDataUri = null
                    )
                }

                "deleteCollection" -> {
                    val groupId = data.getInt("groupId")
                    toDoViewModel.deleteAllDoneToDos(groupId)
                }
            }
        }
    }
}
