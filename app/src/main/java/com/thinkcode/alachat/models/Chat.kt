package com.thinkcode.alachat.models

data class Chat(

    var sender: String = "",
    var message: String = "",
    var receiver: String = "",
    var isseen: Boolean = false,
    var url:String="",
    var messegeId: String = ""

)
