package edu.ucb.project.scheduler

expect class LogScheduler(context: Any?) {
    fun schedulePeriodicalUpload()
}