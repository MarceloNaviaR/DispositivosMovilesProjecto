package edu.ucb.project.feature.tareas

data class jobs(
    val id: String,
    val titulo: String,
    val completada: Boolean = false,
    val timestamp: Long = 0L
)