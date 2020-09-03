package com.it.partaker

class User (
    private val id : String,
    private val fullName : String,
    private val phoneNumber : String,
    private val city : String,
    private val email : String,
    private val password : String,
    private val gender : String,
    private val registerAs : String,
    private val bloodGroup : String,
    private val profilePic : String
)
{

    fun User(){}
    fun getId(): String {
        return id
    }
    fun getFullName(): String {
        return fullName
    }
    fun getPhoneNumber(): String {
        return phoneNumber
    }
    fun getCity(): String {
        return city
    }
    fun getEmail(): String {
        return email
    }
    fun getPassword(): String {
        return password
    }
    fun getRegisterAs(): String {
        return registerAs
    }
    fun getGender(): String {
        return gender
    }
    fun getProfilePic(): String {
        return profilePic
    }
    fun getBloodGroup(): String {
        return bloodGroup
    }
}