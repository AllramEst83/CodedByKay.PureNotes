package com.codedbykay.purenotes

import com.codedbykay.purenotes.db.GroupWithTodos
import com.codedbykay.purenotes.db.ToDo
import com.codedbykay.purenotes.db.ToDoGroup
import com.codedbykay.purenotes.utils.buildNoteShareContent
import com.codedbykay.purenotes.utils.buildShareContent
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Date

class ShareContentUtilsTest {

    @Test
    fun `buildShareContent generates correct content for group with todos`() {
        // Use real instances
        val todo1 = ToDo(
            id = 1,
            title = "Note 1",
            content = "Sub task 1\nSub task 2",
            createdAt = Date(1672531200000L), // Fixed timestamp
            done = true,
            groupId = 1
        )

        val todo2 = ToDo(
            id = 2,
            title = "Note 2",
            content = null,
            createdAt = Date(1672531200000L), // Fixed timestamp
            done = false,
            groupId = 1
        )

        val groupWithTodos = GroupWithTodos(
            group = ToDoGroup(
                id = 1,
                name = "My Tasks",
                createdAt = Date(1672531200000L) // Fixed timestamp
            ),
            todos = listOf(todo1, todo2)
        )

        val expectedOutput = """
        My Tasks

        ✓ Note 1
           Sub task 1
           Sub task 2

        ○ Note 2
    """.trimIndent()

        val actualOutput = buildShareContent(groupWithTodos).trimEnd()

        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun `buildNoteShareContent generates correct content for a single todo`() {
        // Create a real ToDo instance
        val todo = ToDo(
            id = 1,
            title = "My Note",
            content = "This is the content of my note",
            createdAt = Date(1672531200000L), // Fixed timestamp for consistency
            done = true,
            groupId = 1
        )

        val expectedOutput = """
        ✓ My Note
        This is the content of my note
    """.trimIndent()

        val actualOutput = buildNoteShareContent(todo)

        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun `buildNoteShareContent handles empty content`() {
        // Create a real ToDo instance with empty content
        val todo = ToDo(
            id = 1,
            title = "My Note",
            content = "", // Empty content
            createdAt = Date(1672531200000L), // Fixed timestamp for consistency
            done = false,
            groupId = 1
        )

        val expectedOutput = """
        ○ My Note
    """.trimIndent()

        val actualOutput = buildNoteShareContent(todo)

        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun `buildShareContent handles empty todos list`() {
        // Create a real GroupWithTodos instance with an empty todos list
        val groupWithTodos = GroupWithTodos(
            group = ToDoGroup(
                id = 1,
                name = "Empty Group",
                createdAt = Date(1672531200000L) // Fixed timestamp for consistency
            ),
            todos = emptyList() // No todos
        )

        val expectedOutput = """
        Empty Group


    """.trimIndent()

        val actualOutput = buildShareContent(groupWithTodos)

        assertEquals(expectedOutput, actualOutput)
    }
}