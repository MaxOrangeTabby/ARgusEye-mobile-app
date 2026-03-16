package com.example.argus_eye.data.remote.api.controller

import com.example.argus_eye.data.model.Contact

class ContactsController {
    fun getContacts(): List<Contact> {
        return listOf(
            Contact("1", "Alune Moon"),
            Contact("2", "Amelia Joe"),
            Contact("3", "Aphelios Moon"),
            Contact("4", "Avery"),
            Contact("5", "Banana Cat"),
            Contact("6", "Beatrice"),
            Contact("7", "Bella Doe"),
            Contact("8", "Bob"),
            Contact("9", "Charlie"),
            Contact("10", "David"),
            Contact("11", "Eve"),
            Contact("12", "Frank"),
            Contact("13", "Grace"),
            Contact("14", "Heidi"),
            Contact("15", "Ivan"),
            Contact("16", "Jack"),
            Contact("17", "Kelly"),
            Contact("18", "Liam"),
            Contact("19", "Mia"),
            Contact("20", "Noah"),
            Contact("21", "Olivia"),
            Contact("22", "Peter"),
            Contact("23", "Quinn"),
            Contact("24", "Rose"),
            Contact("25", "Sam"),
            Contact("26", "Tina"),
            Contact("27", "Umar"),
            Contact("28", "Victor"),
            Contact("29", "Wendy"),
            Contact("30", "Xavier"),
            Contact("31", "Yara"),
            Contact("32", "Zane")
        ).sortedBy { it.name }
    }
}