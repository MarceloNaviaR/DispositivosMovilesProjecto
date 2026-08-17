package edu.ucb.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform