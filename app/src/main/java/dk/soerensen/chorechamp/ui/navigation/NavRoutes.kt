package dk.soerensen.chorechamp.ui.navigation

object NavRoutes {
    const val ROLE = "role"
    const val CHILD = "child/{username}"
    const val CHILD_CHOOSE_TASKS = "child_choose_tasks/{username}"
    const val PARENT = "parent/{username}"
    const val APPROVALS = "approvals/{username}"
    const val ADD_TASK = "add_task/{username}"
    const val REWARDS = "rewards/{username}"
    const val PARENT_REWARDS = "parent_rewards/{username}"
    const val PROFILE = "profile/{username}"

    const val DRAGON_SELECT = "dragon_select/{username}"

    fun child(username: String) = "child/$username"
    fun childChooseTasks(username: String) = "child_choose_tasks/$username"
    fun parent(username: String) = "parent/$username"
    fun approvals(username: String) = "approvals/$username"
    fun addTask(username: String) = "add_task/$username"
    fun rewards(username: String) = "rewards/$username"
    fun parentRewards(username: String) = "parent_rewards/$username"
    fun profile(username: String) = "profile/$username"
    fun dragonSelect(username: String) = "dragon_select/$username"
}
