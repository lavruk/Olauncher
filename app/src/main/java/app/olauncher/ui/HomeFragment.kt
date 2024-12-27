package app.olauncher.ui

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.olauncher.MainViewModel
import app.olauncher.R
import app.olauncher.data.AppModel
import app.olauncher.data.Constants
import app.olauncher.data.Prefs

import app.olauncher.helper.appUsagePermissionGranted
import app.olauncher.helper.dpToPx
import app.olauncher.helper.expandNotificationDrawer
import app.olauncher.helper.getChangedAppTheme
import app.olauncher.helper.getUserHandleFromString
import app.olauncher.helper.isPackageInstalled
import app.olauncher.helper.openAlarmApp
import app.olauncher.helper.openCalendar
import app.olauncher.helper.openCameraApp
import app.olauncher.helper.openDialerApp
import app.olauncher.helper.openSearch
import app.olauncher.helper.setPlainWallpaperByTheme
import app.olauncher.helper.showToast
import app.olauncher.listener.OnSwipeTouchListener
import app.olauncher.listener.ViewSwipeTouchListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import app.olauncher.databinding.FragmentHomeBinding
import app.olauncher.ui.theme.JetLaggedTheme

class HomeFragment : Fragment(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var prefs: Prefs
    private lateinit var viewModel: MainViewModel
    private lateinit var deviceManager: DevicePolicyManager

//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!

    private var useCompose = true

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        return if (useCompose) {
            return ComposeView(requireContext()).apply {
                setContent {


                }
            }
//        } else {
//            _binding = FragmentHomeBinding.inflate(inflater, container, false)
//            binding.root
//        }
    }

    private fun homeAppLongClicked(i: Int) {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        viewModel = activity?.run {
            ViewModelProvider(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        deviceManager = context?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        initObservers()
        setHomeAlignment(prefs.homeAlignment)
        initSwipeTouchListener()
        initClickListeners()

    }

    override fun onResume() {
        super.onResume()
        populateHomeScreen(false)
        viewModel.isOlauncherDefault()
        if (prefs.showStatusBar) showStatusBar()
        else hideStatusBar()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.lock -> {}
            R.id.clock -> openClockApp()
            R.id.date -> openCalendarApp()
            R.id.setDefaultLauncher -> viewModel.resetLauncherLiveData.call()
            R.id.tvScreenTime -> openScreenTimeDigitalWellbeing()

            else -> {
//                if (view is HomeAppView) {
//                    val appLocation = view.textView.tag.toString().toInt()
//                    homeAppClicked(appLocation)
//                }
            }
        }
    }

    private fun openClockApp() {
        if (prefs.clockAppPackage.isBlank())
            openAlarmApp(requireContext())
        else
            launchApp(
                "Clock",
                prefs.clockAppPackage,
                prefs.clockAppClassName,
                prefs.clockAppUser
            )
    }

    private fun openCalendarApp() {
        if (prefs.calendarAppPackage.isBlank())
            openCalendar(requireContext())
        else
            launchApp(
                "Calendar",
                prefs.calendarAppPackage,
                prefs.calendarAppClassName,
                prefs.calendarAppUser
            )
    }

    override fun onLongClick(view: View): Boolean {
//        if (view is HomeAppView) {
//            val index = view.textView.tag.toString().toInt() -1
//            showAppList(Constants.FLAG_SET_HOME_APP_1 + index, prefs.getAppName(index + 1).isNotEmpty(), true)
//            return true
//        }

        when (view.id) {
            R.id.clock -> {
                showAppList(Constants.FLAG_SET_CLOCK_APP)
                prefs.clockAppPackage = ""
                prefs.clockAppClassName = ""
                prefs.clockAppUser = ""
                return true
            }

            R.id.date -> {
                showAppList(Constants.FLAG_SET_CALENDAR_APP)
                prefs.calendarAppPackage = ""
                prefs.calendarAppClassName = ""
                prefs.calendarAppUser = ""
                return true
            }
        }
        return false
    }

    private fun initObservers() {
//        if (prefs.firstSettingsOpen) {
//            binding.firstRunTips.visibility = View.VISIBLE
//            binding.setDefaultLauncher.visibility = View.GONE
//        } else {
//            binding.firstRunTips.visibility = View.GONE
//        }

        viewModel.refreshHome.observe(viewLifecycleOwner) {
            populateHomeScreen(it)
        }
        viewModel.isOlauncherDefault.observe(viewLifecycleOwner, Observer {
            if (it != true) {
                if (prefs.dailyWallpaper) {
                    prefs.dailyWallpaper = false
                    viewModel.cancelWallpaperWorker()
                }
                prefs.homeBottomAlignment = false
                setHomeAlignment()
            }
//            if (binding.firstRunTips.visibility == View.VISIBLE) return@Observer
//            if (it) binding.setDefaultLauncher.visibility = View.GONE
//            else binding.setDefaultLauncher.visibility = View.VISIBLE
        })
        viewModel.homeAppAlignment.observe(viewLifecycleOwner) {
            setHomeAlignment(it)
        }
        viewModel.toggleDateTime.observe(viewLifecycleOwner) {
            populateDateTime()
        }
        viewModel.screenTimeValue.observe(viewLifecycleOwner) {
//            it?.let { binding.tvScreenTime.text = it }
        }
    }

    private fun initSwipeTouchListener() {
        val context = requireContext()
//        binding.mainLayout.setOnTouchListener(getSwipeGestureListener(context))
//        homeAppViews.forEach { it.setOnTouchListener(getViewSwipeTouchListener(context, it)) }
    }

    private fun initClickListeners() {
//        binding.lock.setOnClickListener(this)
//        binding.clock.setOnClickListener(this)
//        binding.date.setOnClickListener(this)
//        binding.clock.setOnLongClickListener(this)
//        binding.date.setOnLongClickListener(this)
//        binding.setDefaultLauncher.setOnClickListener(this)
//        binding.tvScreenTime.setOnClickListener(this)
    }

    private fun setHomeAlignment(horizontalGravity: Int = prefs.homeAlignment) {
        val verticalGravity = if (prefs.homeBottomAlignment) Gravity.BOTTOM else Gravity.CENTER_VERTICAL
//        binding.homeAppsLayout.gravity = horizontalGravity or verticalGravity
//        binding.dateTimeLayout.gravity = horizontalGravity
    }

    private fun populateDateTime() {
//        binding.dateTimeLayout.isVisible = prefs.dateTimeVisibility != Constants.DateTime.OFF
//        binding.clock.isVisible = Constants.DateTime.isTimeVisible(prefs.dateTimeVisibility)
//        binding.date.isVisible = Constants.DateTime.isDateVisible(prefs.dateTimeVisibility)

//        var dateText = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date())
        val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        var dateText = dateFormat.format(Date())

        if (!prefs.showStatusBar) {
            val battery = (requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager)
                .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if (battery > 0)
                dateText = getString(R.string.day_battery, dateText, battery)
        }
//        binding.date.text = dateText.replace(".,", ",")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun populateScreenTime() {
        if (requireContext().appUsagePermissionGranted().not()) return

        viewModel.getTodaysScreenTime()
//        binding.tvScreenTime.visibility = View.VISIBLE

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val horizontalMargin = if (isLandscape) 64.dpToPx() else 10.dpToPx()
        val marginTop = if (isLandscape) {
            if (prefs.dateTimeVisibility == Constants.DateTime.DATE_ONLY) 36.dpToPx() else 56.dpToPx()
        } else {
            if (prefs.dateTimeVisibility == Constants.DateTime.DATE_ONLY) 45.dpToPx() else 72.dpToPx()
        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = marginTop
            marginStart = horizontalMargin
            marginEnd = horizontalMargin
            gravity = if (prefs.homeAlignment == Gravity.END) Gravity.START else Gravity.END
        }
//        binding.tvScreenTime.layoutParams = params
//        binding.tvScreenTime.setPadding(10.dpToPx())
    }

    private fun populateHomeScreen(appCountUpdated: Boolean) {
        if (appCountUpdated) hideHomeApps()
        populateDateTime()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            populateScreenTime()

        val homeAppsNum = prefs.homeAppsNum
        if (homeAppsNum == 0) return

//        binding.homeAppsLayout.removeAllViews()
//        homeAppViews.clear()

//        for (i in 0 until homeAppsNum) {
//            val homeAppView = HomeAppView(requireContext())
//            homeAppViews.add(homeAppView)
//            binding.homeAppsLayout.addView(homeAppView)
//
//            homeAppView.setOnClickListener(this)
//            homeAppView.setOnLongClickListener(this)
//            homeAppView.textView.tag = i + 1
//
//
//            val appName = prefs.getAppName(i + 1)
//            val appPackage = prefs.getAppPackage(i + 1)
//            val appUser = prefs.getAppUser(i + 1)
//
//            if (!setHomeAppText(homeAppView, appName, appPackage, appUser)) {
//                prefs.setAppName(i + 1, "")
//                prefs.setAppPackage(i + 1, "")
//            }
//        }
    }

//    private fun setHomeAppText(homeAppView: HomeAppView, appName: String, packageName: String, userString: String): Boolean {
//        if (isPackageInstalled(requireContext(), packageName, userString)) {
//            homeAppView.textView.text = appName
//            return true
//        }
//        homeAppView.textView.text = ""
//        return false
//    }

    private fun hideHomeApps() {
//        binding.homeAppsLayout.removeAllViews()
    }

    private fun homeAppClicked(location: Int) {
        if (prefs.getAppName(location).isEmpty()) {
            showLongPressToast()
        } else {
            launchApp(
                prefs.getAppName(location),
                prefs.getAppPackage(location),
                prefs.getAppActivityClassName(location),
                prefs.getAppUser(location)
            )
        }
    }

    private fun launchApp(appName: String, packageName: String, activityClassName: String?, userString: String) {
//        viewModel.selectedApp(
//            AppModel(
//                appName,
//                null,
//                packageName,
//                activityClassName,
//                false,
//                getUserHandleFromString(requireContext(), userString)
//            ),
//            Constants.FLAG_LAUNCH_APP
//        )
    }

    private fun showAppList(flag: Int, rename: Boolean = false, includeHiddenApps: Boolean = false) {
        viewModel.getAppList(includeHiddenApps)
        try {
            findNavController().navigate(
                R.id.action_mainFragment_to_appListFragment,
                bundleOf(
                    Constants.Key.FLAG to flag,
                    Constants.Key.RENAME to rename
                )
            )
        } catch (e: Exception) {
            findNavController().navigate(
                R.id.appListFragment,
                bundleOf(
                    Constants.Key.FLAG to flag,
                    Constants.Key.RENAME to rename
                )
            )
            e.printStackTrace()
        }
    }

    private fun swipeDownAction() {
        when (prefs.swipeDownAction) {
            Constants.SwipeDownAction.SEARCH -> openSearch(requireContext())
            else -> expandNotificationDrawer(requireContext())
        }
    }

    private fun openSwipeRightApp() {
        if (!prefs.swipeRightEnabled) return
        if (prefs.appPackageSwipeRight.isNotEmpty())
            launchApp(
                prefs.appNameSwipeRight,
                prefs.appPackageSwipeRight,
                prefs.appActivityClassNameRight,
                prefs.appUserSwipeRight
            )
        else openDialerApp(requireContext())
    }

    private fun openSwipeLeftApp() {
        if (!prefs.swipeLeftEnabled) return
        if (prefs.appPackageSwipeLeft.isNotEmpty())
            launchApp(
                prefs.appNameSwipeLeft,
                prefs.appPackageSwipeLeft,
                prefs.appActivityClassNameSwipeLeft,
                prefs.appUserSwipeLeft
            )
        else openCameraApp(requireContext())
    }

    private fun lockPhone() {
        requireActivity().runOnUiThread {
            try {
                deviceManager.lockNow()
            } catch (e: SecurityException) {
                requireContext().showToast(getString(R.string.please_turn_on_double_tap_to_unlock), Toast.LENGTH_LONG)
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
            } catch (e: Exception) {
                requireContext().showToast(getString(R.string.launcher_failed_to_lock_device), Toast.LENGTH_LONG)
                prefs.lockModeOn = false
            }
        }
    }

    private fun showStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            requireActivity().window.insetsController?.show(WindowInsets.Type.statusBars())
        else
            @Suppress("DEPRECATION", "InlinedApi")
            requireActivity().window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }

    private fun changeAppTheme() {
        if (prefs.dailyWallpaper.not()) return
        val changedAppTheme = getChangedAppTheme(requireContext(), prefs.appTheme)
        prefs.appTheme = changedAppTheme
        if (prefs.dailyWallpaper) {
            setPlainWallpaperByTheme(requireContext(), changedAppTheme)
            viewModel.setWallpaperWorker()
        }
        requireActivity().recreate()
    }

    private fun openScreenTimeDigitalWellbeing() {
        val intent = Intent()
        try {
            intent.setClassName(
                Constants.DIGITAL_WELLBEING_PACKAGE_NAME,
                Constants.DIGITAL_WELLBEING_ACTIVITY
            )
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                intent.setClassName(
                    Constants.DIGITAL_WELLBEING_SAMSUNG_PACKAGE_NAME,
                    Constants.DIGITAL_WELLBEING_SAMSUNG_ACTIVITY
                )
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLongPressToast() = requireContext().showToast(getString(R.string.long_press_to_select_app))

    private fun textOnClick(view: View) = onClick(view)

    private fun textOnLongClick(view: View) = onLongClick(view)

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                openSwipeLeftApp()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                openSwipeRightApp()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                showAppList(Constants.FLAG_LAUNCH_APP)
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                swipeDownAction()
            }

            override fun onLongClick() {
                super.onLongClick()
                try {
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                    viewModel.firstOpen(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onDoubleClick() {
                super.onDoubleClick()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//                    binding.lock.performClick()
                else if (prefs.lockModeOn)
                    lockPhone()
            }

            override fun onClick() {
                super.onClick()
                viewModel.checkForMessages.call()
            }
        }
    }

    private fun getViewSwipeTouchListener(context: Context, view: View): View.OnTouchListener {
        return object : ViewSwipeTouchListener(context, view) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                openSwipeLeftApp()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                openSwipeRightApp()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                showAppList(Constants.FLAG_LAUNCH_APP)
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                swipeDownAction()
            }

            override fun onLongClick(view: View) {
                super.onLongClick(view)
                textOnLongClick(view)
            }

            override fun onClick(view: View) {
                super.onClick(view)
                textOnClick(view)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}