package com.example.test.services

import Models.Department
import Models.Doctor
import Models.Patient
import Models.Schedule
import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.example.test.R
import com.example.test.utils.DOCTORS
import com.example.test.utils.PATIENTS
import kotlinx.serialization.json.jsonObject

class Search(context: Context) {

    private val client = ClientSearch(
        applicationID = ApplicationID(getString(context, R.string.searchAPI)),
        apiKey = APIKey(getString(context, R.string.publicKey))
    )

    private val departments: Array<Department> = Department.values()

    private val index = client.initIndex(IndexName(DOCTORS))
    private val patientIndex = client.initIndex(IndexName(PATIENTS))

    private val attributes: List<Attribute> = listOf(
        Attribute("firstName"),
        Attribute("lastName"),
        Attribute("address"),
        Attribute("phone"),
        Attribute("email"),
        Attribute("department"),
        Attribute("messageAvailable"),
        Attribute("officeHours"),
        Attribute("objectID")
    )

    private val patientAttributes: List<Attribute> = listOf(
        Attribute("firstName"),
        Attribute("lastName"),
        Attribute("address"),
        Attribute("phone"),
        Attribute("email"),
        Attribute("doctorName"),
        Attribute("doctorUid"),
        Attribute("age"),
        Attribute("objectID")
    )

    suspend fun retrieveValues(searchString: String = ""): MutableMap<String, Doctor> {
        val list: MutableMap<String, Doctor> = mutableMapOf()
        val query = Query(
            query = searchString, attributesToRetrieve = attributes
        )
        val response = index.search(query)
        val hits = response.hits
        hits.forEach { hit ->
            val officeHours = hit.getValue("officeHours").jsonObject
            val messageAvailableObj = hit["messageAvailable"].toString()
            val messageAvailable: Boolean = messageAvailableObj == "true"
            val officeObj = Schedule(
                start = officeHours["start"].toString().trim('"'),
                end = officeHours["end"].toString().trim('"'),
                weekStart = officeHours["weekStart"].toString().trim('"'),
                weekend = officeHours["weekend"].toString().trim('"')
            )
            val user = departments.find { el -> el.name == hit["department"].toString().trim('"') }
                ?.let { dep ->
                    Doctor(
                        firstName = hit["firstName"].toString().trim('"'),
                        lastName = hit["lastName"].toString().trim('"'),
                        address = hit["address"].toString().trim('"'),
                        phone = hit["phone"].toString().trim('"'),
                        email = hit["email"].toString().trim('"'),
                        department = dep,
                        officeHours = officeObj,
                        messageAvailable = messageAvailable
                    )
                }
            if (user != null) {
                list[hit["objectID"].toString().trim('"')] = user
            }
        }
        return list

    }

    suspend fun retrievePatientValues(searchString: String = ""): MutableMap<String, Patient> {
        val list: MutableMap<String, Patient> = mutableMapOf()
        val query = Query(
            query = searchString, attributesToRetrieve = patientAttributes
        )
        val response = patientIndex.search(query)
        val hits = response.hits
        hits.forEach { hit ->
            val user =
                Patient(
                    firstName = hit["firstName"].toString().trim('"'),
                    lastName = hit["lastName"].toString().trim('"'),
                    address = hit["address"].toString().trim('"'),
                    phone = hit["phone"].toString().trim('"'),
                    email = hit["email"].toString().trim('"'),
                    age = hit["age"].toString().trim('"').toInt(),
                    doctorName = hit["doctorName"].toString().trim('"'),
                    doctorUid = hit["doctorUid"].toString().trim('"'),
                )

            list[hit["objectID"].toString().trim('"')] = user
        }
        return list

    }
}