package com.silatsaktistudios.shreddit.supers

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable


abstract class ReactiveFragment : Fragment() {
    lateinit var viewModel: ViewModel
    val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initSubs()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    abstract fun initSubs()
    abstract fun initViewModel()
}