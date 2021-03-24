package ch.epfl.sdp.drone3d

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sdp.drone3d.auth.AuthenticationService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * The activity that creates the main menu
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
    }

    private fun refresh() {
        val loginButton: Button = findViewById(R.id.log_in_button)
        val logoutButton: Button = findViewById(R.id.log_out_button)

        if (authService.hasActiveSession()) {
            logoutButton.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
        } else {
            logoutButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    /**
     * Go to LoginActivity when log_in_button is clicked
     */
    fun goToLogin(@Suppress("UNUSED_PARAMETER") view: View) {
        open(LoginActivity::class)
    }

    /**
     * Go to LoginActivity when log_in_button is clicked
     */
    fun logout(@Suppress("UNUSED_PARAMETER") view: View) {
        authService.signOut()
        refresh()
    }

    /**
     * Go to ItineraryCreateActivity when create_itinerary_button is clicked
     */
    fun goToItineraryCreate(@Suppress("UNUSED_PARAMETER") view: View) {
        open(ItineraryCreateActivity::class)
    }

    /**
     * Go to MappingMissionSelectionActivity when browse_itinerary_button is clicked
     */
    fun goToMappingMissionSelection(@Suppress("UNUSED_PARAMETER") view: View) {
        if (authService.hasActiveSession()) {
            open(MappingMissionSelectionActivity::class)
        } else {
            goToLogin(view)
        }
    }

    /**
     * Go to DroneConnectActivity when connect_drone_button is clicked
     * TODO : replace TempTestActivity by DroneConnectActivity once it exists
     */
    fun goToDroneConnect(@Suppress("UNUSED_PARAMETER") view: View) {
        open(TempTestActivity::class)
    }

    private fun <T> open(activity: KClass<T>) where T : Activity {
        val intent = Intent(this, activity.java).apply {}
        startActivity(intent)
    }
}