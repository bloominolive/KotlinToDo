import java.io.File //allows us to add a file after creating the task list and access it when running the application again
//main function that runs the application

fun main() {
    println("Welcome to Task Manager!")

    // Loads tasks from the file at startup
    val tasks = loadTasksFromFile()

    while (true) {
        // Main menu display
        println("\nTask Menu:")
        println("1. Add")
        println("2. List")
        println("3. Complete/Incomplete")
        println("4. Remove")
        println("5. Exit (save tasks to file)")
        print("Please choose a menu item number: ")

        when (readLine()?.toIntOrNull()) {
            //each case the user could enter with accompanying functions
            1 -> addTasks(tasks)
            2 -> {
                listTasks(tasks)
                waitForMenu()
            }
            3 -> {
                if (tasks.isEmpty()) {
                    println("No tasks to mark as complete. Add a task to get started!")
                    waitForMenu()
                } else {
                    listTasks(tasks)
                    var validInput = false
                    while (!validInput) {
                        print("Enter task number to mark complete/incomplete (or enter 0 to return to the Main Menu): ")
                        val index = (readLine()?.toIntOrNull() ?: -1) - 1
                        if (index == -1) break // Return to main menu if input is 0

                        if (index in tasks.indices) {
                            markTask(tasks, index)
                            validInput = true
                            saveTasksToFile(tasks) // Save updated tasks
                            waitForMenu() // Only wait if a task is marked complete
                        } else {
                            println("Invalid task number. Please try again.")
                        }
                    }
                }
            }
            4 -> {
                if (tasks.isEmpty()) {
                    println("No tasks to remove. Add a task to get started!")
                    waitForMenu()
                } else {
                    listTasks(tasks)
                    var validInput = false
                    while (!validInput) {
                        print("Enter task number to remove (or enter 0 to return to the Main Menu): ")
                        val index = (readLine()?.toIntOrNull() ?: -1) - 1
                        if (index == -1) break // Return to main menu if input is 0

                        if (index in tasks.indices) {
                            removeTask(tasks, index)
                            validInput = true
                            saveTasksToFile(tasks) // Save updated tasks
                            waitForMenu() // Only wait if a task is removed
                        } else {
                            println("Invalid task number. Please try again.")
                        }
                    }
                }
            }
            5 -> {
                println("Exiting Task Manager. Happy tasking!")
                break
            }
            else -> println("Invalid option. Please try again.")
        }
    }
}

// Immutable variable for file path to store tasks
val filePath = "tasks.txt"

// Function to load mutable list of tasks from a file
fun loadTasksFromFile(): MutableList<Task> {
    val tasks = mutableListOf<Task>()
    val file = File(filePath)
    if (file.exists()) {
        file.useLines { lines ->
            lines.drop(2).forEach { line ->  // Skip the header row and underline row
                val parts = line.split("| ")
                if (parts.size == 2) {
                    val description = parts[0].trim()
                    val isComplete = parts[1].trim() == "Complete"
                    tasks.add(Task(description, isComplete))
                }
            }
        }
    }
    return tasks
}

// Function to save tasks to a file with headers
fun saveTasksToFile(tasks: List<Task>) {
    val file = File(filePath)
    file.printWriter().use { out ->
        out.println("Task                 | Status     ")  // Header row with fixed-width columns
        out.println("----------------------------------")   // Underlining header
        tasks.forEach { task ->
            val description = task.description.padEnd(20)  // Pad description to 20 characters for more uniform look
            val status = if (task.isComplete) "Complete" else "Incomplete"
            out.println("$description | $status")
        }
    }
}

// add multiple tasks based on user input
fun addTasks(tasks: MutableList<Task>) {
    do {
        print("Add a task: ")
        val description = readLine() ?: ""
        addTask(tasks, description) //pulls in single task from addTask
        saveTasksToFile(tasks) // save updated tasks after add

        print("Great job! Would you like to add another task? (Y/N): ")
        val choice = readLine()?.trim()?.uppercase()
    } while (choice == "Y")
}

// wait for Enter key to return to the main menu
fun waitForMenu() {
    println("\nPress Enter to return to Main Menu.")
    readLine()  // Wait for Enter key without requiring any specific input
}

// Task class definition
class Task(val description: String, var isComplete: Boolean = false) {
    fun toggleComplete() {
        isComplete = !isComplete

    }
}

// functions to manage tasks
// adding single task
fun addTask(tasks: MutableList<Task>, description: String) {
    tasks.add(Task(description)) //adds to task list in addTasks
    println("Task added: $description")
}

// list of tasks and if they are complete or incomplete
fun listTasks(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        println("No tasks to display. Add a task to get started!")
    } else {
        println("Tasks:")
        for ((index, task) in tasks.withIndex()) {
            println("${index + 1}. ${task.description} - ${if (task.isComplete) "Complete" else "Incomplete"}")
        }
    }
}

//marking a task complete or incomplete
fun markTask(tasks: MutableList<Task>, index: Int) {
    if (index in tasks.indices) {
        tasks[index].toggleComplete() //toggles the function in task class to be complete or incomplete
        println("Marked task as ${if (tasks[index].isComplete) "complete" else "incomplete"}: ${tasks[index].description}")
    } else {
        println("Invalid task number")
    }
}

//removes task from list
fun removeTask(tasks: MutableList<Task>, index: Int) {
    if (index in tasks.indices) {
        println("Removing task: ${tasks[index].description}")
        tasks.removeAt(index) //removal
    } else {
        println("Invalid task number")
    }
}
