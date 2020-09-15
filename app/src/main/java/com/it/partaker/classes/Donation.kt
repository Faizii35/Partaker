package com.it.partaker.classes

data class Donation(
    private var name : String = "",
    private var desc : String = "",
    private var image : String = "",
    private var donor: String = "",
    private var status: String = "",
    private var assigned: String = ""
) {

    fun Donation(){}
    fun Donation( name: String, desc : String, image: String, donor: String, status: String, assigned: String){
        this.name = name
        this.desc = desc
        this.image = image
        this.donor = donor
        this.status = status
        this.assigned = assigned
    }

    fun getName(): String{ return name }
    fun getDesc(): String{ return desc }
    fun getImage(): String{ return image }
    fun getDonor(): String {return donor}
    fun getStatus(): String{ return status}
    fun getAssigned() : String{return assigned}
}