package org.sopt.santamanitto

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.sopt.santamanitto.databinding.ActivitySplashBinding
import org.sopt.santamanitto.main.MainActivity
import org.sopt.santamanitto.update.version.Version
import org.sopt.santamanitto.user.signin.SignInActivity
import org.sopt.santamanitto.view.dialog.RoundDialogBuilder

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

	companion object {
		private const val SPLASH_DELAY = 1000L
	}

	private val splashViewModel: SplashViewModel by viewModels()

	private var isDelayDone = false
	private var isDialogShown = false
	private var isActivityTransitioned = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		installSplashScreen()
		setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)

		handleRemoteServerCheck()
	}

	private fun handleRemoteServerCheck() {
		splashViewModel.run {
			lifecycleScope.launch {
				combine(remoteServerCheck, remoteServerCheckMessage) { isCheck, message ->
					isCheck to message
				}.collect { (isCheck, message) ->
					if (isCheck && !isDialogShown && message.isNotEmpty()) {
						isDialogShown = true
						val formattedMessage = message.replace("\\n", "\n")
						RoundDialogBuilder()
							.setContentText(formattedMessage)
							.addHorizontalButton(getString(R.string.splash_network_error_dialog_button)) {
								tryLogin()
								isDialogShown = false
							}
							.build()
							.show(supportFragmentManager, "error")
					} else {
						observeData()
					}
				}
			}
		}
	}

	private fun SplashViewModel.observeData() {
		latestVersion.observe(this@SplashActivity) { latestVersion ->
			if (isDialogShown) {
				return@observe
			} else if (latestVersion == null) {
				return@observe
			} else if (latestVersion.compare(BuildConfig.VERSION_NAME, Version.MAJOR) > 0) {
				showUpdateDialog()
				isDialogShown = true
				return@observe
			} else {
				tryLogin()
			}
		}

		versionCheckFail.observe(this@SplashActivity) {
			if (it && !isDialogShown) {
				// 버전 체크에 실패하더라도 로그인 시도는 해본다. (API 안정성이 보장되지 않음)
				tryLogin()
			}
		}

		loginSuccess.observe(this@SplashActivity) {
			if (!isDialogShown) startNextActivity()
		}

		checkUpdate()
	}

	private fun showUpdateDialog() {
		RoundDialogBuilder()
			.setContentText(getString(R.string.update_dialog_content))
			.addHorizontalButton(getString(R.string.update_dialog_exit)) {
				finish()
			}
			.addHorizontalButton(getString(R.string.update_dialog_update)) {
				goToStore()
			}
			.enableCancel(false)
			.build()
			.show(supportFragmentManager, "update")
	}

	private fun goToStore() {
		val intent = Intent(Intent.ACTION_VIEW).apply {
			data = Uri.parse(
				"https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
			)
			setPackage("com.android.vending")
		}
		startActivity(intent)
		finish()
	}

	private fun tryLogin() {
		Handler(Looper.getMainLooper()).postDelayed({
			isDelayDone = true
			startNextActivity()
		}, SPLASH_DELAY)

		splashViewModel.login()
	}

	private fun startNextActivity() {
		val loginState = splashViewModel.loginSuccess.value

		if (isDelayDone && loginState != SplashViewModel.LoginState.WAITING && !isDialogShown) {
			when (loginState) {
				SplashViewModel.LoginState.SUCCESS -> {
					if (isActivityTransitioned) return
					isActivityTransitioned = true
					startActivity(Intent(this@SplashActivity, MainActivity::class.java))
					finish()
				}

				SplashViewModel.LoginState.FAIL -> {
					if (isActivityTransitioned) return
					isActivityTransitioned = true
					startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
					finish()
				}

				else -> {
					RoundDialogBuilder()
						.setContentText(getString(R.string.splash_network_error_dialog), false)
						.addHorizontalButton(getString(R.string.splash_network_error_dialog_button)) {
							finish()
						}
						.build()
						.show(supportFragmentManager, "error")
				}
			}
		}
	}
}
