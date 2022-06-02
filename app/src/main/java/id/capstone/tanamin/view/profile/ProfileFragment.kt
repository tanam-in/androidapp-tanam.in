package id.capstone.tanamin.view.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.capstone.tanamin.R
import id.capstone.tanamin.data.Result
import id.capstone.tanamin.data.local.datastore.LoginPreferences
import id.capstone.tanamin.data.local.datastore.PreferencesViewModel
import id.capstone.tanamin.data.local.datastore.PreferencesViewModelFactory
import id.capstone.tanamin.data.remote.response.ProfileResponse
import id.capstone.tanamin.data.remote.response.User
import id.capstone.tanamin.databinding.CustomAlertLogoutBinding
import id.capstone.tanamin.databinding.FragmentProfileBinding
import id.capstone.tanamin.view.MainActivity
import id.capstone.tanamin.view.ViewModelFactory
import id.capstone.tanamin.view.login.LoginActivity
import id.capstone.tanamin.view.profileedit.ProfileEditActivity

class ProfileFragment : Fragment() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userSession")
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var liveData : LiveData<Result<ProfileResponse>>
    private lateinit var liveDataStore : LiveData<Int>
    private lateinit var user:User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        setupViewModel()
        getProfileUser()
        binding.logoutButton.setOnClickListener{
            showDialogLogout()
        }

        binding.editDataButton .setOnClickListener{
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            intent.putExtra(PROFILE_USER_EXTRA,user)
            startActivity(intent)
        }
    }

    private fun setupViewModel(){
        val pref=LoginPreferences.getInstance(requireContext().dataStore)
        preferencesViewModel = ViewModelProvider(this, PreferencesViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity(), "")
        val profileViewModel: ProfileViewModel by viewModels {
            factory
        }
        this.profileViewModel=profileViewModel
    }

    private fun showDialogLogout() {
        val builder = AlertDialog.Builder(requireContext()).create()
        val bindAlert: CustomAlertLogoutBinding = CustomAlertLogoutBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(bindAlert.root)
        bindAlert.logoutConfirm.setOnClickListener {
            builder.dismiss()
            liveData.removeObservers(requireActivity())
            preferencesViewModel.saveNameUser("DEFAULT_VALUE")
            preferencesViewModel.saveIDUser(0)
            preferencesViewModel.saveTokenUser("DEFAULT_VALUE")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
        bindAlert.cancelButton.setOnClickListener {
            builder.dismiss()
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        getProfileUser()
    }

    private fun getProfileUser(){
        val profileMap: HashMap<String, String> = HashMap()
        liveDataStore = preferencesViewModel.getIDUser()
        liveDataStore.observe(requireActivity()) { userId ->
            profileMap["userid"] = userId.toString()
            liveData = profileViewModel.getProfileUser(profileMap)
            liveData.observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.loadingList3.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            user=result.data.data.user
                            val success = "${result.data.data.finish ?: "0"} Selesai"
                            val process = "${result.data.data.progress ?: "0"} Proses"
                            binding.loadingList3.visibility = View.GONE
                            binding.textView3.text=result.data.data.user.name
                            binding.textView4.text=result.data.data.user.email
                            binding.textView5.text= if(result.data.data.user.age != null && result.data.data.user.age != 0)
                                result.data.data.user.age.toString() else "Tidak ada data"
                            binding.textView6.text=if(result.data.data.user.address != null && result.data.data.user.address != "dikosongkan")
                                result.data.data.user.address else "Tidak ada data"
                            binding.successCount.text=success
                            binding.processCount.text=process
                            Glide.with(this)
                                .load(result.data.data.user.profilePicture)
                                .placeholder(R.drawable.ic_profileuser_illustration)
                                .error(R.drawable.ic_profileuser_illustration)
                                .into(binding.classImage2)
                            liveData.removeObservers(requireActivity())
                            liveDataStore.removeObservers(requireActivity())
                        }
                        is Result.Error -> {
                            binding.loadingList3.visibility = View.GONE
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_error_24)?.let {
                                (requireActivity() as MainActivity).showDialog(result.error, it)
                            }
                            liveData.removeObservers(requireActivity())
                            liveDataStore.removeObservers(requireActivity())
                        }
                    }
                }
            }
        }
    }
    companion object{
        const val PROFILE_USER_EXTRA = "profile_user"
    }
}