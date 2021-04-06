package ch.epfl.sdp.drone3d

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

/**
 * The activity that allows the user to register
 */
class RegisterActivity : AuthActivity() {

    private lateinit var pseudoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initUI()

        pseudoEditText = findViewById(R.id.pseudoEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        pseudoEditText.setAutofillHints(View.AUTOFILL_HINT_USERNAME)
        emailEditText.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS)
        passwordEditText.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)

        infoText.text = getString(R.string.register_info_default)
    }

    override fun success() {
        // Registration is a success, show Toast and main activity
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(baseContext, R.string.register_success,
                Toast.LENGTH_SHORT).show()
        //TODO Set pseudo
    }

    fun register(@Suppress("UNUSED_PARAMETER") view: View) {
        startProcess(authService.register(emailEditText.text.toString(), passwordEditText.text.toString()),
                R.string.register_fail)
    }

    fun login(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}