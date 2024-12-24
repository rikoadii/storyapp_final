package com.submissionandroid.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.submissionandroid.storyapp.view.map.MapsActivity
import com.submissionandroid.storyapp.R
import com.submissionandroid.storyapp.databinding.ActivityMainBinding
import com.submissionandroid.storyapp.view.ViewModelFactory
import com.submissionandroid.storyapp.view.adapter.StoryAdapter
import com.submissionandroid.storyapp.view.add_story.AddStoryActivity
import com.submissionandroid.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        lifecycleScope.launch {
            showLoading(true)
            try {
                viewModel.getSession().observe(this@MainActivity) { user ->
                    if (user.isLogin) {
                        val adapter = StoryAdapter(user.token)
                        binding.storyRecyclerView.adapter = adapter
                        binding.storyRecyclerView.layoutManager =
                            LinearLayoutManager(this@MainActivity)

                        lifecycleScope.launch {
                            viewModel.getStoriesPaging(user.token).collect { pagingData ->
                                adapter.submitData(pagingData)

                                adapter.addLoadStateListener { loadStates ->
                                    if (adapter.itemCount == 0 && loadStates.refresh is androidx.paging.LoadState.NotLoading) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Data kosong, tidak ada cerita yang ditemukan.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    } else {
                        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                        finish()
                    }
                }
            } catch (e: Exception) {
                showError("Failed to load session: ${e.message}")
            } finally {
                showLoading(false)
            }
        }

        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_more -> {
                showPopupMenu(findViewById(R.id.menu_more))
                return true
            }

            R.id.action_logout -> {
                performLogout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_navigation, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_add_story -> {
                    startActivity(Intent(this, AddStoryActivity::class.java))
                    true
                }

                R.id.nav_map -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }

                R.id.action_logout -> {
                    performLogout()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            showLoading(true)
            viewModel.logout()
            showLoading(false)
            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }
}