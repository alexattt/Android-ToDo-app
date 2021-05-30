package com.alexandrat.todo_rtu

class Model {

    private var task: String? = null
    private var description:kotlin.String? = null
    private var id:kotlin.String? = null
    private var date:kotlin.String? = null
    private var status:kotlin.Boolean = false

    constructor() {}

    constructor(task: String?, description: String?, id: String?, date: String?, status: Boolean) {
        this.task = task!!
        this.description = description!!
        this.id = id!!
        this.date = date!!
        this.status = status
    }

    fun getTask(): String? {
        return task
    }

    fun setTask(task: String?) {
        this.task = task
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

}