package fr.paug.androidmakers.model

import android.util.SparseArray
import com.google.firebase.firestore.FirebaseFirestore
import fr.paug.androidmakers.util.MapUtil.getStringOrEmpty

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.Locale

import fr.paug.androidmakers.util.MapUtil.getInt
import fr.paug.androidmakers.util.MapUtil.getString
import fr.paug.androidmakers.util.MapUtil.getPartnerList

class FirebaseDataConverted {

    val speakers = mutableMapOf<String, Speaker>()
    val sessions = mutableMapOf<String, Session>()
    val days = mutableListOf<Day>()
    val scheduleSlots = mutableListOf<ScheduleSlot>()

    private val mAllLanguages = HashSet<String>()
    val rooms = SparseArray<Room>()
    private val mPartners = HashMap<PartnerGroup.PartnerType, PartnerGroup>()
    val venues = SparseArray<Venue>()

    var loading = 0

    val db = FirebaseFirestore.getInstance()


    val partners: Map<PartnerGroup.PartnerType, PartnerGroup>
        get() = mPartners

    val allLanguages: Set<String>
        get() = mAllLanguages

    fun loadAllFromFirebase() {
        loading++
        db.collection("speakers")
                .get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        val map = it.data!!
                        speakers.put(it.id,
                                Speaker(
                                        name = map.getStringOrEmpty("name"),
                                        bio = map.getStringOrEmpty("bio"),
                                        company = map.getStringOrEmpty("company"),
                                        thumbnailUrl = map.getStringOrEmpty("photoUrl"),
                                        surname = "",
                                        rockstar = "",
                                        socialNetworkHandles = map.getSocialNetworkHandles("socials"),
                                        ribbonList = emptyList()
                                )
                        )
                    }
                    loadFinished()
                }

        loading++
        db.collection("sessions")
                .get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        val map = it.data!!
                        sessions.put(it.id,
                                Session(
                                        title = map.getStringOrEmpty("title"),
                                        description = map.getStringOrEmpty("description"),
                                        language = map.getStringOrEmpty("language"),
                                        speakers = (map.get("speakers") as? List<String>) ?: emptyList(),
                                        experience = map.getStringOrEmpty("complexity"),
                                        subtype = "",
                                        type = "",
                                        videoURL = null
                                )
                        )
                    }
                    loadFinished()
                }

        loading++
        db.collection("schedule")
                .get()
                .addOnSuccessListener {
                    it.documents.forEach {doc ->
                        val map = doc.data!!

                        val day = Day.fromString(doc.id)
                        if (day != null) {
                            days.add(day)
                        }

                        val timeslots = map.get("timeslots") as? List<Map<String, *>>
                        if (timeslots != null) {
                            timeslots.forEach {
                                scheduleSlots.add(
                                        ScheduleSlot(
                                                room = 0,
                                                sessionId = map.get("")
                                        )
                                )
                                sessions.put(it.id,
                                        Session(
                                                title = map.getStringOrEmpty("title"),
                                                description = map.getStringOrEmpty("description"),
                                                language = map.getStringOrEmpty("language"),
                                                speakers = (map.get("speakers") as? List<String>) ?: emptyList(),
                                                experience = map.getStringOrEmpty("complexity"),
                                                subtype = "",
                                                type = "",
                                                videoURL = null
                                        )
                            }
                        }
                    }
                    loadFinished()
                }

        loadScheduleFromFirebase()
        loadPartnersFromFirebase()
        loadVenuesFromFirebase()
    }


    private fun loadFinished() {
        loading--
        if (loading == 0) {

        }
    }


    private fun loadScheduleFromFirebase(`object`: Any?) {
        if (`object` !is List<*>) {
            return
        }
        val values = `object` as List<*>?
        for (value in values!!) {
            if (value is Map<*, *>) {
                val roomId = value.getInt("roomId", -1)
                val sessionId = value.getInt("sessionId", -1)
                val startDate = getTimestamp(value.getString("startDate"))
                val endDate = getTimestamp(value.getString("endDate"))
                if (startDate > 0 && endDate > 0
                        && sessionId >= 0 && roomId >= 0) {
                    val schedule = ScheduleSlot(
                            roomId, sessionId, startDate, endDate)
                    mScheduleSlots.add(schedule)
                }
            }
        }
    }

    private fun loadPartnersFromFirebase(`object`: Any?) {
        if (`object` !is List<*>) {
            return
        }
        val values = `object` as List<*>?
        for (value in values!!) {
            if (value is Map<*, *>) {
                val partnerGroup = PartnerGroup.getPartnerTypeFromString(value.getString("group"))
                val partnersList = getPartnerList(value, "elements")
                if (partnerGroup != PartnerGroup.PartnerType.Unknown && partnersList != null && partnersList.size > 0) {
                    mPartners[partnerGroup] = PartnerGroup(partnerGroup, partnersList)
                }
            }
        }
    }

    private fun loadVenuesFromFirebase(`object`: Any?) {
        if (`object` !is List<*>) {
            return
        }
        val values = `object` as List<*>?
        for (value in values!!) {
            if (value is Map<*, *>) {
                val id = getId(value)
                if (id >= 0) {
                    venues.put(id, Venue(value.getString("address"),
                            value.getString("coordinates"),
                            value.getString("description"),
                            value.getString("descriptionFr"),
                            value.getString("imageUrl"),
                            value.getString("name")))
                }
            }
        }
    }

    private fun getId(map: Map<*, *>): Int {
        return map.getInt("id", -1)
    }

    private fun getTimestamp(dateIso: String?): Long {
        var dateIso: String? = dateIso ?: return 0
        if (dateIso!!.endsWith("Z")) {
            dateIso = dateIso.substring(0, dateIso.length - 1) + "+0000"
        }
        try {
            return ISO_8601_DATEFORMAT.parse(dateIso).time
        } catch (e: ParseException) {
            return 0
        }

    }

    companion object {
        fun Map<*, *>.getSocialNetworkHandles(key: String): List<SocialNetworkHandle> {
            val list = get(key) as? List<Map<String, *>>
            if (list == null) {
                return emptyList()
            }

            return list.map {
                SocialNetworkHandle(it.getStringOrEmpty("name"), it.getStringOrEmpty("link"))
            }
        }

        private val ISO_8601_DATEFORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    }

}