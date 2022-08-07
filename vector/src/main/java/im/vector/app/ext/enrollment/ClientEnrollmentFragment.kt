package im.vector.app.ext.enrollment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.facebook.imagepipeline.transcoder.JpegTranscoderUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.hideKeyboardDrop
import im.vector.app.core.extensions.showPassword
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsFragmentClientEnrollmentBinding
import im.vector.app.ext.data.model.ClientRegistration
import im.vector.app.ext.data.model.Policies
import im.vector.app.ext.data.type.CustomPair
import im.vector.app.ext.utils.*
import im.vector.app.features.login.LoginAction
import im.vector.app.features.login.LoginViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Register a new client
 */
class ClientEnrollmentFragment @Inject constructor() :
    VectorBaseFragment<GlobitsFragmentClientEnrollmentBinding>() {

    private val loginViewModel: LoginViewModel by activityViewModel()

    private var passwordShown = false
    private var repeatPasswordShown = false

    private val viewModel: ClientEnrollmentViewModel by activityViewModel()

    private lateinit var profile: ClientRegistration
    private lateinit var policies: Policies

    // service types
    private lateinit var serviceTypeAdapter: ArrayAdapter<CustomPair<Int>>
    private val serviceTypes = mutableListOf<CustomPair<Int>>()

    private var hasError: Boolean = false


    companion object {
        const val CLIENT_REGISTRATION_TAG = "im.vector.app.ext.enrollment.ClientEnrollmentFragment"
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsFragmentClientEnrollmentBinding {
        return GlobitsFragmentClientEnrollmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickHandlers()
        setupPasswordsReveal()
        views.repeatPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


    }

    override fun invalidate() = withState(viewModel) {
        when (it.asyncPreRegistration) {
            is Success -> {
                val result = it.asyncPreRegistration.invoke()
                if (result.data != null && result.data == false && !it.isSubmitResgister) {
                    views.registerError.visibility = View.GONE
                    submit(checkDuplicateSuccessful = true)

                } else {
                    views.registerError.visibility = View.VISIBLE
                }
            }
            else -> Unit
        }
        if (it.asyncPolicies is Success) {
            if (!this::policies.isInitialized) {
                it.asyncPolicies.invoke().let {
                    policies = it[0]
                }
            }


        }
    }

    private fun updatepolicies(data: Policies): Policies {
        return data
    }

    override fun onResume() {
        super.onResume()
        views.fullname.style(views.fullnameTil, requireActivity())
        views.serviceType.style(views.serviceTypeTil, requireActivity())
        views.username.style(views.usernameTil, requireActivity())
        views.emailAddress.style(views.emailAddressTil, requireActivity())
        views.password.style(views.passwordTil, requireActivity())
        views.repeatPassword.style(views.repeatPasswordTil, requireActivity())
    }
    private fun createPolociesDialog(data: Policies) {
        Log.e("TAG", data.content.toString())
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout._policies_dialog)
        var btnClose = dialog.findViewById<Button>(R.id.dialog_ok_close)
        var content = dialog.findViewById<TextView>(R.id.dialog_content)

        var toolbar = dialog.findViewById<Toolbar>(R.id.dialog_toolbar)

        toolbar.title = data.name
        content.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(data.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(data.content)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    ///////////////////////////////////// PRIVATE ////////////////////////////////////

    private fun setupViews() {
        // service types
        serviceTypeAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout._globits_simple_list_item2,
                serviceTypes
            )
        views.serviceType.apply {
            inputType = InputType.TYPE_NULL
            imeOptions = EditorInfo.IME_ACTION_DONE
            showSoftInputOnFocus = false
            onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                views.serviceType.hideKeyboard(requireActivity())
            }
            onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                views.serviceType.hideKeyboard(requireActivity())
            }
            hideKeyboardDrop(requireActivity())
            setAdapter(serviceTypeAdapter)
        }

        updateServiceTypes()

        //check checkbox for button register
        views.cbAcceptRule.setOnClickListener {
            views.btnRegister.isEnabled = (it as CompoundButton).isChecked
        }
        viewModel.handle(LoginAction.HandleshowPolicies)

    }

    private fun updateServiceTypes() {
        serviceTypes.clear()
        serviceTypes.addAll(
            listOf(
//                CustomPair(0, getString(R.string.service_type_shi)),
                CustomPair(1, getString(R.string.service_type_paid)),
                CustomPair(2, getString(R.string.service_type_free)),
//                CustomPair(3, getString(R.string.service_type_other))
            )
        )
    }

    private fun setupClickHandlers() {
        views.btnRegister.clickWithThrottle {
            submit()
        }
        views.registerSubtitle.clickWithThrottle {
            loginViewModel.handle(LoginAction.ReturnToLogin)
        }

        views.btnGetpolicies.clickWithThrottle {

            createPolociesDialog(policies)

        }

    }


    private fun setupPasswordsReveal() {
        passwordShown = false
        repeatPasswordShown = false

        views.passwordReveal.setOnClickListener {
            passwordShown = !passwordShown

            renderPasswordField()
        }

        views.repeatPasswordReveal.setOnClickListener {
            repeatPasswordShown = !repeatPasswordShown

            renderRepeatPasswordField()
        }

        renderPasswordField()
        renderRepeatPasswordField()
    }

    private fun renderPasswordField() {
        views.password.showPassword(passwordShown)
        views.passwordReveal.render(passwordShown)
    }

    private fun renderRepeatPasswordField() {
        views.repeatPassword.showPassword(repeatPasswordShown)
        views.repeatPasswordReveal.render(repeatPasswordShown)
    }

    private fun updateDataFromView() {
        profile = ClientRegistration()

        val pos = serviceTypes.indexOfFirst { it.b == views.serviceType.text.toString() }
        profile.serviceType = if (pos >= 0) serviceTypeAdapter.getItem(pos)?.a else null

        profile.fullname = views.fullname.toStringAlt()
//        profile.phoneNumber = views.phoneNumber.toStringAlt()
        profile.emailAddress = views.emailAddress.toStringAlt()
        profile.username = views.username.toStringAlt()
        profile.password = views.password.toStringAlt()
    }

    private fun validateInputs() {
        hasError = false
        validateField(views.fullname, true, 6, views.fullnameTil)
        validateField(views.username, true, 6, views.usernameTil)
        validateField(views.emailAddress, true, -1, views.emailAddressTil)
        validateField(views.password, true, 6, views.passwordTil)
        validateField(views.repeatPassword, true, 6, views.repeatPasswordTil)

        val validUsername: Boolean =
            "^[a-z0-9]+$".toRegex().matches(views.username.toStringAlt().trim());
        if (!validUsername) {
            getString(R.string.validate_username).also { views.usernameTil.error = it }
            hasError = true
        }
        if ("^[A-Z]+$".toRegex().matches(views.username.toStringAlt().trim())) {
            "Bạn cần nhập chữ thường".also { views.usernameTil.error = it }
            hasError = true
        }

        if (!isEmailValid(views.emailAddress.toStringAlt().trim())) {
            getString(R.string.validate_email).also { views.emailAddressTil.error = it }
            hasError = true
        }

        updateDataFromView()

        val repeatedPassword = views.repeatPassword.toStringAlt()

        // validate
        if (profile.serviceType == null
            || profile.fullname.isNullOrEmptyAlt()
            || profile.emailAddress.isNullOrEmptyAlt()
            || profile.username.isNullOrEmptyAlt()
            || profile.password.isNullOrEmptyAlt()
        ) {
            hasError = true
            requireActivity().snackbar(getString(R.string.msg_please_complete_required_fields))
        } else if (!profile.password.equals(repeatedPassword)) {
            hasError = true
            requireActivity().snackbar(getString(R.string.passwords_do_not_match))
        }
    }

    private fun validateField(
        textField: TextInputEditText,
        required: Boolean,
        min: Int,
        input: TextInputLayout
    ) {
        if (required && textField.toStringAlt().isNullOrEmptyAlt()) {
            getString(R.string.msg_required_field).also { input.error = it }
            hasError = true
        }
        if (min != -1 && (!textField.toStringAlt()
                .isNullOrEmptyAlt() && textField.toStringAlt().length < min)
        ) {
            if (textField == views.username) {
                "Tên đăng nhập phải trên ${min} ki tu".also { input.error = it }
            } else
                getString(R.string.msg_validate_min_field).also { input.error = it }

            hasError = true
        }
    }

    private fun submit(checkDuplicateSuccessful: Boolean? = false) {

        validateInputs()

        if (hasError) {
            return
        }

        if (checkDuplicateSuccessful == true) {
            viewModel.handle(LoginAction.SubmitEnrollClient(profile))
        } else {
            viewModel.handle(LoginAction.CheckDuplicate4EnrollClient(profile))
        }
    }

    private fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(
            email
        )
            .matches()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
