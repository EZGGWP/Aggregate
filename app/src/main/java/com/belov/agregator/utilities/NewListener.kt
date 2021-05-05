package com.belov.agregator.utilities

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.belov.agregator.profile.ProfileBase
import java.io.Serializable

class NewListener(val prof: ProfileBase): SwipeRefreshLayout.OnRefreshListener, Serializable {
    override fun onRefresh() {
        prof.app.githubController.clearData()
        prof.authenticateSpotify()
    }
}