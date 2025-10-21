package com.example.wil_byte_horizon.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class ProgramUi(
    val id: String,
    val title: String,
    val description: String
)

data class ArticleUi(
    val id: String,
    val title: String,
    val snippet: String
)

data class ImpactStats(
    val beneficiaries: Int,
    val volunteers: Int,
    val projects: Int
)

class HomeViewModel : ViewModel() {

    private val _programs = MutableLiveData<List<ProgramUi>>()
    val programs: LiveData<List<ProgramUi>> = _programs

    private val _articles = MutableLiveData<List<ArticleUi>>()
    val articles: LiveData<List<ArticleUi>> = _articles

    private val _stats = MutableLiveData<ImpactStats>()
    val stats: LiveData<ImpactStats> = _stats

    init {
        // TODO: Replace with Firestore/REST
        _programs.value = listOf(
            ProgramUi("p1", "Youth Skills", "Coding workshops and mentorship for high school learners."),
            ProgramUi("p2", "Food Support", "Monthly parcels for vulnerable households."),
            ProgramUi("p3", "Community Clinics", "Mobile wellness days with screenings and referrals.")
        )
        _articles.value = listOf(
            ArticleUi("a1", "October Food Drive Results", "We supported 1,200 families thanks to generous donors."),
            ArticleUi("a2", "Volunteer Spotlight", "Meet Ayanda, a volunteer who leads weekend tutoring sessions."),
            ArticleUi("a3", "New Partnership Announced", "We’re partnering with LocalMart to reduce food waste.")
        )
        _stats.value = ImpactStats(beneficiaries = 1200, volunteers = 85, projects = 12)
    }
}
